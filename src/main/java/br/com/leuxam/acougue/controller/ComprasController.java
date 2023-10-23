package br.com.leuxam.acougue.controller;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasService;
import br.com.leuxam.acougue.domain.compras.DadosArquivoCompras;
import br.com.leuxam.acougue.domain.compras.DadosAtualizarCompras;
import br.com.leuxam.acougue.domain.compras.DadosCriarCompras;
import br.com.leuxam.acougue.domain.compras.DadosDetalhamentoCompras;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/compras")
public class ComprasController {
	
	@Autowired
	private ComprasService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoCompras> save(
			@RequestBody @Valid DadosCriarCompras dados,
			UriComponentsBuilder uriBuilder){
		var compra = service.create(dados);
		var uri = uriBuilder.path("/compras/{id}").buildAndExpand(compra.id()).toUri();
		return ResponseEntity.created(uri).body(compra);
	}
	
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoCompras>> findAll(
			@PageableDefault(size = 5, sort = {"fornecedor"}) Pageable pageable){
		var compras = service.findAll(pageable);
		return ResponseEntity.ok().body(compras);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCompras> findById(
			@PathVariable(name = "id") Long id){
		var compra = service.findById(id);
		return ResponseEntity.ok().body(compra);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoCompras> update(
			@PathVariable(name = "id") Long id,
			@RequestBody DadosAtualizarCompras dados){
		var compra = service.update(id, dados);
		return ResponseEntity.ok().body(compra);
	}
	
	@PostMapping("/upload-one")
	public ResponseEntity<DadosArquivoCompras> uploadFile(
			@RequestParam("file") MultipartFile file) throws Exception{
		Compras compras = null;
		String downloadUrl = "";
		
		compras = service.saveAttachment(file);
		downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(compras.getId().toString()).toUriString();
		return ResponseEntity.ok().body(new DadosArquivoCompras(compras.getFileName(), downloadUrl, file.getContentType(), file.getSize()));
	}
	
	@PostMapping("/upload-multiple")
	public ResponseEntity<List<DadosArquivoCompras>> uploadMultipleFiles(
			@RequestParam("files") MultipartFile[] files) throws Exception{
		List<DadosArquivoCompras> responseList = new ArrayList<>();
		for (MultipartFile file : files) {
			Compras compras = null;
			String downloadUrl = "";
			
			compras = service.saveAttachment(file);
			downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(compras.getId().toString()).toUriString();
			responseList.add(new DadosArquivoCompras(compras.getFileName(), downloadUrl, file.getContentType(), file.getSize()));
		}
		return ResponseEntity.ok().body(responseList);
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<byte[]> downloadArquivo(
			@PathVariable(name = "id") Long id){
		Compras file = service.findByIdFile(id);
		return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getFileName())
                .body(file.getDat());
	}
	
}






