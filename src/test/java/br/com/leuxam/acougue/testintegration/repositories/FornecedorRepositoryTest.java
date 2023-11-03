package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorRepository;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class FornecedorRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private FornecedorRepository repository;
	
	private Pageable pageable = PageRequest.of(0, 5, Sort.by("razaoSocial"));
	
	@Test
	void testSearchFornecedorByAtivoTrueAndLikeRazao() {
		cadastrarFornecedores();
		var result = repository.searchFornecedorByAtivoTrueAndLikeRazao("soc", pageable);
		
		var f1 = result.getContent().get(1);
		
		assertNotNull(f1.getId());
		assertEquals("razao social 1", f1.getRazaoSocial());
		assertEquals("11.111.111/0001-11", f1.getCnpj());
		assertEquals("telefone 1", f1.getTelefone());
		assertEquals("nome contato 1", f1.getNomeContato());
		assertTrue(f1.getAtivo());
		
		var f2 = result.getContent().get(2);
		
		assertNotNull(f2.getId());
		assertEquals("razao social 2", f2.getRazaoSocial());
		assertEquals("11.111.111/0001-11", f2.getCnpj());
		assertEquals("telefone 2", f2.getTelefone());
		assertEquals("nome contato 2", f2.getNomeContato());
		assertTrue(f2.getAtivo());
	}

	@Test
	@DisplayName("Deveria retornar o fornecedor caso esteja ativo")
	void testFindByIdAndAtivoTrue() {
		var fornecedor = cadastrarFornecedor();
		var result = repository.findByIdAndAtivoTrue(fornecedor.getId());
		
		var f = result.get();
		
		assertNotNull(f.getId());
		assertEquals("razao social 1", f.getRazaoSocial());
		assertEquals("11.111.111/0001-11", f.getCnpj());
		assertEquals("telefone 1", f.getTelefone());
		assertEquals("nome contato 1", f.getNomeContato());
		assertTrue(f.getAtivo());
	}
	
	@Test
	@DisplayName("Não deveria retornar o fornecedor caso não esteja ativo, ou não exista")
	void testFindByIdAndAtivoTrue01() {
		var result = repository.findByIdAndAtivoTrue(1L);
		
		assertFalse(result.isPresent());
	}
	
	private void cadastrarFornecedores() {
		for(int i = 0; i < 3; i++) {
			var f = mockFornecedor(i);
			em.persist(f);
		}
	}
	
	private Fornecedor cadastrarFornecedor() {
		var f = mockFornecedor(1);
		em.persist(f);
		return f;
	}
	
	private Fornecedor mockFornecedor(int i) {
		return new Fornecedor(null, "razao social " + i, "11.111.111/0001-11",
				"telefone " + i, "nome contato "  + i, true);
	}
}
