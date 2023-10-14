package br.com.leuxam.acougue.domain.comprasEstoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class ComprasEstoqueService {
	
	private ComprasEstoqueRepository comprasEstoqueRepository;
	
	@Autowired
	public ComprasEstoqueService(ComprasEstoqueRepository comprasEstoqueRepository) {
		this.comprasEstoqueRepository = comprasEstoqueRepository;
	}

	public DadosDetalhamentoComprasEstoque create(DadosCriarComprasEstoque dados) {
		
		return null;
	}
}
