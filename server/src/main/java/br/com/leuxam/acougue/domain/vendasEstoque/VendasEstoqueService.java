package br.com.leuxam.acougue.domain.vendasEstoque;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoqueData.DadosDetalhamentoEstoqueData;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueDataRepository;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class VendasEstoqueService {
	
	private VendasEstoqueRepository vendasEstoqueRepository;
	
	private VendasRepository vendasRepository;
	
	private EstoqueRepository estoqueRepository;
	
	private ComprasRepository comprasRepository;
	
	private ClienteEstoqueRepository clienteEstoqueRepository;
	
	private EstoqueDataRepository estoqueDataRepository;
	
	@Autowired
	public VendasEstoqueService(VendasEstoqueRepository vendasEstoqueRepository,
			VendasRepository vendasRepository, EstoqueRepository estoqueRepository,
			ComprasRepository comprasRepository, ClienteEstoqueRepository clienteEstoqueRepository,
			EstoqueDataRepository estoqueDataRepository) {
		this.vendasEstoqueRepository = vendasEstoqueRepository;
		this.vendasRepository = vendasRepository;
		this.estoqueRepository = estoqueRepository;
		this.comprasRepository = comprasRepository;
		this.clienteEstoqueRepository = clienteEstoqueRepository;
		this.estoqueDataRepository = estoqueDataRepository;
	}

	@Transactional
	public DadosDetalhamentoVendaEstoque create(DadosCriarVendaEstoque dados) {
		
		if(!vendasRepository.existsById(dados.idVendas())) throw new ExisteException("A venda nº " + dados.idVendas() + " não existe");
		
		if(!estoqueRepository.existsById(dados.idEstoque())) throw new ExisteException("O item nº " + dados.idEstoque() + " não existe");
		
		var vendas = vendasRepository.findById(dados.idVendas());
		var estoque = estoqueRepository.findById(dados.idEstoque());
		var estoqueData = estoqueDataRepository.findByEstoqueAndDataCompra(estoque.get(), dados.dataEstoque());
		
		if(!estoqueData.isPresent()) throw new ExisteException("O item " + dados.idEstoque() + " na data " + dados.dataEstoque() + " não existe");
		
		estoqueData.get().atualizarQuantidade(dados.quantidade());
		
		if(estoqueData.get().getQuantidade() <= 0) estoqueDataRepository.delete(estoqueData.get());
		
		vendas.get().atualizar(dados);
//		estoque.get().atualizar(dados);
		
		var vendasEstoque = new VendasEstoque(dados, vendas.get(), estoque.get());
		vendasEstoqueRepository.save(vendasEstoque);
		
		var dataRecente = comprasRepository.searchDataRecente(estoque.get());
		var precoRecente = comprasRepository.searchPrecoRecente(estoque.get(), dataRecente);
		
		var lucratividade = (dados.valorUnitario().divide(precoRecente,2, RoundingMode.HALF_UP)).subtract(BigDecimal.ONE)
				.multiply(new BigDecimal("100"));
		
		var clienteLucratividade = clienteEstoqueRepository.findByClienteAndEstoque(
				vendas.get().getCliente(), estoque.get());
		
		clienteLucratividade.atualizar(lucratividade);
		
		return new DadosDetalhamentoVendaEstoque(vendasEstoque);
	}
	
	@Transactional
	public Page<DadosDetalhamentoVendaEstoque> findAll(Pageable pageable) {
		var vendasEstoque = vendasEstoqueRepository.findAll(pageable);
		return vendasEstoque.map(DadosDetalhamentoVendaEstoque::new);
	}
	
	@Transactional
	public Page<DadosDetalhamentoVendaEstoque> findById(Long id, Pageable pageable) {
		if(!vendasRepository.existsById(id)) throw new ExisteException("A venda nº " + id + " não existe");
		
		var vendaEstoque = vendasEstoqueRepository.findByVendas(id, pageable);
		
		return vendaEstoque.map(DadosDetalhamentoVendaEstoque::new);
	}
	
	@Transactional
	public void delete(Long idVendas, Long idEstoque) {
		if(!vendasRepository.existsById(idVendas)) throw new ExisteException("A venda nº " + idVendas + " não existe");
		
		if(!estoqueRepository.existsById(idEstoque)) throw new ExisteException("O item nº " + idEstoque + " não existe");
		
		var vendas = vendasRepository.findById(idVendas);
		var estoque = estoqueRepository.findById(idEstoque);
		
		var vendaEstoque = vendasEstoqueRepository.findByVendasAndEstoque(vendas.get(), estoque.get());
		
		if(!vendaEstoque.isPresent()) throw new ExisteException("A venda nº " + idVendas + " não contem o produto nº " + idEstoque);
		
		vendas.get().atualizar(vendaEstoque.get());
//		estoque.get().atualizar(vendaEstoque.get());
		
		vendasEstoqueRepository.delete(vendaEstoque.get());
	}
	
	@Transactional
	public DadosDetalhamentoVendaEstoque update(Long idVendas, Long idEstoque,
			DadosAtualizarVendaEstoque dados) {
		if(!vendasRepository.existsById(idVendas)) throw new ExisteException("A venda nº " + idVendas + " não existe");
		
		if(!estoqueRepository.existsById(idEstoque)) throw new ExisteException("O item nº " + idEstoque + " não existe");
		
		var vendas = vendasRepository.findById(idVendas);
		var estoque = estoqueRepository.findById(idEstoque);
		
		var vendaEstoque = vendasEstoqueRepository.findByVendasAndEstoque(vendas.get(), estoque.get());
		
		var quantidadeAntiga = vendaEstoque.get().getQuantidade();
		
		vendaEstoque.get().atualizar(dados);
		
		var estoqueData = estoqueDataRepository.findByEstoqueAndDataCompra(estoque.get(), dados.dataEstoque());
		if(!estoqueData.isPresent()) throw new ExisteException("O item " + idEstoque + " na data " + dados.dataEstoque() + " não existe");
		
		estoqueData.get().atualizarQuantidade(dados.quantidade(), quantidadeAntiga);
		
		if(estoqueData.get().getQuantidade() <= 0) estoqueDataRepository.delete(estoqueData.get());
		
		vendas.get().atualizar(vendaEstoque.get());
		
		var dataRecente = comprasRepository.searchDataRecente(estoque.get());
		var precoRecente = comprasRepository.searchPrecoRecente(estoque.get(), dataRecente);
		
		var lucratividade = (dados.valorUnitario().divide(precoRecente,2, RoundingMode.HALF_UP)).subtract(BigDecimal.ONE)
				.multiply(new BigDecimal("100"));
		
		var clienteLucratividade = clienteEstoqueRepository.findByClienteAndEstoque(
				vendas.get().getCliente(), estoque.get());
		
		clienteLucratividade.atualizar(lucratividade);
		
		return new DadosDetalhamentoVendaEstoque(vendaEstoque.get());
	}
	
	public List<DadosDetalhamentoEstoqueData> findAllDateWithProduct(Long id){
		
		var product = estoqueRepository.findById(id);
		
		if(!product.isPresent()) throw new ExisteException("O item " + id + " não existe");
		
		var estoqueData = estoqueDataRepository.findByEstoque(product.get());
		
//		var estoqueData = estoqueDataRepository.findBy
		return estoqueData.stream().map(DadosDetalhamentoEstoqueData::new).collect(Collectors.toList());
	}
	
	@Transactional
	public List<DadosDetalhamentoVendaEstoque> createList(List<DadosCriarVendaEstoque> dados) {
		List<DadosDetalhamentoVendaEstoque> lista = new ArrayList<>();
		
		dados.forEach((d) -> {
			var dado = create(d);
			lista.add(dado);
		});
		
		return lista;
	}
}


















