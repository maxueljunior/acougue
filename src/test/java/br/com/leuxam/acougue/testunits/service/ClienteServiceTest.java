package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.ClienteService;
import br.com.leuxam.acougue.domain.cliente.DadosAtualizarCliente;
import br.com.leuxam.acougue.domain.cliente.DadosCriarCliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.DadosEndereco;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.IdProdutosEstoque;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ClienteServiceTest {
	
	private ClienteService service;
	
	@MockBean
	private ClienteRepository clienteRepository;

	@MockBean
	private ClienteEstoqueRepository clienteEstoqueRepository;

	@MockBean
	private EstoqueRepository estoqueRepository;
	
	private Pageable pageable;
	
	@BeforeAll
	void beforeAll() {
		service = new ClienteService(clienteRepository, clienteEstoqueRepository, estoqueRepository);
		pageable = PageRequest.of(0, 10, Sort.by("nome"));
	}
	
	@Test
	@DisplayName("Deveria salvar caso dados estejam corretos")
	void test_cenario01() {
		var cliente = mockCliente(1);
		var listaId = mockListIdProdutos();
		var dadosCriar = mockDadosCliente(cliente);
		
		when(clienteRepository.save(any())).thenReturn(cliente);
		when(estoqueRepository.findIdsAll()).thenReturn(listaId);
		
		var result = service.save(dadosCriar);
		
		assertNotNull(result);
		assertNotNull(result.id());
		
		assertEquals(1L, result.id());
		assertEquals("nome1", result.nome());
		assertEquals("sobrenome1", result.sobrenome());
		assertEquals("99232", result.telefone());
		assertEquals(LocalDate.of(1999, 1, 1), result.dataNascimento());
		assertEquals("rua", result.endereco().rua);
		assertEquals("bairro", result.endereco().bairro);
		assertEquals(22, result.endereco().numero);
		
		verify(estoqueRepository).getReferenceById(any());
		verify(clienteEstoqueRepository).save(any());
	}
	
	@Test
	@DisplayName("Deveria retornar todos os Clientes sem procurar por nome")
	void test_cenario02() {
		var clientes = mockListCliente();
		
		when(clienteRepository.queryByFindAllByAtivoAndNome(any(), any(Pageable.class))).thenReturn(new PageImpl<>(clientes));
		
		var result = service.findAllByAtivoAndNome(pageable, "");
		assertNotNull(result);
		assertEquals(5, result.getContent().size());
		
		var cliente1 = result.getContent().get(1);
		
		assertNotNull(cliente1.id());
		
		assertEquals(1L, cliente1.id());
		assertEquals("nome1", cliente1.nome());
		assertEquals("sobrenome1", cliente1.sobrenome());
		assertEquals("99232", cliente1.telefone());
		assertEquals(LocalDate.of(1999, 1, 1), cliente1.dataNascimento());
		assertEquals("rua", cliente1.endereco().rua);
		assertEquals("bairro", cliente1.endereco().bairro);
		assertEquals(22, cliente1.endereco().numero);
		
		var cliente3 = result.getContent().get(3);
		
		assertNotNull(cliente3.id());
		
		assertEquals(3L, cliente3.id());
		assertEquals("nome3", cliente3.nome());
		assertEquals("sobrenome3", cliente3.sobrenome());
		assertEquals("99232", cliente3.telefone());
		assertEquals(LocalDate.of(1999, 1, 1), cliente3.dataNascimento());
		assertEquals("rua", cliente3.endereco().rua);
		assertEquals("bairro", cliente3.endereco().bairro);
		assertEquals(22, cliente3.endereco().numero);
		
		assertEquals(5, result.getSize());
		assertEquals(5, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
	}
	
	@Test
	@DisplayName("Deveria retornar um Cliente pelo id caso exista")
	void test_cenario03() {
		var cliente = mockCliente(1);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(cliente));
		
		var result = service.findByIdAndAtivo(1L);
		
		assertNotNull(result);
		assertNotNull(result.id());
		
		assertEquals(1L, result.id());
		assertEquals("nome1", result.nome());
		assertEquals("sobrenome1", result.sobrenome());
		assertEquals("99232", result.telefone());
		assertEquals(LocalDate.of(1999, 1, 1), result.dataNascimento());
		assertEquals("rua", result.endereco().rua);
		assertEquals("bairro", result.endereco().bairro);
		assertEquals(22, result.endereco().numero);
	}
	
	@Test
	@DisplayName("Não deveria retornar um Cliente pelo id caso não exista")
	void test_cenario04() {
		var cliente = mockCliente(1);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.findByIdAndAtivo(1L);
		});
		
		String expectedMessage = "Cliente não existe ou está desativado";
		String actualMessage = ex.getMessage();
		
		assertTrue(actualMessage.equals(expectedMessage));
	}
	
	@Test
	@DisplayName("Deveria atualizar um cliente caso informações estejam corretas")
	void test_cenario05() {
		var cliente = mockCliente(1);
		var dadosAtualizar = new DadosAtualizarCliente("maxuel", null, null, null, null, null);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(cliente));
		
		var result = service.update(1L, dadosAtualizar);
		
		assertNotNull(result);
		assertNotNull(result.id());
		
		assertEquals(1L, result.id());
		assertEquals("maxuel", result.nome());
		assertEquals("sobrenome1", result.sobrenome());
		assertEquals("99232", result.telefone());
		assertEquals(LocalDate.of(1999, 1, 1), result.dataNascimento());
		assertEquals("rua", result.endereco().rua);
		assertEquals("bairro", result.endereco().bairro);
		assertEquals(22, result.endereco().numero);
	}
	
	@Test
	@DisplayName("Não deveria atualizar um cliente caso informações estejam incorretas")
	void test_cenario06() {
		var cliente = mockCliente(1);
		var dadosAtualizar = new DadosAtualizarCliente("maxuel", null, null, null, null, null);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.update(1L, dadosAtualizar);
		});
		
		String expectedMessage = "Cliente não existe ou está desativado";
		String actualMessage = ex.getMessage();
		
		assertTrue(actualMessage.equals(expectedMessage));
	}
	
	@Test
	@DisplayName("Deveria desativar um cliente ativo")
	void test_cenario07() {
		var cliente = mockCliente(1);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(cliente));
		
		service.desativar(1L);
	}
	
	@Test
	@DisplayName("Não deveria desativar um cliente desativado")
	void test_cenario08() {
		var cliente = mockCliente(1);
		
		when(clienteRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.desativar(1L);
		});
		
		String expectedMessage = "Cliente não existe ou está desativado";
		String actualMessage = ex.getMessage();
		
		assertTrue(actualMessage.equals(expectedMessage));
	}
	
	@Test
	@DisplayName("Deveria Ativar um cliente desativo")
	void test_cenario09() {
		var cliente = mockCliente(1);
		cliente.desativar();
		
		when(clienteRepository.findById(any())).thenReturn(Optional.of(cliente));
		
		service.ativar(1L);
	}
	
	@Test
	@DisplayName("Não deveria ativar um cliente ativado")
	void test_cenario10() {
		var cliente = mockCliente(1);
		
		when(clienteRepository.findById(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.ativar(1L);
		});
		
		String expectedMessage = "Cliente não existe ou está desativado";
		String actualMessage = ex.getMessage();
		
		assertTrue(actualMessage.equals(expectedMessage));
	}
	
	private Cliente mockCliente(int i) {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(Long.valueOf(i), "nome" + i, "sobrenome" + i, "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private DadosCriarCliente mockDadosCliente(Cliente cliente) {
		return new DadosCriarCliente(cliente.getNome(), cliente.getSobrenome(), cliente.getSexo(),
				cliente.getDataNascimento(), cliente.getTelefone(),
					new DadosEndereco(cliente.getEndereco().getBairro(),
							cliente.getEndereco().getNumero(), cliente.getEndereco().getRua()));
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
