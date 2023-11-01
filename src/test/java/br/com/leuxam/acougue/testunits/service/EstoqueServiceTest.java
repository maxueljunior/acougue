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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.IdCliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.DadosAtualizarEstoque;
import br.com.leuxam.acougue.domain.estoque.DadosCriarEstoque;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueService;
import br.com.leuxam.acougue.domain.estoque.Unidade;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class EstoqueServiceTest {
	
	@InjectMocks
	private EstoqueService service;
	
	@Mock
	private EstoqueRepository estoqueRepository;

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private ClienteEstoqueRepository clienteEstoqueRepository;
	
	private Pageable pageable;
	
	@BeforeAll
	void beforeAll() {
		MockitoAnnotations.openMocks(this);
		service = new EstoqueService(estoqueRepository, clienteRepository, clienteEstoqueRepository);
		pageable = PageRequest.of(0, 5, Sort.by("descricao"));
	}
	
	@Test
	@DisplayName("Deveria salvar caso informações estejam corretas")
	void test_cenario01() {
		var produto = mockProduto(1);
		var listaId = mockIdClientes();
		
		when(clienteRepository.findIdAlls()).thenReturn(listaId);
		when(estoqueRepository.save(any())).thenReturn(produto);
		var result = service.create(new DadosCriarEstoque(produto.getDescricao(), produto.getUnidade()));
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals("Carne1", result.descricao());
		assertEquals(Unidade.KG, result.unidade());
		
		verify(clienteRepository).getReferenceById(any());
		verify(clienteEstoqueRepository).save(any());
	}
	
	@Test
	@DisplayName("Deveria retornar todos os produtos")
	void test_cenario02() {
		var produto = mockListProdutos();
		
		when(estoqueRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(produto));
		
		var result = service.findAll(pageable);
		
		assertNotNull(result);
		
		var produto1 = result.getContent().get(1);
		
		assertEquals(1L, produto1.id());
		assertEquals("Carne1", produto1.descricao());
		assertEquals(Unidade.KG, produto1.unidade());
		
		var produto3 = result.getContent().get(3);
		
		assertEquals(3L, produto3.id());
		assertEquals("Carne3", produto3.descricao());
		assertEquals(Unidade.KG, produto3.unidade());
	}
	
	@Test
	@DisplayName("Deveria retornar o produto pelo ID")
	void test_cenario03() {
		var produto = mockProduto(1);
		
		when(estoqueRepository.findById(any())).thenReturn(Optional.of(produto));
		
		var result = service.findById(1L);
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals("Carne1", result.descricao());
		assertEquals(Unidade.KG, result.unidade());
	}
	
	@Test
	@DisplayName("Não deveria retornar o produto pelo ID caso não exista")
	void test_cenario04() {
		var produto = mockProduto(1);
		
		when(estoqueRepository.findById(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.findById(1L);
		});
		
		String expectedMessage = "O produto nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria atualizar caso exista um produto")
	void test_cenario05() {
		var produto = mockProduto(1);
		
		when(estoqueRepository.findById(any())).thenReturn(Optional.of(produto));
		
		var result = service.update(1L, new DadosAtualizarEstoque("Maminha"));
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals("Maminha", result.descricao());
		assertEquals(Unidade.KG, result.unidade());
	}
	
	@Test
	@DisplayName("Não deveria atualizar caso não exista um produto")
	void test_cenario06() {
		var produto = mockProduto(1);
		
		when(estoqueRepository.findById(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.update(1L, new DadosAtualizarEstoque("Maminha"));
		});
		
		String expectedMessage = "O produto nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(Long.valueOf(i), "Carne"+i, Unidade.KG, null, null, null, null);
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
































