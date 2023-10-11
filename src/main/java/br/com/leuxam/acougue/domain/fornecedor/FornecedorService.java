package br.com.leuxam.acougue.domain.fornecedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class FornecedorService {
	
	private FornecedorRepository fornecedorRepository;

	@Autowired
	public FornecedorService(FornecedorRepository fornecedorRepository) {
		this.fornecedorRepository = fornecedorRepository;
	}

	public DadosDetalhamentoFornecedor create(DadosCriarFornecedor dados) {
		return null;
	}
}
