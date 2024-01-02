package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.estoque.DadosAtualizarEstoque;
import br.com.leuxam.acougue.domain.estoque.DadosCriarEstoque;
import br.com.leuxam.acougue.domain.estoque.DadosDetalhamentoEstoque;
import br.com.leuxam.acougue.domain.estoque.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
@Tag(description = "Produtos", name = "Produtos")
public class EstoqueController {
	
	@Autowired
	private EstoqueService service;
	
	@Operation(summary = "Salvar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosDetalhamentoEstoque> save(
			@RequestBody @Valid DadosCriarEstoque dados,
			UriComponentsBuilder uriBuilder){
		var estoque = service.create(dados);
		var uri = uriBuilder.path("/produtos/{id}").buildAndExpand(estoque.id()).toUri();
		return ResponseEntity.created(uri).body(estoque);
	}
	
	@Operation(summary = "Recuperar todos")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoEstoque>> findAll(
			@PageableDefault(size = 5, sort = {"descricao"}) Pageable pageable){
		var estoques = service.findAll(pageable);
		return ResponseEntity.ok().body(estoques);
	}
	
	@Operation(summary = "Recuperar por id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoEstoque> findById(
			@PathVariable(name = "id") Long id){
		var estoque = service.findById(id);
		return ResponseEntity.ok().body(estoque);
	}
	
	@Operation(summary = "Atualizar descrição")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PatchMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoEstoque> update(
			@PathVariable(name = "id") Long id,
			@RequestBody @Valid DadosAtualizarEstoque dados){
		var estoque = service.update(id, dados);
		return ResponseEntity.ok().body(estoque);
	}
}
