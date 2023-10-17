package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.vendasEstoque.DadosCriarVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosDetalhamentoVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/itens/vendas")
public class VendasEstoqueController {
	
	@Autowired
	private VendasEstoqueService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoVendaEstoque> save(
			@RequestBody @Valid DadosCriarVendaEstoque dados,
			UriComponentsBuilder uriBuilder){
		var vendaEstoque = service.create(dados);
		var uri = uriBuilder.path("/itens/vendas/{idVendas}/{idEstoque}")
				.buildAndExpand(vendaEstoque.idVendas(), vendaEstoque.idEstoque()).toUri();
		return ResponseEntity.created(uri).body(vendaEstoque);
	}
}
