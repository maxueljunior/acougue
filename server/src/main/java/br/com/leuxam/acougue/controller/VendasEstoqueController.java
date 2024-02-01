package br.com.leuxam.acougue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.estoqueData.DadosDetalhamentoEstoqueData;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosAtualizarVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosCriarVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosDetalhamentoVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/itens/vendas")
@Tag(name = "Inserção de Produtos na Venda", description = "Inserção de Produtos na Venda")
public class VendasEstoqueController {
	
	@Autowired
	private VendasEstoqueService service;
	
	@Operation(summary = "Alocar um produto na venda")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoVendaEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosDetalhamentoVendaEstoque> save(
			@RequestBody @Valid DadosCriarVendaEstoque dados,
			UriComponentsBuilder uriBuilder){
		var vendaEstoque = service.create(dados);
		var uri = uriBuilder.path("/itens/vendas/{idVendas}/{idEstoque}")
				.buildAndExpand(vendaEstoque.idVendas(), vendaEstoque.idEstoque()).toUri();
		return ResponseEntity.created(uri).body(vendaEstoque);
	}
	
	@Operation(summary = "Recuperar todos os produtos alocados em vendas")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoVendaEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoVendaEstoque>> findAll(
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable){
		var vendasEstoque = service.findAll(pageable);
		return ResponseEntity.ok().body(vendasEstoque);
	}
	
	@Operation(summary = "Recuperar todos os produtos alocados em uma venda")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoVendaEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<Page<DadosDetalhamentoVendaEstoque>> findByIdVendas(
			@PathVariable(name = "id") Long id,
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable){
		var vendaEstoque = service.findById(id, pageable);
		return ResponseEntity.ok().body(vendaEstoque);
	}
	
	@Operation(summary = "Deletar um produto alocado em uma venda")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Excluido" ,content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@DeleteMapping("/{idVendas}/{idEstoque}")
	public ResponseEntity delete(
			@PathVariable(name = "idVendas") Long idVendas,
			@PathVariable(name = "idEstoque") Long idEstoque) {
		service.delete(idVendas, idEstoque);
		return ResponseEntity.noContent().build();
	}
	
	@Operation(summary = "Atualizar um produto alocado em uma venda")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoVendaEstoque.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PutMapping("/{idVendas}/{idEstoque}")
	public ResponseEntity<DadosDetalhamentoVendaEstoque> update(
			@PathVariable(name = "idVendas") Long idVendas,
			@PathVariable(name = "idEstoque") Long idEstoque,
			@RequestBody @Valid DadosAtualizarVendaEstoque dados){
		var vendaEstoque = service.update(idVendas, idEstoque, dados);
		return ResponseEntity.ok().body(vendaEstoque);
	}
	
	@Operation(summary = "Recuperar todas as datas para venda de um determinado Produto")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoEstoqueData.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/datas/{id}")
	public ResponseEntity<List<DadosDetalhamentoEstoqueData>> findAllDates(
			@PathVariable(name = "id") Long id){
		var vendasEstoque = service.findAllDateWithProduct(id);
		return ResponseEntity.ok().body(vendasEstoque);
	}
}
















