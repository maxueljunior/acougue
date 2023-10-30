package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
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

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoque;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueData;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueDataRepository;
import br.com.leuxam.acougue.domain.vendas.CondicaoPagamento;
import br.com.leuxam.acougue.domain.vendas.Vendas;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosAtualizarVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosCriarVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosDetalhamentoVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class VendasEstoqueControllerTest {
	
	private VendasEstoqueService service;
	
	@MockBean
	private VendasEstoqueRepository vendasEstoqueRepository;

	@MockBean
	private VendasRepository vendasRepository;

	@MockBean
	private EstoqueRepository estoqueRepository;

	@MockBean
	private ComprasRepository comprasRepository;

	@MockBean
	private ClienteEstoqueRepository clienteEstoqueRepository;

	@MockBean
	private EstoqueDataRepository estoqueDataRepository;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoVendaEstoque> dadosDetalhamento;

	@Autowired
	private JacksonTester<DadosAtualizarVendaEstoque> dadosAtualizar;
	
	@Autowired
	private JacksonTester<List<DadosDetalhamentoVendaEstoque>> dadosListDetalhamento;

	@Autowired
	private JacksonTester<DadosCriarVendaEstoque> dadosCriar;
	
	@BeforeAll
	void beforeAll() {
		service = new VendasEstoqueService(vendasEstoqueRepository, vendasRepository,
				estoqueRepository, comprasRepository, clienteEstoqueRepository, estoqueDataRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria salvar caso informações estejam corretas e retornar codigo 201")
	void test_cenario01() throws Exception {
		var vendaEstoque = mockVendaProdutos();
		var dataEstoque = mockEstoqueData();
		var clienteEstoque = mockClienteEstoque();
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(estoqueRepository.existsById(1L)).thenReturn(true);
		when(vendasRepository.findById(1L)).thenReturn(Optional.of(vendaEstoque.getVendas()));
		when(estoqueRepository.findById(1L)).thenReturn(Optional.of(vendaEstoque.getEstoque()));
		when(estoqueDataRepository.findByEstoqueAndDataCompra(any(), any())).thenReturn(Optional.of(dataEstoque));
		when(comprasRepository.searchDataRecente(any())).thenReturn(LocalDateTime.now());
		when(comprasRepository.searchPrecoRecente(any(), any())).thenReturn(new BigDecimal("15.00"));
		when(clienteEstoqueRepository.findByClienteAndEstoque(any(), any())).thenReturn(clienteEstoque);
		
		var result = mvc.perform(post("/itens/vendas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(new DadosCriarVendaEstoque(
							vendaEstoque.getEstoque().getId(), vendaEstoque.getVendas().getId(),
							vendaEstoque.getQuantidade(), vendaEstoque.getValorUnitario(), LocalDate.of(1999, 1, 1))
								).getJson()
							)
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoVendaEstoque(vendaEstoque)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).findById(1L);
		verify(estoqueRepository, times(1)).findById(1L);
		verify(estoqueDataRepository, times(1)).findByEstoqueAndDataCompra(any(), any());
		verify(comprasRepository, times(1)).searchDataRecente(any());
		verify(comprasRepository, times(1)).searchPrecoRecente(any(), any());
		verify(clienteEstoqueRepository, times(1)).findByClienteAndEstoque(any(), any());
		
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria salvar caso informações estejam incorretas e retornar codigo 400")
	void test_cenario02() throws Exception {
		var vendaEstoque = mockVendaProdutos();
		var dataEstoque = mockEstoqueData();
		var clienteEstoque = mockClienteEstoque();
		
		when(vendasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(post("/itens/vendas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(new DadosCriarVendaEstoque(
							vendaEstoque.getEstoque().getId(), vendaEstoque.getVendas().getId(),
							vendaEstoque.getQuantidade(), vendaEstoque.getValorUnitario(), LocalDate.of(1999, 1, 1))
								).getJson()
							)
				).andReturn().getResponse();
		
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(0)).existsById(1L);
		verify(vendasRepository, times(0)).findById(1L);
		verify(estoqueRepository, times(0)).findById(1L);
		verify(estoqueDataRepository, times(0)).findByEstoqueAndDataCompra(any(), any());
		verify(comprasRepository, times(0)).searchDataRecente(any());
		verify(comprasRepository, times(0)).searchPrecoRecente(any(), any());
		verify(clienteEstoqueRepository, times(0)).findByClienteAndEstoque(any(), any());
		
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar todas as vendas de produtos de todos os produtos e vendas e codigo 200")
	void test_cenario03() throws Exception {
		var vendasEstoque = mockListVendasProdutos();
		
		when(vendasEstoqueRepository.findAll(any(Pageable.class)))
			.thenReturn(new PageImpl<>(vendasEstoque));
		
		var result = mvc.perform(get("/itens/vendas")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		
		var jsonEsperado = dadosListDetalhamento.write(vendasEstoque.stream()
				.map(DadosDetalhamentoVendaEstoque::new).collect(Collectors.toList())).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		verify(vendasEstoqueRepository, times(1)).findAll(any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar uma lista de produtos vinculados ao id da venda passado e devolver codigo 200")
	void test_cenario04() throws Exception {
		var vendasEstoque = mockListVendasProdutos();
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(vendasEstoqueRepository.findByVendas(any(), any(Pageable.class)))
			.thenReturn(new PageImpl<>(vendasEstoque));
		
		var result = mvc.perform(get("/itens/vendas/1")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		var jsonEsperado = dadosListDetalhamento.write(
				vendasEstoque.stream().map(DadosDetalhamentoVendaEstoque::new)
				.collect(Collectors.toList())).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasEstoqueRepository, times(1)).findByVendas(any(), any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria retornar uma lista de produtos vinculados ao id da venda caso ela não exista e devolver codigo 400")
	void test_cenario05() throws Exception {
		var vendasEstoque = mockListVendasProdutos();
		
		when(vendasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(get("/itens/vendas/1")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasEstoqueRepository, times(0)).findByVendas(any(), any(Pageable.class));
	}

	@Test
	@WithMockUser
	@DisplayName("Deveria deletar caso tenha o produto alocado na venda e retornar codigo 204")
	void test_cenario06() throws Exception {
		var venda = mockVendas();
		var estoque = mockProduto();
		var vendaEstoque = mockVendaProdutos();
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(estoqueRepository.existsById(1L)).thenReturn(true);
		when(vendasRepository.findById(1L)).thenReturn(Optional.of(venda));
		when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
		when(vendasEstoqueRepository.findByVendasAndEstoque(any(), any())).thenReturn(Optional.of(vendaEstoque));
		
		var result = mvc.perform(delete("/itens/vendas/1/1")
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).findById(1L);
		verify(estoqueRepository, times(1)).findById(1L);
		verify(vendasEstoqueRepository, times(1)).findByVendasAndEstoque(any(), any());
		verify(vendasEstoqueRepository, times(1)).delete(vendaEstoque);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria deletar caso tenha o produto alocado na venda e retornar codigo 400")
	void test_cenario07() throws Exception {
		var venda = mockVendas();
		var estoque = mockProduto();
		var vendaEstoque = mockVendaProdutos();
		
		when(vendasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(delete("/itens/vendas/1/1")
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(0)).existsById(1L);
		verify(vendasRepository, times(0)).findById(1L);
		verify(estoqueRepository, times(0)).findById(1L);
		verify(vendasEstoqueRepository, times(0)).findByVendasAndEstoque(any(), any());
		verify(vendasEstoqueRepository, times(0)).delete(vendaEstoque);
	}

	
	@Test
	@WithMockUser
	@DisplayName("Deveria atualizar os dados do produto alocado na venda e devolver codigo 200")
	void test_cenario08() throws IOException, Exception {
		var venda = mockVendas();
		var estoque = mockProduto();
		var vendaEstoque = mockVendaProdutos();
		var estoqueData = mockEstoqueData();
		var clienteEstoque = mockClienteEstoque();
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(estoqueRepository.existsById(1L)).thenReturn(true);
		
		when(vendasRepository.findById(1L)).thenReturn(Optional.of(venda));
		when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
		
		when(vendasEstoqueRepository.findByVendasAndEstoque(any(), any())).thenReturn(Optional.of(vendaEstoque));
		
		when(estoqueDataRepository.findByEstoqueAndDataCompra(any(), any())).thenReturn(Optional.of(estoqueData));
		
		when(comprasRepository.searchDataRecente(any())).thenReturn(LocalDateTime.now());
		when(comprasRepository.searchPrecoRecente(any(), any())).thenReturn(new BigDecimal("15.00"));
		when(clienteEstoqueRepository.findByClienteAndEstoque(any(), any())).thenReturn(clienteEstoque);
		
		var result = mvc.perform(put("/itens/vendas/1/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizar
								.write(new DadosAtualizarVendaEstoque(2.0, new BigDecimal("100"),
										LocalDate.of(1999, 1, 1)))
								.getJson())
				).andReturn().getResponse();
		vendaEstoque.atualizar(new DadosAtualizarVendaEstoque(2.0, new BigDecimal("100"),
										LocalDate.of(1999, 1, 1)));
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoVendaEstoque(vendaEstoque)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).findById(1L);
		verify(estoqueRepository, times(1)).findById(1L);
		verify(vendasEstoqueRepository, times(1)).findByVendasAndEstoque(any(), any());
		verify(estoqueDataRepository, times(1)).findByEstoqueAndDataCompra(any(), any());
		verify(comprasRepository, times(1)).searchDataRecente(any());
		verify(comprasRepository, times(1)).searchPrecoRecente(any(), any());
		verify(clienteEstoqueRepository, times(1)).findByClienteAndEstoque(any(), any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria atualizar os dados do produto alocado na venda se estiverem incorretos e devolver codigo 400")
	void test_cenario09() throws IOException, Exception {
		var venda = mockVendas();
		var estoque = mockProduto();
		var vendaEstoque = mockVendaProdutos();
		var estoqueData = mockEstoqueData();
		var clienteEstoque = mockClienteEstoque();
		
		when(vendasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(put("/itens/vendas/1/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizar
								.write(new DadosAtualizarVendaEstoque(2.0, new BigDecimal("100"),
										LocalDate.of(1999, 1, 1)))
								.getJson())
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(estoqueRepository, times(0)).existsById(1L);
		verify(vendasRepository, times(0)).findById(1L);
		verify(estoqueRepository, times(0)).findById(1L);
		verify(vendasEstoqueRepository, times(0)).findByVendasAndEstoque(any(), any());
		verify(estoqueDataRepository, times(0)).findByEstoqueAndDataCompra(any(), any());
		verify(comprasRepository, times(0)).searchDataRecente(any());
		verify(comprasRepository, times(0)).searchPrecoRecente(any(), any());
		verify(clienteEstoqueRepository, times(0)).findByClienteAndEstoque(any(), any());
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(1L, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Vendas mockVendas() {
		var cliente = mockCliente();
		var venda = new Vendas(1L, LocalDateTime.of(1999, 1, 1, 1, 1), new BigDecimal("1"), cliente, CondicaoPagamento.PIX, null, null, null);
		return venda;
	}
	
	private Estoque mockProduto() {
		var produto = new Estoque(1L, "Carne", Unidade.KG, null, null, null, null);
		return produto;
	}
	
	private VendasEstoque mockVendaProdutos() {
		var vendas = mockVendas();
		var produto = mockProduto();
		var vendaEstoque = new VendasEstoque(null, vendas, produto, new BigDecimal("100.00"), 2.0);
		return vendaEstoque;
	}
	
	private EstoqueData mockEstoqueData() {
		var estoque = mockProduto();
		var estoqueData = new EstoqueData(1L, estoque, 5.0, LocalDate.of(1999, 1, 1), LocalDate.of(1999, 2, 1));
		return estoqueData;
	}
	
	private ClienteEstoque mockClienteEstoque() {
		var estoque = mockProduto();
		var cliente = mockCliente();
		var clienteEstoque = new ClienteEstoque(null, estoque, cliente, new BigDecimal("150"), LocalDateTime.now());
		return clienteEstoque;
	}
	
	private List<VendasEstoque> mockListVendasProdutos(){
		List<VendasEstoque> result = new ArrayList<>();
		
		for(int i = 0; i < 5; i++) {
			result.add(mockVendaProdutos());
		}
		
		return result;
	}
}
