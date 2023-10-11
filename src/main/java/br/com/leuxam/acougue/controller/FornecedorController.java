package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.fornecedor.DadosCriarFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.DadosDetalhamentoFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/fornecedor")
public class FornecedorController {
	
	@Autowired
	private FornecedorService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoFornecedor> save(
			@RequestBody @Valid DadosCriarFornecedor dados,
			UriComponentsBuilder uriBuilder){
		var fornecedor = service.create(dados);
		var uri = uriBuilder.path("/fornecedor/{id}").buildAndExpand(fornecedor.id()).toUri();
		return ResponseEntity.created(uri).body(null);
	}
}
