package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosCompras;
import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasDTO;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.compras.ComprasService;
import br.com.leuxam.acougue.domain.compras.DadosCriarCompras;
import br.com.leuxam.acougue.domain.compras.DadosDetalhamentoCompras;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class ComprasControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private ComprasService service;
	
	@MockBean
	private ComprasRepository comprasRepository;

	@MockBean
	private FornecedorRepository fornecedorRepository;
	
	@Autowired
	private JacksonTester<DadosCriarCompras> dadosCriar;

	@Autowired
	private JacksonTester<DadosDetalhamentoCompras> dadosDetalhamento;
	
	@BeforeAll
	void beforeAll() {
		service = new ComprasService(comprasRepository, fornecedorRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria criar uma compra caso dados estejam corretos e retornar codigo HTTP 201")
	void test_cenario01() throws IOException, Exception {
		var compras = mockCompras();
		var fornecedor = mockFornecedor();
		
		when(fornecedorRepository.existsById(1L)).thenReturn(true);
		when(fornecedorRepository.getReferenceById(1L)).thenReturn(fornecedor);
		when(comprasRepository.save(any())).thenReturn(compras);
		
		var result = mvc.perform(post("/compras")
						.contentType(MediaType.APPLICATION_JSON)
						.content(
								dadosCriar.write(new DadosCriarCompras(1L)).getJson()
								)
					).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isNotBlank();
		
		verify(fornecedorRepository, times(1)).existsById(1L);
		verify(fornecedorRepository, times(1)).getReferenceById(1L);
		verify(comprasRepository, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria criar uma compra caso dados estejam incorretos e retornar codigo HTTP 404")
	void test_cenario02() throws IOException, Exception {
		var compras = mockCompras();
		var fornecedor = mockFornecedor();
		
		when(fornecedorRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(post("/compras")
						.contentType(MediaType.APPLICATION_JSON)
						.content(
								dadosCriar.write(new DadosCriarCompras(1L)).getJson()
								)
					).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(fornecedorRepository, times(1)).existsById(1L);
		verify(fornecedorRepository, times(0)).getReferenceById(1L);
		verify(comprasRepository, times(0)).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar todas as compras e codigo HTTP 200")
	void test_cenario03() throws IOException, Exception {
		var compras = listMockCompras();
		
		when(comprasRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(compras));
		
		var result = mvc.perform(get("/compras")
							.contentType(MediaType.APPLICATION_JSON)
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains("{\"_embedded\":{\"comprasDTOList\":");
		
		verify(comprasRepository, times(1)).findAll(any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar a compra pelo ID caso exista e codigo HTTP 200")
	void test_cenario04() throws IOException, Exception {
		var compras = mockCompras();
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.findById(1L)).thenReturn(Optional.of(compras));
		
		var result = mvc.perform(get("/compras/1")
							.contentType(MediaType.APPLICATION_JSON)
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isNotBlank();
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(1)).findById(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria retornar a compra pelo ID caso não exista e codigo HTTP 400")
	void test_cenario05() throws IOException, Exception {
		var compras = mockCompras();
		
		when(comprasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(get("/compras/1")
							.contentType(MediaType.APPLICATION_JSON)
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(0)).findById(1L);
	}
	
	
	
	private Fornecedor mockFornecedor() {
		return new Fornecedor(1L, "razao social", "cnpj", "telefone", "contato", true);
	}
	
	private Compras mockCompras() {
		var fornecedor = mockFornecedor();
		List<ArquivosCompras> arquivos = new ArrayList<>();
		return new Compras(null, fornecedor, new BigDecimal("0.0"), null, arquivos, LocalDateTime.of(1999, 1, 1, 1, 1));
	}
	
	private List<Compras> listMockCompras(){
		List<Compras> result = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			result.add(mockCompras());
		}
		return result;
	}
}
