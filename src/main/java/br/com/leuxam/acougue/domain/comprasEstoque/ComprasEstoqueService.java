package br.com.leuxam.acougue.domain.comprasEstoque;

import org.springframework.beans.factory.annotation.Autowired;
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
		
		var compras = comprasRepository.getReferenceById(dados.idCompras());
		
		if(!estoqueRepository.existsById(dados.idEstoque())) throw new ExisteException("O item nº " + dados.idEstoque() + " não existe");
		
		var estoque = estoqueRepository.getReferenceById(dados.idEstoque());
		
		var compraEstoque = new ComprasEstoque(null, estoque, compras, dados.precoUnitario(), dados.quantidade());
		
		comprasEstoqueRepository.save(compraEstoque);
		
		return new DadosDetalhamentoComprasEstoque(compraEstoque);
	}
}
