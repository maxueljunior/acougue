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
	
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoVendaEstoque>> findAll(
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable){
		var vendasEstoque = service.findAll(pageable);
		return ResponseEntity.ok().body(vendasEstoque);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Page<DadosDetalhamentoVendaEstoque>> findByIdVendas(
			@PathVariable(name = "id") Long id,
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable){
		var vendaEstoque = service.findById(id, pageable);
		return ResponseEntity.ok().body(vendaEstoque);
	}
	
	@DeleteMapping("/{idVendas}/{idEstoque}")
	public ResponseEntity delete(
			@PathVariable(name = "idVendas") Long idVendas,
			@PathVariable(name = "idEstoque") Long idEstoque) {
		service.delete(idVendas, idEstoque);
		return ResponseEntity.noContent().build();
	}
}
















