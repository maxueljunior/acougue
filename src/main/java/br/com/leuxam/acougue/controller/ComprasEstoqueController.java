package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
		var uri = uriBuilder.path("/itens/compras/{idCompras}/{idEstoque}").buildAndExpand(compraEstoque.idCompras(), compraEstoque.idEstoque()).toUri();
		return ResponseEntity.created(uri).body(compraEstoque);
	}
	
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoComprasEstoque>> findAll(
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable){
		var comprasEstoque = service.findAll(pageable);
		return ResponseEntity.ok().body(comprasEstoque);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Page<DadosDetalhamentoComprasEstoque>> findByIdCompras(
			@PathVariable(name = "id") Long id,
			@PageableDefault(size = 10, sort = {"estoque"}) Pageable pageable){
		var compraEstoque = service.findById(id, pageable);
		return ResponseEntity.ok().body(compraEstoque);
	}
	
	@DeleteMapping("/{idCompras}/{idEstoque}")
	public ResponseEntity delete(
			@PathVariable(name = "idCompras") Long idCompras,
			@PathVariable(name = "idEstoque") Long idEstoque) {
		service.delete(idCompras, idEstoque);
		return ResponseEntity.noContent().build();
	}
}














