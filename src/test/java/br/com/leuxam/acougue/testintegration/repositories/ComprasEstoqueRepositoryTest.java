package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosCompras;
import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ComprasEstoqueRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private ComprasEstoqueRepository comprasEstoqueRepository;
	
	private Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
	
	@Test
	@DisplayName("Deveria devolver todos produtos alocado em uma compra caso exista")
	void testFindByCompras() {
		var fornecedor = createFornecedor();
		var compras = createCompras(fornecedor);
		var estoque = createListEstoque();
		createListComprasEstoque(compras, estoque);
		
		var result = comprasEstoqueRepository.findByCompras(compras.getId(), pageable);
		
		assertNotNull(result);
		
		var ce1 = result.getContent().get(1);
		
		assertNotNull(ce1.getId());
		assertNotNull(ce1.getCompras().getId());
		assertNotNull(ce1.getEstoque().getId());
		assertEquals(new BigDecimal(1), ce1.getPrecoUnitario());
		assertEquals(1.0, ce1.getQuantidade());
		
		var ce3 = result.getContent().get(3);
		
		assertNotNull(ce3.getId());
		assertNotNull(ce3.getCompras().getId());
		assertNotNull(ce3.getEstoque().getId());
		assertEquals(new BigDecimal(3), ce3.getPrecoUnitario());
		assertEquals(3.0, ce3.getQuantidade());
	}

	@Test
	@DisplayName("Deveria devolver um produto alocado em uma compra caso exista")
	void testFindByComprasAndEstoque() {
		var fornecedor = createFornecedor();
		var compras = createCompras(fornecedor);
		var estoque = createEstoque();
		createComprasEstoque(compras, estoque);
		
		var result = comprasEstoqueRepository.findByComprasAndEstoque(compras, estoque);
		
		var ce1 = result.get();
		
		assertNotNull(ce1.getId());
		assertNotNull(ce1.getCompras().getId());
		assertNotNull(ce1.getEstoque().getId());
		assertEquals(new BigDecimal(1), ce1.getPrecoUnitario());
		assertEquals(1.0, ce1.getQuantidade());
	}
	
	@Test
	@DisplayName("Não deveria devolver um produto alocado em uma compra caso não exista")
	void testFindByComprasAndEstoque01() {
		var fornecedor = createFornecedor();
		var compras = createCompras(fornecedor);
		var estoque = createEstoque();
		
		var result = comprasEstoqueRepository.findByComprasAndEstoque(compras, estoque);
		
		assertFalse(result.isPresent());
	}
	
	private Fornecedor createFornecedor() {
		var fornecedor = mockFornecedor();
		em.persist(fornecedor);
		return fornecedor;
	}
	
	private Compras createCompras(Fornecedor f) {
		var compras = mockCompras(f);
		em.persist(compras);
		return compras;
	}
	
	private List<Estoque> createListEstoque(){
		List<Estoque> result = new ArrayList<>();
		
		for(int i = 0; i < 5; i++) {
			var estoque = mockProduto(i);
			em.persist(estoque);
			result.add(estoque);
		}
		
		return result;
	}
	
	private void createComprasEstoque(Compras c, Estoque p) {
		var comprasEstoque = mockCompraEstoque(c, p, 1);
		em.persist(comprasEstoque);
	}
	
	private Estoque createEstoque() {
		var estoque = mockProduto(1);
		em.persist(estoque);
		return estoque;
	}
	
	private void createListComprasEstoque(Compras c, List<Estoque> p) {
		var compras = c;
		for(int i = 0; i < p.size(); i++) {
			var comprasEstoque = mockCompraEstoque(c, p.get(i), i);
			em.persist(comprasEstoque);
		}
	}
	
	private Fornecedor mockFornecedor() {
		return new Fornecedor(null, "razao social", "cnpj", "telefone", "contato", true);
	}
	
	private Compras mockCompras(Fornecedor f) {
		var fornecedor = f;
		List<ArquivosCompras> arquivos = new ArrayList<>();
		return new Compras(null, fornecedor, new BigDecimal("0.0"), null, arquivos, LocalDateTime.of(1999, 1, 1, 1, 1));
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(null, "Carne"+i, Unidade.KG, null, null, null, null);
		return produto;
	}
	
	private ComprasEstoque mockCompraEstoque(Compras c, Estoque p, int i) {
		var prod = p;
		var comp = c;
		return new ComprasEstoque(null, prod, comp, new BigDecimal(i), Double.valueOf(i));
	}
	
}
