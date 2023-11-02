package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VendasEstoqueRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private VendasEstoqueRepository vendasEstoqueRepository;

	@Test
	void testFindByVendas() {
		fail("Not yet implemented");
	}

	@Test
	void testFindAllVendasEstoque() {
		fail("Not yet implemented");
	}

	@Test
	void testFindByVendasAndEstoque() {
		fail("Not yet implemented");
	}

}
