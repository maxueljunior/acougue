package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.leuxam.acougue.controller.ArquivosComprasController;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosCompras;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasRepository;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasService;
import br.com.leuxam.acougue.domain.arquivosCompras.DadosDetalhamentoArquivo;
import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class ArquivosComprasControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private ArquivosComprasService service;
	
	@MockBean
	private ArquivosComprasRepository arquivosComprasRepository;
	
	@MockBean
	private ComprasRepository comprasRepository;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoArquivo> dadosDetalhamento;
	
	@BeforeAll
	void beforeAll() {
		service = new ArquivosComprasService(arquivosComprasRepository, comprasRepository);
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria fazer o upload do arquivo caso ele exista e retornar codigo HTTP 200")
	void test_cenario01() throws Exception {
		var compras = mockCompras();
		MockMultipartFile file = new MockMultipartFile(
				"file", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "Ola mundo!".getBytes());
		var arquivosCompras = mockArquivosCompras(file);
		
		when(comprasRepository.existsById(1l)).thenReturn(true);
		when(comprasRepository.getReferenceById(1L)).thenReturn(compras);
		when(arquivosComprasRepository.save(any())).thenReturn(arquivosCompras);
		
		String downloadUrl = "";
		
		downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/arquivos/compras/")
				.path(arquivosCompras.getCompras().getId().toString())
				.path("/download")
				.toUriString();
		
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		var result = mvc.perform(multipart("/arquivos/compras/1/one")
							.file(file))
						.andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoArquivo(file.getOriginalFilename(),
				file.getContentType(), file.getSize(), downloadUrl)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(1)).getReferenceById(1L);
		verify(arquivosComprasRepository, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria fazer o upload do arquivo caso ele não exista/nome incorreto e retornar codigo HTTP 400")
	void test_cenario02() throws Exception {
		var compras = mockCompras();
		MockMultipartFile file = new MockMultipartFile(
				"files", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "Ola mundo!".getBytes());
		var arquivosCompras = mockArquivosCompras(file);
		
		when(comprasRepository.existsById(1l)).thenReturn(true);
		when(comprasRepository.getReferenceById(1L)).thenReturn(compras);
		when(arquivosComprasRepository.save(any())).thenReturn(arquivosCompras);
		
		
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mvc.perform(multipart("/arquivos/compras/1/one")
						.file(file))
					.andExpect(status().isBadRequest());
		
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria fazer o download caso exista um arquivo registrado e devolver codigo HTTP 200")
	void test_cenario03() throws Exception {
		var compras = mockCompras();
		MockMultipartFile file = new MockMultipartFile(
				"files", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "Ola mundo!".getBytes());
		var arquivosCompras = mockArquivosCompras(file);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.getReferenceById(1L)).thenReturn(compras);
		when(arquivosComprasRepository.findByCompras(any())).thenReturn(Optional.of(arquivosCompras));
		
		var result = mvc.perform(get("/arquivos/compras/1/download")
					.header("Content-Disposition", "attachment; filename="+arquivosCompras.getFileName())
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isNotBlank();
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(1)).getReferenceById(1L);
		verify(arquivosComprasRepository, times(1)).findByCompras(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria fazer o download caso não exista um arquivo registrado e devolver codigo HTTP 404")
	void test_cenario04() throws Exception {
		var compras = mockCompras();
		MockMultipartFile file = new MockMultipartFile(
				"files", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "Ola mundo!".getBytes());
		var arquivosCompras = mockArquivosCompras(file);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.getReferenceById(1L)).thenReturn(compras);
		when(arquivosComprasRepository.findByCompras(any())).thenReturn(Optional.ofNullable(null));
		
		var result = mvc.perform(get("/arquivos/compras/1/download")
					.header("Content-Disposition", "attachment; filename="+arquivosCompras.getFileName())
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(1)).getReferenceById(1L);
		verify(arquivosComprasRepository, times(1)).findByCompras(any());
	}
	
	private ArquivosCompras mockArquivosCompras(MockMultipartFile file) throws IOException {
		var compras = mockCompras();
		return new ArquivosCompras(1L, compras, file.getBytes(), file.getOriginalFilename(), file.getContentType());
	}

	private Compras mockCompras() {
		return new Compras(1L, new Fornecedor(), new BigDecimal("100"), null, null, LocalDateTime.of(1999, 1, 1, 1, 1));
	}

}
