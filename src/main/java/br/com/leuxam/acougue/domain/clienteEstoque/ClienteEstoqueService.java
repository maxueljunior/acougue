package br.com.leuxam.acougue.domain.clienteEstoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteEstoqueService {
	
	private ClienteEstoqueRepository clienteEstoqueRepository;

	@Autowired
	public ClienteEstoqueService(ClienteEstoqueRepository clienteEstoqueRepository) {
		this.clienteEstoqueRepository = clienteEstoqueRepository;
	}

	
}
