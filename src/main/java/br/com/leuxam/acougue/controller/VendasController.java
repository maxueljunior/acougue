package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.compras.DadosCriarVendas;
import br.com.leuxam.acougue.domain.vendas.DadosDetalhamentoVendas;
import br.com.leuxam.acougue.domain.vendas.VendasService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private VendasService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoVendas> save(
			@RequestBody @Valid DadosCriarVendas dados,
			UriComponentsBuilder uriBuilder){
		
	}
}
