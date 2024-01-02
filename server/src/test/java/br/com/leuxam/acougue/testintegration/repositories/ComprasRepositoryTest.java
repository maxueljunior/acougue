package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ComprasRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private ComprasRepository comprasRepository;
	
	@Test
	void testSearchDataRecente() {
		var fornecedor = createFornecedor();
		var compras = createCompras(fornecedor);
		var estoque = createEstoque();
		createComprasEstoque(compras, estoque);
		
		var result = comprasRepository.searchDataRecente(estoque);
		
		assertEquals(LocalDateTime.of(1999, 1, 1, 1, 1), result);
	}

	@Test
	void testSearchPrecoRecente() {
		var fornecedor = createFornecedor();
		var compras = createCompras(fornecedor);
		var estoque = createEstoque();
		createComprasEstoque(compras, estoque);
		var dataRecente = comprasRepository.searchDataRecente(estoque);
		
		var result = comprasRepository.searchPrecoRecente(estoque, dataRecente);
		
		assertEquals(new BigDecimal(1).setScale(2, RoundingMode.UP), result);
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
	
	private void createComprasEstoque(Compras c, Estoque p) {
		var comprasEstoque = mockCompraEstoque(c, p, 1);
		em.persist(comprasEstoque);
	}
	
	private Estoque createEstoque() {
		var estoque = mockProduto(1);
		em.persist(estoque);
		return estoque;
	}
	
	private Fornecedor mockFornecedor() {
		return new Fornecedor(null, "razao social", "cnpj", "telefone", "contato", true);
	}
	
	private Compras mockCompras(Fornecedor f) {
		var fornecedor = f;
		return new Compras(null, fornecedor, new BigDecimal(1), null, null, LocalDateTime.of(1999, 1, 1, 1, 1));
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
