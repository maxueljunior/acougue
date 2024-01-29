package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.compras.ComprasDTO;
import br.com.leuxam.acougue.domain.compras.ComprasService;
import br.com.leuxam.acougue.domain.compras.DadosAtualizarCompras;
import br.com.leuxam.acougue.domain.compras.DadosCriarCompras;
import br.com.leuxam.acougue.domain.compras.DadosDetalhamentoCompras;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/compras")
@Tag(description = "Compras", name = "Compras")
public class ComprasController {
	
	@Autowired
	private ComprasService service;
	
	@Operation(summary = "Salvar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoCompras.class))}),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosDetalhamentoCompras> save(
			@RequestBody @Valid DadosCriarCompras dados,
			UriComponentsBuilder uriBuilder){
		var compra = service.create(dados);
		var uri = uriBuilder.path("/compras/{id}").buildAndExpand(compra.id()).toUri();
		return ResponseEntity.created(uri).body(compra);
	}
	
	@Operation(summary = "Recuperar todas")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ComprasDTO.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<PagedModel<EntityModel<ComprasDTO>>> findAll(
			@PageableDefault(size = 5, sort = {"fornecedor"}) Pageable pageable,
			@RequestParam(name = "q", defaultValue = "") String razaoSocial){
		var compras = service.findAllByRazaoFornecedor(pageable, razaoSocial);
		
		return ResponseEntity.ok().body(compras);
	}
	
	@Operation(summary = "Recuperar por Id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ComprasDTO.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<ComprasDTO> findById(
			@PathVariable(name = "id") Long id){
		var compra = service.findById(id);
		return ResponseEntity.ok().body(compra);
	}
	
	@Operation(summary = "Atualizar informações")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoCompras.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCompras> update(
			@PathVariable(name = "id") Long id,
			@RequestBody DadosAtualizarCompras dados){
		var compra = service.update(id, dados);
		return ResponseEntity.ok().body(compra);
	}
}






