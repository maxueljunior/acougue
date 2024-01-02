package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
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
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueService;
import br.com.leuxam.acougue.domain.comprasEstoque.DadosCriarComprasEstoque;
import br.com.leuxam.acougue.domain.comprasEstoque.DadosDetalhamentoComprasEstoque;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueData;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueDataRepository;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class ComprasEstoqueControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private ComprasEstoqueService service;
	
	@MockBean
	private ComprasEstoqueRepository comprasEstoqueRepository;
	
	@MockBean
	private ComprasRepository comprasRepository;
	
	@MockBean
	private EstoqueRepository estoqueRepository;
	
	@MockBean
	private EstoqueDataRepository estoqueDataRepository;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoComprasEstoque> dadosDetalhamento;

	@Autowired
	private JacksonTester<DadosCriarComprasEstoque> dadosCriar;
	
	@Autowired
	private JacksonTester<List<DadosDetalhamentoComprasEstoque>> listDadosDetalhamento;
	
	@BeforeAll
	void beforeAll() {
		service = new ComprasEstoqueService(comprasEstoqueRepository,
				comprasRepository, estoqueRepository, estoqueDataRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria salvar caso dados estejam corretos e retornar codigo HTTP 201")
	void test_cenario01() throws Exception {
		var compras = mockCompras();
		var produto = mockProduto(1);
		var compraEstoque = mockCompraEstoque(produto, compras);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.findById(1L)).thenReturn(Optional.of(compras));
		when(estoqueRepository.existsById(1L)).thenReturn(true);
		when(estoqueRepository.getReferenceById(1L)).thenReturn(produto);
		when(comprasEstoqueRepository.save(any())).thenReturn(compraEstoque);
		
		var result = mvc.perform(post("/itens/compras")
							.contentType(MediaType.APPLICATION_JSON)
							.content(
									dadosCriar.write(new DadosCriarComprasEstoque(new BigDecimal("1"),
											1.0, 1L, 1L)).getJson()
									)
						).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoComprasEstoque(compraEstoque)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(1)).findById(1L);
		verify(estoqueRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(1)).getReferenceById(1L);
		verify(estoqueDataRepository).save(any());
		verify(comprasEstoqueRepository, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria salvar caso dados estejam incorretos e retornar codigo HTTP 400")
	void test_cenario02() throws Exception {
		var compras = mockCompras();
		var produto = mockProduto(1);
		var compraEstoque = mockCompraEstoque(produto, compras);
		
		when(comprasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(post("/itens/compras")
							.contentType(MediaType.APPLICATION_JSON)
							.content(
									dadosCriar.write(new DadosCriarComprasEstoque(new BigDecimal("1"),
											1.0, 1L, 1L)).getJson()
									)
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(0)).findById(1L);
		verify(estoqueRepository, times(0)).existsById(1L);
		verify(estoqueRepository, times(0)).getReferenceById(1L);
		verifyNoInteractions(estoqueDataRepository);
		verify(comprasEstoqueRepository, times(0)).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar todas os produtos vinculados a todas as compras e codigo HTTP 200")
	void test_cenario03() throws Exception {
		var compras = mockCompras();
		var produto = mockProduto(1);
		var compraEstoque = mockListCompraEstoque(produto, compras);
		
		when(comprasEstoqueRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(compraEstoque));
		
		var result = mvc.perform(get("/itens/compras")
							.contentType(MediaType.APPLICATION_JSON)
						).andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(compraEstoque.stream()
				.map(DadosDetalhamentoComprasEstoque::new).collect(Collectors.toList())).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(comprasEstoqueRepository, times(1)).findAll(any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar todas os produtos vinculados a uma compra e codigo HTTP 200")
	void test_cenario04() throws Exception {
		var compras = mockCompras();
		var produto = mockProduto(1);
		var compraEstoque = mockListCompraEstoque(produto, compras);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasEstoqueRepository.findByCompras(any(), any(Pageable.class))).thenReturn(new PageImpl<>(compraEstoque));
		
		var result = mvc.perform(get("/itens/compras/1")
							.contentType(MediaType.APPLICATION_JSON)
						).andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(compraEstoque.stream()
				.map(DadosDetalhamentoComprasEstoque::new).collect(Collectors.toList())).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasEstoqueRepository, times(1)).findByCompras(any(), any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria retornar todas os produtos vinculados a uma compra que não existe e codigo HTTP 400")
	void test_cenario05() throws Exception {
		var compras = mockCompras();
		var produto = mockProduto(1);
		var compraEstoque = mockListCompraEstoque(produto, compras);
		
		when(comprasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(get("/itens/compras/1")
							.contentType(MediaType.APPLICATION_JSON)
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasEstoqueRepository, times(0)).findByCompras(any(), any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria deletar o produto vinculado a compra e retornar codigo HTTP 204")
	void test_cenario06() throws Exception {
		var compras = mockCompras();
		var produto = mockProduto(1);
		var compraEstoque = mockCompraEstoque(produto, compras);
		var estoqueData = mockEstoqueData(produto);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.findById(1L)).thenReturn(Optional.of(compras));
		when(estoqueRepository.existsById(1L)).thenReturn(true);
		when(estoqueRepository.getReferenceById(1L)).thenReturn(produto);
		
		when(comprasEstoqueRepository.findByComprasAndEstoque(any(), any())).thenReturn(Optional.of(compraEstoque));
		when(estoqueDataRepository.findByEstoqueAndQuantidadeAndDataCompra(any(), any(), any())).thenReturn(Optional.of(estoqueData));
		
		var result = mvc.perform(delete("/itens/compras/1/1")
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(1)).findById(1L);
		verify(estoqueRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(1)).getReferenceById(1L);
		
		verify(comprasEstoqueRepository, times(1)).findByComprasAndEstoque(any(), any());
		verify(estoqueDataRepository, times(1)).findByEstoqueAndQuantidadeAndDataCompra(any(), any(), any());
		verify(estoqueDataRepository).delete(any());
		verify(comprasEstoqueRepository).delete(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria deletar o produto vinculado a compra e retornar codigo HTTP 400")
	void test_cenario07() throws Exception {
		var compras = mockCompras();
		var produto = mockProduto(1);
		var compraEstoque = mockCompraEstoque(produto, compras);
		var estoqueData = mockEstoqueData(produto);
		
		when(comprasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(delete("/itens/compras/1/1")
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(comprasRepository, times(1)).existsById(1L);
		verify(comprasRepository, times(0)).findById(1L);
		verify(estoqueRepository, times(0)).existsById(1L);
		verify(estoqueRepository, times(0)).getReferenceById(1L);
		
		verify(comprasEstoqueRepository, times(0)).findByComprasAndEstoque(any(), any());
		verify(estoqueDataRepository, times(0)).findByEstoqueAndQuantidadeAndDataCompra(any(), any(), any());
	}
	
	private Fornecedor mockFornecedor() {
		return new Fornecedor(1L, "razao social", "cnpj", "telefone", "contato", true);
	}
	
	private Compras mockCompras() {
		var fornecedor = mockFornecedor();
		List<ArquivosCompras> arquivos = new ArrayList<>();
		return new Compras(null, fornecedor, new BigDecimal("0.0"), null, arquivos, LocalDateTime.of(1999, 1, 1, 1, 1));
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(null, "Carne"+i, Unidade.KG, null, null, null, null);
		return produto;
	}
	
	private ComprasEstoque mockCompraEstoque(Estoque produto, Compras compras) {
		var prod = produto;
		var comp = compras;
		return new ComprasEstoque(null, produto, compras, new BigDecimal("1"), 1.0);
	}
	
	private List<ComprasEstoque> mockListCompraEstoque(Estoque produto, Compras compras){
		var prod = produto;
		var comp = compras;
		List<ComprasEstoque> result = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			result.add(mockCompraEstoque(produto, compras));
		}
		return result;
	}
	
	private EstoqueData mockEstoqueData(Estoque produto) {
		var estoque = produto;
		return new EstoqueData(1L, estoque, 1.0, LocalDate.of(1999, 1, 1), LocalDate.of(1999, 2, 1));
	}
}






















