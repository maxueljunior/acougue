package br.com.leuxam.acougue.domain.vendasEstoque;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;
import jakarta.transaction.Transactional;

@Service
public class VendasEstoqueService {
	
	private VendasEstoqueRepository vendasEstoqueRepository;
	
	private VendasRepository vendasRepository;
	
	private EstoqueRepository estoqueRepository;
	
	private ComprasRepository comprasRepository;
	
	@Autowired
	public VendasEstoqueService(VendasEstoqueRepository vendasEstoqueRepository,
			VendasRepository vendasRepository, EstoqueRepository estoqueRepository,
			ComprasRepository comprasRepository) {
		this.vendasEstoqueRepository = vendasEstoqueRepository;
		this.vendasRepository = vendasRepository;
		this.estoqueRepository = estoqueRepository;
		this.comprasRepository = comprasRepository;
	}

	@Transactional
	public DadosDetalhamentoVendaEstoque create(DadosCriarVendaEstoque dados) {
		
		if(!vendasRepository.existsById(dados.idVendas())) throw new ExisteException("A venda nº " + dados.idVendas() + " não existe");
		
		if(!estoqueRepository.existsById(dados.idEstoque())) throw new ExisteException("O item nº " + dados.idEstoque() + " não existe");
		
		var vendas = vendasRepository.findById(dados.idVendas());
		var estoque = estoqueRepository.findById(dados.idEstoque());
		
		vendas.get().atualizar(dados);
		estoque.get().atualizar(dados);
		
		var vendasEstoque = new VendasEstoque(dados, vendas.get(), estoque.get());
		vendasEstoqueRepository.save(vendasEstoque);
		
		var dataRecente = comprasRepository.searchDataRecente(estoque.get());
		var precoRecente = comprasRepository.searchPrecoRecente(estoque.get(), dataRecente);
		
		var lucratividade = (dados.valorUnitario().divide(precoRecente,2, RoundingMode.HALF_UP)).subtract(BigDecimal.ONE)
				.multiply(new BigDecimal("100"));
		
		System.out.println(lucratividade);
		return new DadosDetalhamentoVendaEstoque(vendasEstoque);
	}

	public Page<DadosDetalhamentoVendaEstoque> findAll(Pageable pageable) {
		var vendasEstoque = vendasEstoqueRepository.findAll(pageable);
		return vendasEstoque.map(DadosDetalhamentoVendaEstoque::new);
	}

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
		estoque.get().atualizar(vendaEstoque.get());
		
		vendasEstoqueRepository.delete(vendaEstoque.get());
	}
}


















