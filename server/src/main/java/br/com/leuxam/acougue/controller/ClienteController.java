package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
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

import br.com.leuxam.acougue.domain.cliente.ClienteService;
import br.com.leuxam.acougue.domain.cliente.DadosAtualizarCliente;
import br.com.leuxam.acougue.domain.cliente.DadosCriarCliente;
import br.com.leuxam.acougue.domain.cliente.DadosDetalhamentoCliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
@Tag(description = "Clientes", name = "Clientes")
public class ClienteController {
	
	@Autowired
	private ClienteService service;
	
	@Operation(summary = "Salvar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoCliente.class))}),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosDetalhamentoCliente> save(
			@RequestBody @Valid DadosCriarCliente dados,
			UriComponentsBuilder uriBuilder){
		var cliente = service.save(dados);
		var uri = uriBuilder.path("/clientes/{id}").buildAndExpand(cliente.id()).toUri();
		return ResponseEntity.created(uri).body(cliente);
	}
	
	@Operation(summary = "Recuperar todos com ou sem busca por nome")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoCliente.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoCliente>> findAll(
			@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable,
			@RequestParam(name = "q", defaultValue = "") String nome){
		var clientes = service.findAllByAtivoAndNome(pageable, nome);
		return ResponseEntity.ok().body(clientes);
	}
	
	@Operation(summary = "Recuperar por id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoCliente.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCliente> findById(
			@PathVariable(name = "id") Long id){
		var cliente = service.findByIdAndAtivo(id);
		return ResponseEntity.ok().body(cliente);
	}
	
	@Operation(summary = "Atualizar informações")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoCliente.class))}),
			@ApiResponse(responseCode = "400", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCliente> update(
			@PathVariable(name = "id") Long id,
			@RequestBody @Valid DadosAtualizarCliente dados){
		var cliente = service.update(id, dados);
		return ResponseEntity.ok().body(cliente);
	}
	
	@Operation(summary = "Ativar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cliente ativado!" ,content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PatchMapping("/{id}")
	public ResponseEntity ativar(
			@PathVariable(name = "id") Long id){
		service.ativar(id);
		return ResponseEntity.ok().body("Cliente ativado!");
	}
	
	@Operation(summary = "Desativar")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Desativado" ,content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity desativar(
			@PathVariable(name = "id") Long id) {
		service.desativar(id);
		return ResponseEntity.noContent().build();
	}
}








