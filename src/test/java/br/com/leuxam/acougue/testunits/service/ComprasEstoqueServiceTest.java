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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosCompras;
import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueService;
import br.com.leuxam.acougue.domain.comprasEstoque.DadosCriarComprasEstoque;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueData;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueDataRepository;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ComprasEstoqueServiceTest {
	
	private ComprasEstoqueService service;
	
	@MockBean
	private ComprasEstoqueRepository comprasEstoqueRepository;

	@MockBean
	private ComprasRepository comprasRepository;

	@MockBean
	private EstoqueRepository estoqueRepository;

	@MockBean
	private EstoqueDataRepository estoqueDataRepository;
	
	private Pageable pageable;
	
	@BeforeAll
	void beforeAll() {
		service = new ComprasEstoqueService(comprasEstoqueRepository,
				comprasRepository, estoqueRepository, estoqueDataRepository);
		pageable = PageRequest.of(0, 10);
	}
	
	@Test
	@DisplayName("Deveria salvar caso informações estejam corretas")
	void test_cenario01() {
		var compras = mockCompras(1);
		var estoque = mockProduto(1);
		var compraEstoque = mockCompraEstoque(1);
		var dadosCriar = dadosCriar(compraEstoque);
		
		when(comprasRepository.existsById(any())).thenReturn(true);
		when(comprasRepository.findById(any())).thenReturn(Optional.of(compras));
		when(estoqueRepository.existsById(any())).thenReturn(true);
		when(estoqueRepository.getReferenceById(any())).thenReturn(estoque);
		when(comprasEstoqueRepository.save(any())).thenReturn(compraEstoque);
		
		var result = service.create(dadosCriar);
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals(1L, result.idCompras());
		assertEquals(1L, result.idEstoque());
		assertEquals(new BigDecimal("1"), result.precoUnitario());
		assertEquals(1.0, result.quantidade());
		
		verify(estoqueDataRepository).save(any());
	}
	
	@Test
	@DisplayName("Não deveria salvar caso informações estejam incorretas")
	void test_cenario02() {
		var compras = mockCompras(1);
		var estoque = mockProduto(1);
		var compraEstoque = mockCompraEstoque(1);
		var dadosCriar = dadosCriar(compraEstoque);
		
		when(comprasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.create(dadosCriar);
		});
		
		String expectedMessage = "A compra nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria devolver todos os produtos alocados em todas as compras")
	void test_cenario03() {
		var compraEstoque = mockListCompraEstoque();
		
		when(comprasEstoqueRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(compraEstoque));
		var result = service.findAll(pageable);
		
		assertNotNull(result);
		
		var compraEstoque1 = result.getContent().get(1);
		
		assertNotNull(compraEstoque1);
		
		assertEquals(1L, compraEstoque1.id());
		assertEquals(1L, compraEstoque1.idCompras());
		assertEquals(1L, compraEstoque1.idEstoque());
		assertEquals(new BigDecimal("1"), compraEstoque1.precoUnitario());
		assertEquals(1.0, compraEstoque1.quantidade());
		
		var compraEstoque3 = result.getContent().get(3);
		
		assertNotNull(compraEstoque3);
		
		assertEquals(3L, compraEstoque3.id());
		assertEquals(3L, compraEstoque3.idCompras());
		assertEquals(3L, compraEstoque3.idEstoque());
		assertEquals(new BigDecimal("3"), compraEstoque3.precoUnitario());
		assertEquals(3.0, compraEstoque3.quantidade());
	}
	
	@Test
	@DisplayName("Deveria devolver todos os produtos alocados em uma compras")
	void test_cenario04() {
		var compraEstoque = mockListCompraEstoqueMesmoId();
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasEstoqueRepository.findByCompras(any(), any(Pageable.class))).thenReturn(new PageImpl<>(compraEstoque));
		
		var result = service.findById(1L, pageable);
		
		assertNotNull(result);
		
		var compraEstoque1 = result.getContent().get(1);
		
		assertNotNull(compraEstoque1);
		
		assertEquals(1L, compraEstoque1.id());
		assertEquals(1L, compraEstoque1.idCompras());
		assertEquals(1L, compraEstoque1.idEstoque());
		assertEquals(new BigDecimal("1"), compraEstoque1.precoUnitario());
		assertEquals(1.0, compraEstoque1.quantidade());
		
		var compraEstoque3 = result.getContent().get(3);
		
		assertNotNull(compraEstoque3);
		
		assertEquals(3L, compraEstoque3.id());
		assertEquals(1L, compraEstoque3.idCompras());
		assertEquals(3L, compraEstoque3.idEstoque());
		assertEquals(new BigDecimal("3"), compraEstoque3.precoUnitario());
		assertEquals(3.0, compraEstoque3.quantidade());
	}
	
	@Test
	@DisplayName("Não deveria devolver todos os produtos alocados em uma compras caso não exista")
	void test_cenario05() {
		var compraEstoque = mockListCompraEstoqueMesmoId();
		
		when(comprasRepository.existsById(1L)).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.findById(1L, pageable);
		});
		
		String expectedMessage = "A compra nº 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria excluir caso as informações estejam corretas")
	void test_cenario06() {
		var compras = mockCompras(1);
		var estoque = mockProduto(1);
		var compraEstoque = mockCompraEstoque(1);
		var estoqueData = mockEstoqueData(estoque);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.findById(1L)).thenReturn(Optional.of(compras));
		when(estoqueRepository.existsById(1L)).thenReturn(true);
		when(estoqueRepository.getReferenceById(1L)).thenReturn(estoque);
		when(comprasEstoqueRepository.findByComprasAndEstoque(any(), any())).thenReturn(Optional.of(compraEstoque));
		when(estoqueDataRepository.findByEstoqueAndQuantidadeAndDataCompra(any(), any(), any())).thenReturn(Optional.of(estoqueData));
		
		service.delete(1L, 1L);
		
		verify(estoqueDataRepository).delete(any());
		verify(comprasEstoqueRepository).delete(any());
	}
	
	@Test
	@DisplayName("Não deveria excluir caso as informações estejam incorretas")
	void test_cenario07() {
		var compras = mockCompras(1);
		var estoque = mockProduto(1);
		var compraEstoque = mockCompraEstoque(1);
		var estoqueData = mockEstoqueData(estoque);
		
		when(comprasRepository.existsById(1L)).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.findById(1L, pageable);
		});
		
		String expectedMessage = "A compra nº 1 não existe";
		String actualMessage = ex.getMessage();
	}
	
	private DadosCriarComprasEstoque dadosCriar(ComprasEstoque compraEstoque) {
		return new DadosCriarComprasEstoque(compraEstoque.getPrecoUnitario(),
				compraEstoque.getQuantidade(), compraEstoque.getCompras().getId(),
				compraEstoque.getEstoque().getId());
	}

	private Fornecedor mockFornecedor(int i) {
		return new Fornecedor(Long.valueOf(i), "razao social", "cnpj", "telefone", "contato", true);
	}
	
	private Compras mockCompras(int i) {
		var fornecedor = mockFornecedor(1);
		List<ArquivosCompras> arquivos = new ArrayList<>();
		return new Compras(Long.valueOf(i), fornecedor, new BigDecimal("0.0"), null, arquivos, LocalDateTime.of(1999, 1, 1, 1, 1));
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(Long.valueOf(i), "Carne"+i, Unidade.KG, null, null, null, null);
		return produto;
	}
	
	private ComprasEstoque mockCompraEstoque(int i) {
		var prod = mockProduto(i);
		var comp = mockCompras(i);
		return new ComprasEstoque(Long.valueOf(i), prod, comp, new BigDecimal(i), Double.valueOf(i));
	}
	
	private List<ComprasEstoque> mockListCompraEstoque(){
		List<ComprasEstoque> result = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			result.add(mockCompraEstoque(i));
		}
		return result;
	}
	
	private List<ComprasEstoque> mockListCompraEstoqueMesmoId(){
		var comp = mockCompras(1);
		
		List<ComprasEstoque> result = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			var prod = mockProduto(i);
			result.add(new ComprasEstoque(Long.valueOf(i), prod, comp, new BigDecimal(i), Double.valueOf(i)));
		}
		return result;
	}
	
	private EstoqueData mockEstoqueData(Estoque produto) {
		var estoque = produto;
		return new EstoqueData(1L, estoque, 1.0, LocalDate.of(1999, 1, 1), LocalDate.of(1999, 2, 1));
	}
}



















