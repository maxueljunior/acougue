package br.com.leuxam.acougue.testunits.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.ClienteService;
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
		
		var result = mvc.perform(post(null)
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(
							new DadosCriarCliente(cliente.getNome(), cliente.getSobrenome(),
							cliente.getSexo(),cliente.getDataNascimento(), cliente.getTelefone(),
								new DadosEndereco(cliente.getEndereco().getBairro(),
										cliente.getEndereco().getNumero(), cliente.getEndereco().getRua()))
							).getJson())
				).andReturn().getResponse();
	}
	
	private Cliente mockCliente(int i) {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(null, "nome" + i, "sobrenome" + i, "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private List<IdProdutosEstoque> mockListIdProdutos(){
		List<IdProdutosEstoque> result = new ArrayList<>();
		result.add(new IdProdutosEstoque(1L));
		return result;
	}
}
