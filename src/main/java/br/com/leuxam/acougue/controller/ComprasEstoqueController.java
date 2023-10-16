package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueService;
import br.com.leuxam.acougue.domain.comprasEstoque.DadosCriarComprasEstoque;
import br.com.leuxam.acougue.domain.comprasEstoque.DadosDetalhamentoComprasEstoque;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/itens/compras")
public class ComprasEstoqueController {
	
	@Autowired
	private ComprasEstoqueService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoComprasEstoque> save(
			@RequestBody @Valid DadosCriarComprasEstoque dados,
			UriComponentsBuilder uriBuilder){
		var compraEstoque = service.create(dados);
		var uri = uriBuilder.path("/itens/compras/{id}").buildAndExpand(compraEstoque.idCompras()).toUri();
		return ResponseEntity.ok().body(compraEstoque);
	}
}
