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

@RestController
@RequestMapping("/resumo/lucratividade")
public class ResumoLucratividadeController {
	
	@Autowired
	private ClienteEstoqueService service;
	
	@GetMapping
	public ResponseEntity<Page<ResumoLucratividade>> resumoLucratividade(
			@PageableDefault(size = 15) Pageable pageable){
		var resumo = service.resumoLucratividade(pageable);
		return ResponseEntity.ok().body(resumo);
	}
}






















