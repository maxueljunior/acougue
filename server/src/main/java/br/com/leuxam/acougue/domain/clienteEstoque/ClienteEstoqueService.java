package br.com.leuxam.acougue.domain.clienteEstoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ClienteEstoqueService {
	
	private ClienteEstoqueRepository clienteEstoqueRepository;

	@Autowired
	public ClienteEstoqueService(ClienteEstoqueRepository clienteEstoqueRepository) {
		this.clienteEstoqueRepository = clienteEstoqueRepository;
	}

	@Transactional
	public Page<ResumoLucratividade> resumoLucratividade(Pageable pageable){
		var resumo = clienteEstoqueRepository.gerarResumoLucratividade(pageable);
		return resumo.map(ResumoLucratividade::new);
	}
}
