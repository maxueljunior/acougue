package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.ClienteService;
import br.com.leuxam.acougue.domain.cliente.DadosAtualizarCliente;
import br.com.leuxam.acougue.domain.cliente.DadosCriarCliente;
import br.com.leuxam.acougue.domain.cliente.DadosDetalhamentoCliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.DadosEndereco;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.IdProdutosEstoque;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class ClienteControllerTest {
	
	private ClienteService service;
	
	@MockBean
	private ClienteRepository clienteRepository;

	@MockBean
	private ClienteEstoqueRepository clienteEstoqueRepository;

	@MockBean
	private EstoqueRepository estoqueRepository;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoCliente> dadosDetalhamento;

	@Autowired
	private JacksonTester<DadosCriarCliente> dadosCriar;
	
	@Autowired
	private JacksonTester<List<DadosDetalhamentoCliente>> listDadosDetalhamento;
	
	@Autowired
	private JacksonTester<DadosAtualizarCliente> dadosAtualizar;
	
	@BeforeAll
	void beforeAll() {
		service = new ClienteService(clienteRepository, clienteEstoqueRepository, estoqueRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria salvar um Cliente e retornar codigo HTTP 201")
	void test_cenario01() throws Exception {
		var cliente = mockCliente(1);
		var idProdutos = mockListIdProdutos();
		when(clienteRepository.save(any())).thenReturn(cliente);
		when(estoqueRepository.findIdsAll()).thenReturn(idProdutos);
		
		var result = mvc.perform(post("/clientes")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(
							new DadosCriarCliente(cliente.getNome(), cliente.getSobrenome(),
							cliente.getSexo(),cliente.getDataNascimento(), cliente.getTelefone(),
								new DadosEndereco(cliente.getEndereco().getBairro(),
										cliente.getEndereco().getNumero(), cliente.getEndereco().getRua()))
							).getJson())
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoCliente(cliente)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(clienteRepository, times(1)).save(any());
		verify(estoqueRepository, times(1)).findIdsAll();
		verify(estoqueRepository).getReferenceById(any());
		verify(clienteEstoqueRepository).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria salvar um Cliente caso dados incorretos e retornar codigo HTTP 400")
	void test_cenario02() throws Exception {
		var cliente = mockCliente(1);
		var idProdutos = mockListIdProdutos();
		
		var result = mvc.perform(post("/clientes")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(
							new DadosCriarCliente(null, null,
									null,null, null,
								null)
							).getJson())
				).andReturn().getResponse();
		
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verifyNoInteractions(clienteRepository);
		verifyNoInteractions(estoqueRepository);
		verifyNoInteractions(clienteEstoqueRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria recuperar todos com ou sem busca por nome e retornar codigo HTTP 200")
	void test_cenario03() throws Exception {
		var clientes = mockListCliente();
		
		when(clienteRepository.queryByFindAllByAtivoAndNome(any(), any(Pageable.class))).thenReturn(new PageImpl<>(clientes));
		
		var result = mvc.perform(get("/clientes")
					.contentType(MediaType.APPLICATION_JSON)
					.param("q", "")
				).andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(clientes.stream().map(DadosDetalhamentoCliente::new).collect(Collectors.toList())).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(clienteRepository, times(1)).queryByFindAllByAtivoAndNome(any(), any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria recuperar pelo ID e retornar codigo HTTP 200")
	void test_cenario04() throws Exception {
		var clientes = mockCliente(2);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(clientes));
		
		var result = mvc.perform(get("/clientes/2")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoCliente(clientes)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(clienteRepository, times(1)).findByIdAndAtivoTrue(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria recuperar pelo ID caso não exista e retornar codigo HTTP 404")
	void test_cenario05() throws Exception {
		var clientes = mockCliente(2);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.ofNullable(null));
		
		var result = mvc.perform(get("/clientes/2")
						.contentType(MediaType.APPLICATION_JSON)
					).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(clienteRepository, times(1)).findByIdAndAtivoTrue(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria atualizar caso dados e id estejam validos e retornar codigo HTTP 200")
	void test_cenario06() throws Exception {
		var clientes = mockCliente(2);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(clientes));
		
		var result = mvc.perform(put("/clientes/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(
									dadosAtualizar.write(new DadosAtualizarCliente("maxuel", null, null, null, null, null)).getJson()
								)
					).andReturn().getResponse();
		
		clientes.atualizarDados(new DadosAtualizarCliente("maxuel", null, null, null, null, null));
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoCliente(clientes)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(clienteRepository, times(1)).findByIdAndAtivoTrue(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria atualizar caso id esteja invalido e retornar codigo HTTP 404")
	void test_cenario07() throws Exception {
		var clientes = mockCliente(2);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.ofNullable(null));
		
		var result = mvc.perform(put("/clientes/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(
									dadosAtualizar.write(new DadosAtualizarCliente("maxuel", null, null, null, null, null)).getJson()
								)
					).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(clienteRepository, times(1)).findByIdAndAtivoTrue(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria desativar e retornar codigo HTTP 204")
	void test_cenario08() throws Exception {
		var clientes = mockCliente(2);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(clientes));
		
		var result = mvc.perform(delete("/clientes/2")
					).andReturn().getResponse();
		 
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
		
		verify(clienteRepository, times(1)).findByIdAndAtivoTrue(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria desativar caso já esteja desativado e retornar codigo HTTP 404")
	void test_cenario09() throws Exception {
		var clientes = mockCliente(2);
		clientes.desativar();
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(clientes));
		
		var result = mvc.perform(delete("/clientes/2")
					).andReturn().getResponse();
		 
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
		
		verify(clienteRepository, times(1)).findByIdAndAtivoTrue(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria reativar caso esteja desativado e retornar codigo HTTP 200")
	void test_cenario10() throws Exception {
		var clientes = mockCliente(2);
		clientes.desativar();
		when(clienteRepository.findById(any())).thenReturn(Optional.of(clientes));
		
		var result = mvc.perform(patch("/clientes/2")
					).andReturn().getResponse();
		 
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo("Cliente ativado!");
		
		verify(clienteRepository, times(1)).findById(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria reativar caso esteja ativado e retornar codigo HTTP 400")
	void test_cenario11() throws Exception {
		var clientes = mockCliente(2);
		
		when(clienteRepository.findById(any())).thenReturn(Optional.of(clientes));
		
		var result = mvc.perform(patch("/clientes/2")
					).andReturn().getResponse();
		 
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(clienteRepository, times(1)).findById(any());
	}
	
	private Cliente mockCliente(int i) {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(null, "nome" + i, "sobrenome" + i, "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private List<Cliente> mockListCliente(){
		List<Cliente> result = new ArrayList<>();
		
		for(int i = 0; i < 5; i++) {
			result.add(mockCliente(i));
		}
		return result;
	}
	
	private List<IdProdutosEstoque> mockListIdProdutos(){
		List<IdProdutosEstoque> result = new ArrayList<>();
		result.add(new IdProdutosEstoque(1L));
		return result;
	}
}
