package br.com.leuxam.acougue.controller;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.itextpdf.text.DocumentException;

import br.com.leuxam.acougue.domain.compras.DadosCriarVendas;
import br.com.leuxam.acougue.domain.vendas.DadosAtualizarVenda;
import br.com.leuxam.acougue.domain.vendas.DadosDetalhamentoVendas;
import br.com.leuxam.acougue.domain.vendas.VendasDTO;
import br.com.leuxam.acougue.domain.vendas.VendasService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private VendasService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoVendas> save(
			@RequestBody @Valid DadosCriarVendas dados,
			UriComponentsBuilder uriBuilder){
		var venda = service.create(dados);
		var uri = uriBuilder.path("/vendas/{id}").buildAndExpand(venda.id()).toUri();
		return ResponseEntity.created(uri).body(venda);
	}
	
//	@GetMapping
//	public ResponseEntity<Page<DadosDetalhamentoVendas>> findAll(
//			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable,
//			@RequestParam(name = "cliente", defaultValue = "0") String idCliente){
//		var vendas = service.findAll(pageable, idCliente);
//		return ResponseEntity.ok().body(vendas);
//	}
	
	@GetMapping
	public ResponseEntity<PagedModel<EntityModel<VendasDTO>>> findAll(
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable,
			@RequestParam(name = "cliente", defaultValue = "0") String idCliente){
		var vendas = service.findAll(pageable, idCliente);
		return ResponseEntity.ok().body(vendas);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoVendas> findById(
			@PathVariable(name = "id") Long id){
		var vendas = service.findById(id);
		return ResponseEntity.ok().body(vendas);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoVendas> update(
			@PathVariable(name = "id") Long id,
			@RequestBody DadosAtualizarVenda dados){
		var venda = service.update(id, dados);
		return ResponseEntity.ok().body(venda);
	}
	
	@GetMapping("/gerar-cupom/{id}")
	public ResponseEntity<Resource> gerarPdf(
			@PathVariable(name = "id") Long id) throws FileNotFoundException, DocumentException{
		var output = service.gerarPdf(id);
		ByteArrayResource resource = new ByteArrayResource(output.toByteArray());
		return ResponseEntity
				.ok()
				.header("Content-Disposition",
						"attachment;filename=venda "+ id +".pdf")
				.body(resource);
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadPdf(
			@PathVariable(name = "id") Long id){
		var vendas = service.findByIdAndArchive(id);
		ByteArrayResource resource = new ByteArrayResource(vendas.getData());
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename="+vendas.getFileName())
				.body(resource);
	}
}
