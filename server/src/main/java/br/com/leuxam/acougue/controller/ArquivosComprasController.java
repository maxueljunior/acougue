package br.com.leuxam.acougue.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasService;
import br.com.leuxam.acougue.domain.arquivosCompras.DadosDetalhamentoArquivo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/arquivos")
@Tag(description = "Arquivos de Compras", name = "Arquivos de Compras")
public class ArquivosComprasController {

	@Autowired
	private ArquivosComprasService service;
	
	@Operation(summary = "Upload")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoArquivo.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content),
	})
	@PostMapping("/compras/{id}/one")
	public ResponseEntity<DadosDetalhamentoArquivo> uploadOneFile(
			@RequestParam("file") MultipartFile file,
			@PathVariable(name = "id") Long id){
		var arquivo = service.saveArchive(file, id);
		String downloadUrl = "";
		
		downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/arquivos/compras/")
				.path(arquivo.getCompras().getId().toString())
				.path("/download")
				.toUriString();
		
		return ResponseEntity.ok().body(
				new DadosDetalhamentoArquivo(arquivo.getFileName(),
						file.getContentType(), file.getSize(), downloadUrl));
	}
	
	@Operation(summary = "Download")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", headers = @Header(name = "Content-Disposition"),
					content = {@Content(mediaType = "application/pdf",
					schema = @Schema(implementation = Resource.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/compras/{id}/download")
	public ResponseEntity<Resource> downloadFile(
			@PathVariable(name = "id") Long id){
		var file = service.findByIdCompras(id);
		
		ByteArrayResource resource = new ByteArrayResource(file.getData());
		
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=" + file.getFileName())
				.body(resource);
	}
	
//	Conforme conversado com o rodrigo ele só salva apenas um arquivo por compra!
//	Sendo assim não é necessário salvar varios arquivos para uma compra só!
	
//	@PostMapping("/compras/{id}/multiples")
//	public ResponseEntity<List<DadosDetalhamentoArquivo>> uploadMultipleFiles(
//			@RequestParam("files") MultipartFile[] files,
//			@PathVariable(name = "id") Long id){
//		List<DadosDetalhamentoArquivo> response = new ArrayList<>();
//		for(MultipartFile file : files) {
//			var arquivo = service.saveArchive(file, id);
//			String downloadUrl = "";
//			
//			downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
//					.path("/arquivos/compras/").path(arquivo.getCompras().getId().toString()).toUriString();
//			response.add(new DadosDetalhamentoArquivo(arquivo.getFileName(),
//					file.getContentType(), file.getSize(), downloadUrl));
//		}
//		return ResponseEntity.ok().body(response);
//	}

}
