package br.com.leuxam.acougue.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasRepository;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasService;
import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.compras.DadosCriarVendas;
import br.com.leuxam.acougue.domain.vendas.DadosDetalhamentoVendas;
import br.com.leuxam.acougue.domain.vendas.Vendas;
import br.com.leuxam.acougue.domain.vendas.VendasDTO;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;
import br.com.leuxam.acougue.domain.vendas.VendasService;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;

@SpringBootTest
@AutoConfigureMockMvc
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

	@MockBean
	private ModelMapper modelMapper;
	
	private ArquivosComprasService serviceArquivos;

	@MockBean
	private ArquivosComprasRepository arquivosComprasRepository;

	@MockBean
	private ComprasRepository comprasRepository;
	
	@MockBean
	private PagedResourcesAssembler<VendasDTO> assembler;
	
	@Autowired
	private JacksonTester<DadosCriarVendas> dadosCriarVendas;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoVendas> dadosDetalhamentoVendas;
	
	@Autowired
	private JacksonTester<VendasDTO> vendaDTO;
	
	
	@BeforeAll
	void beforeAll() {
		service = new VendasService(vendasRepository, clienteRepository,
				vendasEstoqueRepository, assembler, modelMapper);
		
		serviceArquivos = new ArquivosComprasService(arquivosComprasRepository, comprasRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria salvar uma Venda caso exista um Cliente e retornar codigo 201")
	void teste_cenario01() throws IOException, Exception {
		var cliente = mockCliente();
		var venda = mockVenda(cliente);
		
		when(clienteRepository.existsById(1L)).thenReturn(true);
		
		when(clienteRepository.getReferenceById(1L)).thenReturn(cliente);
		
		var result = mvc.perform(post("/vendas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							dadosCriarVendas.write(new DadosCriarVendas(cliente.getId())).getJson()
							)
					).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamentoVendas.write(new DadosDetalhamentoVendas(venda)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
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
	@DisplayName("Deveria retornar todas as vendas sem cliente especifico e retornar codigo 200")
	void test_cenario03() throws Exception {
		var vendas = mockVendas();
		var vendasDTO = mockVendasDTO(vendas);
		
		when(vendasRepository.findAll(any(Pageable.class))).thenReturn(vendas);
		when(modelMapper.map(any(), any())).thenReturn(vendasDTO);
		
		var result = mvc.perform(get("/vendas")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		
	}

	@Test
	void testFindById() {
	}

	@Test
	void testUpdate() {
	}

	@Test
	void testGerarPdf() {
	}

	@Test
	void testDownloadPdf() {
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
}
















































