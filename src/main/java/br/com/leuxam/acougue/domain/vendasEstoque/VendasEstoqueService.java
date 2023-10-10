package br.com.leuxam.acougue.domain.vendasEstoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendasEstoqueService {
	
	private VendasEstoqueRepository vendasEstoqueRepository;

	@Autowired
	public VendasEstoqueService(VendasEstoqueRepository vendasEstoqueRepository) {
		this.vendasEstoqueRepository = vendasEstoqueRepository;
	}
}
