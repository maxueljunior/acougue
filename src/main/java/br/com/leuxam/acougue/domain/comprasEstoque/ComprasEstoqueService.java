package br.com.leuxam.acougue.domain.comprasEstoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComprasEstoqueService {
	
	private ComprasEstoqueRepository comprasEstoqueRepository;
	
	@Autowired
	public ComprasEstoqueService(ComprasEstoqueRepository comprasEstoqueRepository) {
		this.comprasEstoqueRepository = comprasEstoqueRepository;
	}
}
