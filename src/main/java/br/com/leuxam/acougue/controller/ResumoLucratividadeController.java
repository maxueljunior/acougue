package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueService;
import br.com.leuxam.acougue.domain.clienteEstoque.ResumoLucratividade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/resumo/lucratividade")
@Tag(description = "Gerar Resumo Lucratividade por Carnes", name = "Gerar Resumo Lucratividade por Carnes")
public class ResumoLucratividadeController {
	
	@Autowired
	private ClienteEstoqueService service;
	
	@Operation(summary = "Gera um relatório de lucratividade por carnes")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ResumoLucratividade.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<Page<ResumoLucratividade>> resumoLucratividade(
			@PageableDefault(size = 15) Pageable pageable){
		var resumo = service.resumoLucratividade(pageable);
		return ResponseEntity.ok().body(resumo);
	}
}






















