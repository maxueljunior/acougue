package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.compras.DadosCriarVendas;
import br.com.leuxam.acougue.domain.vendas.CondicaoPagamento;
import br.com.leuxam.acougue.domain.vendas.DadosAtualizarVenda;
import br.com.leuxam.acougue.domain.vendas.DadosDetalhamentoVendas;
import br.com.leuxam.acougue.domain.vendas.Vendas;
import br.com.leuxam.acougue.domain.vendas.VendasDTO;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;
import br.com.leuxam.acougue.domain.vendas.VendasService;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class VendasControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private VendasService service;
	
	@MockBean
	private VendasRepository vendasRepository;

	@MockBean
	private ClienteRepository clienteRepository;

	@MockBean
	private VendasEstoqueRepository vendasEstoqueRepository;

	@Autowired
	private PagedResourcesAssembler<VendasDTO> assembler;
	
	@Autowired
	private JacksonTester<DadosCriarVendas> dadosCriarVendas;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoVendas> dadosDetalhamentoVendas;
	
	@Autowired
	private JacksonTester<DadosAtualizarVenda> dadosAtualizarVendas;
	
	@Autowired
	private JacksonTester<Page<VendasDTO>> vendaDTO;
	
	
	@BeforeAll
	void beforeAll() {
		service = new VendasService(vendasRepository, clienteRepository,
				vendasEstoqueRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria salvar uma Venda caso exista um Cliente e retornar codigo 201")
	void teste_cenario01() throws IOException, Exception {
		var cliente = mockCliente();
		var venda = mockVendaComId(cliente);
		
		when(clienteRepository.existsById(1L)).thenReturn(true);
		
		when(clienteRepository.getReferenceById(1L)).thenReturn(cliente);
		when(vendasRepository.save(any())).thenReturn(venda);
		
		var result = mvc.perform(post("/vendas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							dadosCriarVendas.write(new DadosCriarVendas(cliente.getId())).getJson()
							)
					).andReturn().getResponse();
		
//		var jsonEsperado = dadosDetalhamentoVendas.write(new DadosDetalhamentoVendas(venda)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isNotBlank();
		
		verify(clienteRepository, times(1)).existsById(1L);
		verify(clienteRepository, times(1)).getReferenceById(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria salvar uma Venda caso exista um Cliente e retornar codigo 404")
	void teste_cenario02() throws IOException, Exception {
		
		var cliente = mockCliente();
		when(clienteRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(post("/vendas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							dadosCriarVendas.write(new DadosCriarVendas(cliente.getId())).getJson()
							)
					).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(clienteRepository, times(1)).existsById(1L);
		verify(clienteRepository, times(0)).getReferenceById(1L);
	}

	@Test
	@WithMockUser
	@DisplayName("Deveria retornar todas as vendas sem cliente/com cliente especifico e retornar codigo 200")
	void test_cenario03() throws Exception {
		var vendas = mockVendas();
		var vendasDTO = mockVendasDTO(vendas);
		
		when(vendasRepository.findAll(any(Pageable.class))).thenReturn(vendas);
		
		var result = mvc.perform(get("/vendas")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains("{\"_embedded\":{\"vendasDTOList\":");
		
		verify(vendasRepository, times(1)).findAll(any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar a venda pelo numero e retornar codigo 200")
	void test_cenario04() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVenda(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(vendasRepository.getReferenceById(1L)).thenReturn(vendas);
		
		var result = mvc.perform(get("/vendas/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamentoVendas.write(new DadosDetalhamentoVendas(vendas)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).getReferenceById(1L);
	}

	@Test
	@WithMockUser
	@DisplayName("Não deveria retornar a venda pelo numero caso não exista e retornar codigo 400")
	void test_cenario05() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVenda(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(get("/vendas/1")
					.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(0)).getReferenceById(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria atualizar a condição de pagamento da venda caso exista e retornar codigo 200")
	void test_cenario06() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVenda(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(vendasRepository.findById(1L)).thenReturn(Optional.of(vendas));
		
		vendas.atualizar(new DadosAtualizarVenda(CondicaoPagamento.CREDITO));
		
		var result = mvc.perform(patch("/vendas/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizarVendas.write(new DadosAtualizarVenda(CondicaoPagamento.CREDITO))
						.getJson())
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamentoVendas.write(new DadosDetalhamentoVendas(vendas)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).findById(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria atualizar a condição de pagamento da venda caso não exista e retornar codigo 400")
	void test_cenario07() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVenda(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(patch("/vendas/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizarVendas.write(new DadosAtualizarVenda(CondicaoPagamento.CREDITO))
						.getJson())
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(0)).findById(1L);
	}

	@Test
	@WithMockUser
	@DisplayName("Deveria gerar um cupom não fiscal e devolver codigo 200")
	void test_cenario08() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(vendasRepository.findById(1L)).thenReturn(Optional.of(vendas));
		
		var result = mvc.perform(get("/vendas/gerar-cupom/1")
					.header("Content-Disposition", "attachment;filename= venda 1.pdf")
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isNotBlank();
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).findById(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria gerar um cupom não fiscal caso não exista venda e devolver codigo 400")
	void test_cenario09() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(false);
		
		var result = mvc.perform(get("/vendas/gerar-cupom/1")
				.header("Content-Disposition", "attachment;filename= venda 1.pdf")
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(0)).findById(1L);
	}

	@Test
	@WithMockUser
	@DisplayName("Deveria fazer um download do cupom não fiscal caso já tenha sido gerado e devolver codigo 200")
	void test_cenario10() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(vendasRepository.getReferenceById(1L)).thenReturn(vendas);
		
		var result = mvc.perform(get("/vendas/download/1")
					.header("Content-Disposition", "attachment;filename=venda 1.pdf")
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isNotBlank();
		
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).getReferenceById(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria fazer um download do cupom não fiscal caso não tenha sido gerado e devolver codigo 404")
	void test_cenario11() throws Exception {
		var cliente = mockCliente();
		var vendas = mockVenda(cliente);
		
		when(vendasRepository.existsById(1L)).thenReturn(true);
		when(vendasRepository.getReferenceById(1L)).thenReturn(vendas);
		
		var result = mvc.perform(get("/vendas/download/1")
					.header("Content-Disposition", "attachment;filename=venda 1.pdf")
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		verify(vendasRepository, times(1)).existsById(1L);
		verify(vendasRepository, times(1)).getReferenceById(1L);
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(1L, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Cliente mockClienteTwo() {
		var endereco = new Endereco("rua 2", "bairro 2 ", 2222);
		var cliente = new Cliente(1L, "nome 2", "sobrenome 2", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Page<Vendas> mockVendas(){
		var cliente1 = mockCliente();
		var cliente2 = mockClienteTwo();
		
		List<Vendas> lista = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			if(i % 2 == 0) {
				lista.add(mockVenda(cliente1));
			}else {
				lista.add(mockVenda(cliente2));
			}
		}
		return new PageImpl<>(lista);
	}
	
	private Page<VendasDTO> mockVendasDTO(Page<Vendas> lista){
		List<VendasDTO> listaDTO = new ArrayList<>();
		
		for (Vendas vendas : lista) {
			listaDTO.add(new VendasDTO(vendas.getId(), vendas.getCondicaoPagamento(), vendas.getDataVenda(),
					vendas.getValorTotal(), vendas.getCliente().getId(), vendas.getFileName()));
		}
		
		return new PageImpl<>(listaDTO);
	}
	
	private Vendas mockVenda(Cliente cliente) {
		var venda = new Vendas(cliente);
		return venda;
	}
	
	private Vendas mockVendaComId(Cliente cliente) {
		byte[] bytes = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
		var venda = new Vendas(1L, LocalDateTime.now(), new BigDecimal("1"), cliente, CondicaoPagamento.DEBITO,bytes,"venda 1.pdf",null);
		return venda;
	}
}
















































