package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.Unidade;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EstoqueRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private EstoqueRepository estoqueRepository;

	@Test
	void testFindIdsAll() {
		criarProdutos();
		
		var result = estoqueRepository.findIdsAll();
		
		assertNotNull(result);
		
		assertNotNull(result.get(1).id());
		assertNotNull(result.get(2).id());
		assertNotNull(result.get(3).id());
	}
	
	private void criarProdutos() {
		for(int i = 0; i < 5; i++) {
			var produto = mockProduto();
			em.persist(produto);
		}
	}
	
	private Estoque mockProduto() {
		return new Estoque(null, "Carne", Unidade.KG, null,null,null,null);
	}
}
