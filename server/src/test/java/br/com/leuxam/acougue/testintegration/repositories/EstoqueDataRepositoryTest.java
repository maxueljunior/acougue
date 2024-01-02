package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueData;
import br.com.leuxam.acougue.domain.estoqueData.EstoqueDataRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EstoqueDataRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private EstoqueDataRepository estoqueDataRepository;
	
	@Test
	@DisplayName("Deveria devolver a o Estoque Data caso exista passando produto, quantidade e data compra")
	void testFindByEstoqueAndQuantidadeAndDataCompra() {
		var produto = criarProduto();
		var estoqueData = criarEstoqueData(produto);
		
		var result = estoqueDataRepository.findByEstoqueAndQuantidadeAndDataCompra(produto, 5.0, LocalDate.of(1999, 1, 1));
		
		assertNotNull(result);
		
		var ed = result.get();
		
		assertNotNull(ed.getId());
		assertNotNull(ed.getEstoque().getId());
		assertEquals(5.0, ed.getQuantidade());
		assertEquals(LocalDate.of(1999, 1, 1), ed.getDataCompra());
		assertEquals(LocalDate.of(1999, 2, 1), ed.getDataValidade());
	}
	
	@Test
	@DisplayName("Não deveria devolver a o Estoque Data caso não exista passando produto, quantidade e data compra")
	void testFindByEstoqueAndQuantidadeAndDataCompra01() {
		var produto = criarProduto();
		
		var result = estoqueDataRepository.findByEstoqueAndQuantidadeAndDataCompra(produto, 5.0, LocalDate.of(1999, 1, 1));
		
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Deveria devolver a o Estoque Data caso exista passando produto e data compra")
	void testFindByEstoqueAndDataCompra() {
		var produto = criarProduto();
		var estoqueData = criarEstoqueData(produto);
		
		var result = estoqueDataRepository.findByEstoqueAndDataCompra(produto, LocalDate.of(1999, 1, 1));
		
		assertNotNull(result);
		
		var ed = result.get();
		
		assertNotNull(ed.getId());
		assertNotNull(ed.getEstoque().getId());
		assertEquals(5.0, ed.getQuantidade());
		assertEquals(LocalDate.of(1999, 1, 1), ed.getDataCompra());
		assertEquals(LocalDate.of(1999, 2, 1), ed.getDataValidade());
	}
	
	@Test
	@DisplayName("Não deveria devolver a o Estoque Data caso exista passando produto e data compra")
	void testFindByEstoqueAndDataCompra01() {
		var produto = criarProduto();
		
		var result = estoqueDataRepository.findByEstoqueAndDataCompra(produto, LocalDate.of(1999, 1, 1));
		
		assertFalse(result.isPresent());
	}

	private Estoque criarProduto() {
		var produto = mockProduto();
		em.persist(produto);
		return produto;
	}
	
	private EstoqueData criarEstoqueData(Estoque produto) {
		var estoqueData = mockEstoqueData(produto);
		em.persist(estoqueData);
		return estoqueData;
	}
	
	private Estoque mockProduto() {
		return new Estoque(null, "Carne", Unidade.KG, null, null, null, null);
	}
	
	private EstoqueData mockEstoqueData(Estoque produto) {
		return new EstoqueData(null, produto, 5.0, LocalDate.of(1999, 1, 1), LocalDate.of(1999, 2, 1));
	}
}
