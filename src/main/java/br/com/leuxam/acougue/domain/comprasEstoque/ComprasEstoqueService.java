package br.com.leuxam.acougue.domain.comprasEstoque;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import jakarta.transaction.Transactional;

@Service
public class ComprasEstoqueService {
	
	private ComprasEstoqueRepository comprasEstoqueRepository;
	
	private ComprasRepository comprasRepository;
	
	private EstoqueRepository estoqueRepository;
	
	@Autowired
	public ComprasEstoqueService(ComprasEstoqueRepository comprasEstoqueRepository,
			ComprasRepository comprasRepository, EstoqueRepository estoqueRepository) {
		this.comprasEstoqueRepository = comprasEstoqueRepository;
		this.comprasRepository = comprasRepository;
		this.estoqueRepository = estoqueRepository;
	}

	@Transactional
	public DadosDetalhamentoComprasEstoque create(DadosCriarComprasEstoque dados) {
		if(!comprasRepository.existsById(dados.idCompras())) throw new ExisteException("A compra nº " + dados.idCompras() + " não existe");
		
		var compras = comprasRepository.findById(dados.idCompras());
		
		if(!estoqueRepository.existsById(dados.idEstoque())) throw new ExisteException("O item nº " + dados.idEstoque() + " não existe");
		
		var estoque = estoqueRepository.findById(dados.idEstoque());
		
		estoque.get().atualizarQuantidadeEstoque(dados.quantidade());
		compras.get().atualizarValorCompra(dados);
		
		var compraEstoque = new ComprasEstoque(null, estoque.get(), compras.get(), dados.precoUnitario(), dados.quantidade());
		
		comprasEstoqueRepository.save(compraEstoque);
		
		return new DadosDetalhamentoComprasEstoque(compraEstoque);
	}

	public Page<DadosDetalhamentoComprasEstoque> findAll(Pageable pageable) {
		
		var comprasEstoque = comprasEstoqueRepository.findAll(pageable);
		
		return comprasEstoque.map(DadosDetalhamentoComprasEstoque::new);
	}

	public Page<DadosDetalhamentoComprasEstoque> findById(Long id, Pageable pageable) {
		
		if(!comprasRepository.existsById(id)) throw new ExisteException("A compra nº " + id + " não existe");
		
		var compraEstoque = comprasEstoqueRepository.findByCompras(id, pageable);
		return compraEstoque.map(DadosDetalhamentoComprasEstoque::new);
	}

	public void delete(Long idCompras, Long idEstoque) {
		if(!comprasRepository.existsById(idCompras)) throw new ExisteException("A compra nº " + idCompras + " não existe");
		
		var compras = comprasRepository.findById(idCompras);
		
		if(!estoqueRepository.existsById(idEstoque)) throw new ExisteException("O item nº " + idEstoque + " não existe");
		
		var estoque = estoqueRepository.findById(idEstoque);
		
		var compraEstoque = comprasEstoqueRepository.findByComprasAndEstoque(compras.get(), estoque.get());
		
		compras.get().atualizarValorCompra(compraEstoque.get());
		estoque.get().atualizarQuantidadeEstoque(compraEstoque.get());
		
		if(!compraEstoque.isPresent()) throw new ExisteException("A compra nº " + idCompras + " com o item " + idEstoque + " não existe");
		comprasEstoqueRepository.delete(compraEstoque.get());
	}
}














