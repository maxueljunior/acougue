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

import br.com.leuxam.acougue.domain.cliente.ClienteService;
import br.com.leuxam.acougue.domain.cliente.DadosAtualizarCliente;
import br.com.leuxam.acougue.domain.cliente.DadosCriarCliente;
import br.com.leuxam.acougue.domain.cliente.DadosDetalhamentoCliente;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
	
	@Autowired
	private ClienteService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoCliente> save(
			@RequestBody @Valid DadosCriarCliente dados,
			UriComponentsBuilder uriBuilder){
		var cliente = service.save(dados);
		var uri = uriBuilder.path("/clientes/{id}").buildAndExpand(cliente.id()).toUri();
		return ResponseEntity.created(uri).body(cliente);
	}
	
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoCliente>> findAll(
			@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable,
			@RequestParam(name = "q", defaultValue = "") String nome){
		var clientes = service.findAllByAtivoAndNome(pageable, nome);
		return ResponseEntity.ok().body(clientes);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCliente> findById(
			@PathVariable(name = "id") Long id){
		var cliente = service.findByIdAndAtivo(id);
		return ResponseEntity.ok().body(cliente);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCliente> update(
			@PathVariable(name = "id") Long id,
			@RequestBody @Valid DadosAtualizarCliente dados){
		var cliente = service.update(id, dados);
		return ResponseEntity.ok().body(cliente);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity ativar(
			@PathVariable(name = "id") Long id){
		service.ativar(id);
		return ResponseEntity.ok().body("Cliente ativado!");
	}
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity desativar(
			@PathVariable(name = "id") Long id) {
		service.desativar(id);
		return ResponseEntity.noContent().build();
	}
}








