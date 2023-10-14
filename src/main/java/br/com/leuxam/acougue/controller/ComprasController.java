package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.compras.ComprasService;
import br.com.leuxam.acougue.domain.compras.DadosAtualizarCompras;
import br.com.leuxam.acougue.domain.compras.DadosCriarCompras;
import br.com.leuxam.acougue.domain.compras.DadosDetalhamentoCompras;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/compras")
public class ComprasController {
	
	@Autowired
	private ComprasService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoCompras> save(
			@RequestBody @Valid DadosCriarCompras dados,
			UriComponentsBuilder uriBuilder){
		var compra = service.create(dados);
		var uri = uriBuilder.path("/compras/{id}").buildAndExpand(compra.id()).toUri();
		return ResponseEntity.created(uri).body(compra);
	}
	
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoCompras>> findAll(
			@PageableDefault(size = 5, sort = {"fornecedor"}) Pageable pageable){
		var compras = service.findAll(pageable);
		return ResponseEntity.ok().body(compras);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCompras> findById(
			@PathVariable(name = "id") Long id){
		var compra = service.findById(id);
		return ResponseEntity.ok().body(compra);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCompras> update(
			@PathVariable(name = "id") Long id,
			@RequestBody DadosAtualizarCompras dados){
		var compra = service.update(id, dados);
		return ResponseEntity.ok().body(compra);
	}
}






