package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.fornecedor.DadosAtualizacaoFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.DadosCriarFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.DadosDetalhamentoFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fornecedor")
@Tag(description = "Fornecedores", name = "Fornecedores")
public class FornecedorController {
	
	@Autowired
	private FornecedorService service;
	
	@Operation(summary = "Salvar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoFornecedor.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosDetalhamentoFornecedor> save(
			@RequestBody @Valid DadosCriarFornecedor dados,
			UriComponentsBuilder uriBuilder){
		var fornecedor = service.create(dados);
		var uri = uriBuilder.path("/fornecedor/{id}").buildAndExpand(fornecedor.id()).toUri();
		return ResponseEntity.created(uri).body(fornecedor);
	}
	
	@Operation(summary = "Recuperar todos com busca por razão social")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoFornecedor.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoFornecedor>> findAll(
			@PageableDefault(size = 5, sort = {"razaoSocial"}) Pageable pageable,
			@RequestParam(name = "q", defaultValue = "") String razao){
		var fornecedores = service.searchFornecedorByAtivoTrueAndLikeRazao(razao, pageable);
		return ResponseEntity.ok().body(fornecedores);
	}
	
	@Operation(summary = "Recuperar pelo id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoFornecedor.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoFornecedor> findById(
			@PathVariable(name = "id") Long id){
		var fornecedor = service.findByIdAndAtivoTrue(id);
		return ResponseEntity.ok().body(fornecedor);
	}
	
	@Operation(summary = "Atualizar informações")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoFornecedor.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoFornecedor> update(
			@PathVariable(name = "id") Long id,
			@RequestBody DadosAtualizacaoFornecedor dados){
		var fornecedor = service.update(id, dados);
		return ResponseEntity.ok().body(fornecedor);
	}
	
	@Operation(summary = "Desativar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Desativado", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity desativar(@PathVariable(name = "id") Long id) {
		service.desativar(id);
		return ResponseEntity.noContent().build();
	}
	
	@Operation(summary = "Ativar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario ativado!", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PatchMapping("/{id}")
	public ResponseEntity ativar(@PathVariable(name = "id") Long id) {
		service.ativar(id);
		return ResponseEntity.ok().body("Usuario ativado!");
	}
}























