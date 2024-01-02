package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.leuxam.acougue.domain.ExisteException;
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
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class VendasEstoqueServiceTest {
	
	@InjectMocks
	private VendasEstoqueService service;
	
	@Mock
	private VendasEstoqueRepository vendasEstoqueRepository;

	@Mock
	private VendasRepository vendasRepository;

	@Mock
	private EstoqueRepository estoqueRepository;

	@Mock
	private ComprasRepository comprasRepository;

	@Mock
	private ClienteEstoqueRepository clienteEstoqueRepository;

	@Mock
	private EstoqueDataRepository estoqueDataRepository;
	
	private Pageable pageable;
	
	@BeforeAll
	void beforeAll() {
		MockitoAnnotations.openMocks(this);
		pageable = PageRequest.of(0, 10, Sort.by("id"));
	}
	
	@Test
	@DisplayName("Deveria salvar caso dados estejam corretos")
	void test_cenario01() {
		var vendas = mockVendas();
		var estoque = mockProduto();
		var estoqueData = mockEstoqueData();
		var clienteEstoque = mockClienteEstoque();
		
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(estoqueRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.findById(any())).thenReturn(Optional.of(vendas));
		when(estoqueRepository.findById(any())).thenReturn(Optional.of(estoque));
		when(estoqueDataRepository.findByEstoqueAndDataCompra(any(), any())).thenReturn(Optional.of(estoqueData));
		when(comprasRepository.searchDataRecente(any())).thenReturn(LocalDateTime.now());
		when(comprasRepository.searchPrecoRecente(any(), any())).thenReturn(new BigDecimal("50"));
		when(clienteEstoqueRepository.findByClienteAndEstoque(any(), any())).thenReturn(clienteEstoque);
		
		var dadosCriar = new DadosCriarVendaEstoque(1L, 1L, 2.0, new BigDecimal("100"),LocalDate.of(1999, 1, 1));
		var result = service.create(dadosCriar);
		
		assertNotNull(result);
		
		assertEquals(null, result.id());
		assertEquals(1L, result.idEstoque());
		assertEquals(1L, result.idVendas());
		assertEquals(2.0, result.quantidade());
		assertEquals(new BigDecimal("100"), result.valorUnitario());
		
	}
	
	@Test
	@DisplayName("Não deveria salvar caso dados estejam incorretos")
	void test_cenario02() {
		
		when(vendasRepository.existsById(any())).thenReturn(false);
		
		var dadosCriar = new DadosCriarVendaEstoque(1L, 1L, 2.0, new BigDecimal("100"),LocalDate.of(1999, 1, 1));
		
		Exception ex = assertThrows(ExisteException.class, ()->{
			service.create(dadosCriar);
		});
		
		String expectedMessage = "A venda nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria devolver todos os produtos alocados em todas as vendas")
	void test_cenario03() {
		var vendasEstoque = mockListVendasProdutos();
		
		when(vendasEstoqueRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(vendasEstoque));
		
		var result = service.findAll(pageable);
		
		assertNotNull(result);
		
		var ve1 = result.getContent().get(1);
		
		assertEquals(1L, ve1.id());
		assertEquals(1L, ve1.idEstoque());
		assertEquals(1L, ve1.idVendas());
		assertEquals(2.0, ve1.quantidade());
		assertEquals(new BigDecimal("1"), ve1.valorUnitario());
		
		var ve3 = result.getContent().get(3);
		
		assertEquals(3L, ve3.id());
		assertEquals(1L, ve3.idEstoque());
		assertEquals(1L, ve3.idVendas());
		assertEquals(2.0, ve3.quantidade());
		assertEquals(new BigDecimal("3"), ve3.valorUnitario());
	}
	
	@Test
	@DisplayName("Deveria devolver todos os produtos alocados em uma venda")
	void test_cenario04() {
		var vendasEstoque = mockListVendasProdutos();
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(vendasEstoqueRepository.findByVendas(any(), any(Pageable.class))).thenReturn(new PageImpl<>(vendasEstoque));
		
		var result = service.findById(1L, pageable);
		
		assertNotNull(result);
		
		var ve1 = result.getContent().get(1);
		
		assertEquals(1L, ve1.id());
		assertEquals(1L, ve1.idEstoque());
		assertEquals(1L, ve1.idVendas());
		assertEquals(2.0, ve1.quantidade());
		assertEquals(new BigDecimal("1"), ve1.valorUnitario());
		
		var ve3 = result.getContent().get(3);
		
		assertEquals(3L, ve3.id());
		assertEquals(1L, ve3.idEstoque());
		assertEquals(1L, ve3.idVendas());
		assertEquals(2.0, ve3.quantidade());
		assertEquals(new BigDecimal("3"), ve3.valorUnitario());
	}
	
	@Test
	@DisplayName("Não deveria devolver todos os produtos alocados em uma venda caso ela não exista")
	void test_cenario05() {
		var vendasEstoque = mockListVendasProdutos();
		
		when(vendasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, ()->{
			service.findById(1L, pageable);
		});
		
		String expectedMessage = "A venda nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria excluir caso os dados estejam corretos")
	void test_cenario06() {
		var vendasEstoque = mockVendaProdutos(1);
		var vendas = mockVendas();
		var estoque = mockProduto();
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(estoqueRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.findById(any())).thenReturn(Optional.of(vendas));
		when(estoqueRepository.findById(any())).thenReturn(Optional.of(estoque));
		when(vendasEstoqueRepository.findByVendasAndEstoque(any(), any())).thenReturn(Optional.of(vendasEstoque));
		
		service.delete(1L, 1L);
		
		verify(vendasEstoqueRepository).delete(any());
	}
	
	@Test
	@DisplayName("Não deveria excluir caso os dados estejam incorretos")
	void test_cenario07() {
		when(vendasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, ()->{
			service.delete(1L, 1L);
		});
		
		String expectedMessage = "A venda nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria atualizar caso os dados estejam corretos")
	void test_cenario08() {
		var vendasEstoque = mockVendaProdutos(1);
		var vendas = mockVendas();
		var estoque = mockProduto();
		var estoqueData = mockEstoqueData();
		var clienteEstoque = mockClienteEstoque();
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(estoqueRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.findById(any())).thenReturn(Optional.of(vendas));
		when(estoqueRepository.findById(any())).thenReturn(Optional.of(estoque));
		when(vendasEstoqueRepository.findByVendasAndEstoque(any(), any())).thenReturn(Optional.of(vendasEstoque));
		when(estoqueDataRepository.findByEstoqueAndDataCompra(any(), any())).thenReturn(Optional.of(estoqueData));
		when(comprasRepository.searchDataRecente(any())).thenReturn(LocalDateTime.now());
		when(comprasRepository.searchPrecoRecente(any(), any())).thenReturn(new BigDecimal("50"));
		when(clienteEstoqueRepository.findByClienteAndEstoque(any(), any())).thenReturn(clienteEstoque);
		
		var dadosAtualizar = new DadosAtualizarVendaEstoque(5.0, new BigDecimal("500"), LocalDate.of(1999, 1, 1));
		var result = service.update(1L, 1L, dadosAtualizar);
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals(1L, result.idEstoque());
		assertEquals(1L, result.idVendas());
		assertEquals(5.0, result.quantidade());
		assertEquals(new BigDecimal("500"), result.valorUnitario());
	}
	
	@Test
	@DisplayName("Não deveria atualizar caso os dados estejam incorretos")
	void test_cenario09() {
		when(vendasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, ()->{
			service.delete(1L, 1L);
		});
		
		String expectedMessage = "A venda nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
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
	
	private VendasEstoque mockVendaProdutos(int i) {
		var vendas = mockVendas();
		var produto = mockProduto();
		var vendaEstoque = new VendasEstoque(Long.valueOf(i), vendas, produto, new BigDecimal(i), 2.0);
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
			result.add(mockVendaProdutos(i));
		}
		
		return result;
	}
}



























