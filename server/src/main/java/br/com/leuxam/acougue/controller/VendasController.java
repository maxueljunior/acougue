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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendas")
@Tag(description = "Vendas de produto", name = "Vendas de produto")
public class VendasController {
	
	@Autowired
	private VendasService service;
	
	@Operation(summary = "Cria uma venda")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Venda criada",
					content = {@Content(mediaType = "application/json", 
					schema = @Schema(implementation = DadosDetalhamentoVendas.class)) }),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", description = "Cliente não existe", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosDetalhamentoVendas> save(
			@RequestBody @Valid DadosCriarVendas dados,
			UriComponentsBuilder uriBuilder){
		var venda = service.create(dados);
		var uri = uriBuilder.path("/vendas/{id}").buildAndExpand(venda.id()).toUri();
		return ResponseEntity.created(uri).body(venda);
	}
	
	@Operation(summary = "Retorna todas as vendas passando ou não um Cliente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = VendasDTO.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping
	public ResponseEntity<PagedModel<EntityModel<VendasDTO>>> findAll(
			@PageableDefault(size = 10, sort = {"id"}) Pageable pageable,
			@RequestParam(name = "q", defaultValue = "") String nome){
		var vendas = service.findAll(pageable, nome);
		return ResponseEntity.ok().body(vendas);
	}
	
	
	@Operation(summary = "Retorna venda pelo seu numero")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoVendas.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", description = "Venda não encontrada" ,content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoVendas> findById(
			@PathVariable(name = "id") Long id){
		var vendas = service.findById(id);
		return ResponseEntity.ok().body(vendas);
	}
	
	@Operation(summary = "Altera a condição de pagamento")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosDetalhamentoVendas.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", description = "Venda não encontrada" ,content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PatchMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoVendas> update(
			@PathVariable(name = "id") Long id,
			@RequestBody DadosAtualizarVenda dados){
		var venda = service.update(id, dados);
		return ResponseEntity.ok().body(venda);
	}
	
	
	@Operation(summary = "Gera cupom não fiscal")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", headers = @Header(name = "Content-Disposition"),
					content = {@Content(mediaType = "application/pdf",
					schema = @Schema(implementation = Resource.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", description = "Venda não encontrada" ,content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
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
	
	@Operation(summary = "Download do cupom fiscal")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", headers = @Header(name = "Content-Disposition"),
					content = {@Content(mediaType = "application/pdf",
					schema = @Schema(implementation = Resource.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", description = "Venda não contem arquivo" ,content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
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






















