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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/itens/compras")
@Tag(description = "Produtos alocados em Compras", name = "Produtos alocados em Compras")
public class ComprasEstoqueController {
	
	@Autowired
	private ComprasEstoqueService service;
	
	@Operation(summary = "Alocar um Produto em uma Compra")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoComprasEstoque.class))}),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosDetalhamentoComprasEstoque> save(
			@RequestBody @Valid DadosCriarComprasEstoque dados,
			UriComponentsBuilder uriBuilder){
		var compraEstoque = service.create(dados);
		var uri = uriBuilder.path("/itens/compras/{idCompras}/{idEstoque}").buildAndExpand(compraEstoque.idCompras(), compraEstoque.idEstoque()).toUri();
		return ResponseEntity.created(uri).body(compraEstoque);
	}
	
	@Operation(summary = "Recuperar todos os produtos alocados em todas as compras")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoComprasEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoComprasEstoque>> findAll(
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable){
		var comprasEstoque = service.findAll(pageable);
		return ResponseEntity.ok().body(comprasEstoque);
	}
	
	@Operation(summary = "Recuperar todos os produtos alocados em apenas uma compra")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoComprasEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<Page<DadosDetalhamentoComprasEstoque>> findByIdCompras(
			@PathVariable(name = "id") Long id,
			@PageableDefault(size = 10, sort = {"estoque"}) Pageable pageable){
		var compraEstoque = service.findById(id, pageable);
		return ResponseEntity.ok().body(compraEstoque);
	}
	
	@Operation(summary = "Recuperar todos os produtos alocados em apenas uma compra")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Removido" ,content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@DeleteMapping("/{idCompras}/{idEstoque}")
	public ResponseEntity delete(
			@PathVariable(name = "idCompras") Long idCompras,
			@PathVariable(name = "idEstoque") Long idEstoque) {
		service.delete(idCompras, idEstoque);
		return ResponseEntity.noContent().build();
	}
}














