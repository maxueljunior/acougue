package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDate;
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
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.IdCliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.DadosAtualizarEstoque;
import br.com.leuxam.acougue.domain.estoque.DadosCriarEstoque;
import br.com.leuxam.acougue.domain.estoque.DadosDetalhamentoEstoque;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueService;
import br.com.leuxam.acougue.domain.estoque.Unidade;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class EstoqueControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private EstoqueService service;
	
	@MockBean
	private EstoqueRepository estoqueRepository;

	@MockBean
	private ClienteRepository clienteRepository;

	@MockBean
	private ClienteEstoqueRepository clienteEstoqueRepository;
	
	@Autowired
	private JacksonTester<DadosCriarEstoque> dadosCriar;

	@Autowired
	private JacksonTester<DadosDetalhamentoEstoque> dadosDetalhamento;

	@Autowired
	private JacksonTester<List<DadosDetalhamentoEstoque>> listDadosDetalhamento;

	@Autowired
	private JacksonTester<DadosAtualizarEstoque> dadosAtualizar;
	
	@BeforeAll
	void beforeAll() {
		service = new EstoqueService(estoqueRepository, clienteRepository, clienteEstoqueRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria salvar caso os dados estejam validos e retorna codigo HTTP 201")
	void test_cenario01() throws Exception {
		var estoque = mockProduto(1);
		var listaIdClientes = mockIdClientes();
		
		when(estoqueRepository.save(any())).thenReturn(estoque);
		when(clienteRepository.findIdAlls()).thenReturn(listaIdClientes);
		
		var result = mvc.perform(post("/produtos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(new DadosCriarEstoque(estoque.getDescricao(),
							estoque.getUnidade())).getJson()
							)
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoEstoque(estoque)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(estoqueRepository, times(1)).save(any());
		verify(clienteRepository, times(1)).findIdAlls();
		verify(clienteRepository).getReferenceById(any());
		verify(clienteEstoqueRepository).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria salvar caso os dados estejam invalidos e retorna codigo HTTP 400")
	void test_cenario02() throws Exception {
		var estoque = mockProduto(1);
		var listaIdClientes = mockIdClientes();
		
		when(estoqueRepository.save(any())).thenReturn(estoque);
		when(clienteRepository.findIdAlls()).thenReturn(listaIdClientes);
		
		var result = mvc.perform(post("/produtos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(new DadosCriarEstoque(null,
							null)).getJson()
							)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verifyNoInteractions(estoqueRepository);
		verifyNoInteractions(clienteRepository);
		verifyNoInteractions(clienteEstoqueRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria devolver todos os produtos e retornar codigo 200")
	void test_cenario03() throws Exception {
		var estoques = mockListProdutos();
		
		when(estoqueRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(estoques));
		
		var result = mvc.perform(get("/produtos")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(estoques.stream()
				.map(DadosDetalhamentoEstoque::new).collect(Collectors.toList())).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(estoqueRepository, times(1)).findAll(any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria devolver produto pelo ID e retornar codigo 200")
	void test_cenario04() throws Exception {
		var estoques = mockProduto(5);
		
		when(estoqueRepository.findById(5L)).thenReturn(Optional.of(estoques));
		
		var result = mvc.perform(get("/produtos/5")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoEstoque(estoques)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(estoqueRepository, times(1)).findById(5L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria devolver produto pelo ID caso não exista e retornar codigo 404")
	void test_cenario05() throws Exception {
		var estoques = mockProduto(5);
		
		when(estoqueRepository.findById(5L)).thenReturn(Optional.ofNullable(null));
		
		var result = mvc.perform(get("/produtos/5")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(estoqueRepository, times(1)).findById(5L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria atualizar a descrição caso o Produto exista e retornar codigo 200")
	void test_cenario06() throws Exception {
		var estoques = mockProduto(1);
		
		when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoques));
		
		var result = mvc.perform(patch("/produtos/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							dadosAtualizar.write(new DadosAtualizarEstoque("File de frango"))
								.getJson()
							)
				).andReturn().getResponse();
		
		estoques.atualizar(new DadosAtualizarEstoque("File de frango"));
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoEstoque(estoques)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(estoqueRepository, times(1)).findById(1L);
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(null, "Carne"+i, Unidade.KG, null, null, null, null);
		return produto;
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(1L, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private List<IdCliente> mockIdClientes(){
		List<IdCliente> result = new ArrayList<>();
		result.add(new IdCliente(1L));
		return result;
	}
	
	private List<Estoque> mockListProdutos(){
		List<Estoque> result = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			result.add(mockProduto(i));
		}
		return result;
	}
}





