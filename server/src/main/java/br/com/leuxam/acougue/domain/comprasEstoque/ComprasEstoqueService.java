package br.com.leuxam.acougue.domain.comprasEstoque;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueData;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueDataRepository;
import jakarta.transaction.Transactional;

@Service
public class ComprasEstoqueService {
	
	private ComprasEstoqueRepository comprasEstoqueRepository;
	
	private ComprasRepository comprasRepository;
	
	private EstoqueRepository estoqueRepository;
	
	private EstoqueDataRepository estoqueDataRepository;
	
	@Autowired
	public ComprasEstoqueService(ComprasEstoqueRepository comprasEstoqueRepository,
			ComprasRepository comprasRepository, EstoqueRepository estoqueRepository,
			EstoqueDataRepository estoqueDataRepository) {
		this.comprasEstoqueRepository = comprasEstoqueRepository;
		this.comprasRepository = comprasRepository;
		this.estoqueRepository = estoqueRepository;
		this.estoqueDataRepository = estoqueDataRepository;
	}

	@Transactional
	public DadosDetalhamentoComprasEstoque create(DadosCriarComprasEstoque dados) {
		if(!comprasRepository.existsById(dados.idCompras())) throw new ExisteException("A compra nº " + dados.idCompras() + " não existe");
		
		var compras = comprasRepository.findById(dados.idCompras());
		
		if(!estoqueRepository.existsById(dados.idEstoque())) throw new ExisteException("O item nº " + dados.idEstoque() + " não existe");
		
		var estoque = estoqueRepository.getReferenceById(dados.idEstoque());
		
		var dataHoje = LocalDate.now();
		var dataValidade = dataHoje.plusMonths(1L);
		var estoqueData = new EstoqueData(null, estoque, dados.quantidade(), dataHoje, dataValidade);
		estoqueDataRepository.save(estoqueData);
		
//		estoque.get().atualizarQuantidadeEstoque(dados.quantidade());
		compras.get().atualizarValorCompra(dados);
		
		var compraEstoque = new ComprasEstoque(null, estoque, compras.get(), dados.precoUnitario(), dados.quantidade());
		
		compraEstoque = comprasEstoqueRepository.save(compraEstoque);
		
		return new DadosDetalhamentoComprasEstoque(compraEstoque);
	}
	
	public List<DadosDetalhamentoComprasEstoque> createList(List<DadosCriarComprasEstoque> dados){
		List<DadosDetalhamentoComprasEstoque> list = new ArrayList<>();
		
		dados.forEach((d) -> {
			var created = create(d);
			list.add(created);
		});
		
		return list;
	}
	
	@Transactional
	public Page<DadosDetalhamentoComprasEstoque> findAll(Pageable pageable) {
		
		var comprasEstoque = comprasEstoqueRepository.findAll(pageable);
		
		return comprasEstoque.map(DadosDetalhamentoComprasEstoque::new);
	}

	@Transactional
	public Page<DadosDetalhamentoComprasEstoque> findById(Long id, Pageable pageable) {
		
		if(!comprasRepository.existsById(id)) throw new ExisteException("A compra nº " + id + " não existe");
		
		var compraEstoque = comprasEstoqueRepository.findByCompras(id, pageable);
		return compraEstoque.map(DadosDetalhamentoComprasEstoque::new);
	}

	@Transactional
	public void delete(Long idCompras, Long idEstoque) {
		if(!comprasRepository.existsById(idCompras)) throw new ExisteException("A compra nº " + idCompras + " não existe");
		
		var compras = comprasRepository.findById(idCompras);
		
		if(!estoqueRepository.existsById(idEstoque)) throw new ExisteException("O item nº " + idEstoque + " não existe");
		
		var estoque = estoqueRepository.getReferenceById(idEstoque);
		
		var compraEstoque = comprasEstoqueRepository.findByComprasAndEstoque(compras.get(), estoque);
		var estoqueData = estoqueDataRepository.findByEstoqueAndQuantidadeAndDataCompra(estoque,
				compraEstoque.get().getQuantidade(), compras.get().getData().toLocalDate());
		
		if(estoqueData.isPresent()) estoqueDataRepository.delete(estoqueData.get());
		
		compras.get().atualizarValorCompra(compraEstoque.get());
//		estoque.get().atualizarQuantidadeEstoque(compraEstoque.get());
		
		if(!compraEstoque.isPresent()) throw new ExisteException("A compra nº " + idCompras + " com o item " + idEstoque + " não existe");
		comprasEstoqueRepository.delete(compraEstoque.get());
	}
}














