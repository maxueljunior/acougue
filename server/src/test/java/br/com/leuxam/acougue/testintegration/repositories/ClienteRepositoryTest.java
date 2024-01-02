package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ClienteRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	private Pageable pageable = PageRequest.of(0, 10);

	@Test
	void testFindAllByAtivoTrue() {
		createListCliente();
		
		var result = clienteRepository.findAllByAtivoTrue(pageable);
		
		var c1 = result.getContent().get(1);
		
		assertNotNull(c1.getId());
		assertEquals("nome1", c1.getNome());
		assertEquals("sobrenome1", c1.getSobrenome());
		assertEquals("99232", c1.getTelefone());
		assertEquals(LocalDate.of(1999, 1, 1), c1.getDataNascimento());
		assertEquals(Sexo.M, c1.getSexo());
		assertEquals("rua", c1.getEndereco().getRua());
		assertEquals("bairro", c1.getEndereco().getBairro());
		assertEquals(22, c1.getEndereco().getNumero());
		assertTrue(c1.getAtivo());
		
		var c3 = result.getContent().get(3);
		
		assertNotNull(c3.getId());
		assertEquals("nome3", c3.getNome());
		assertEquals("sobrenome3", c3.getSobrenome());
		assertEquals("99232", c3.getTelefone());
		assertEquals(LocalDate.of(1999, 1, 1), c3.getDataNascimento());
		assertEquals(Sexo.M, c3.getSexo());
		assertEquals("rua", c3.getEndereco().getRua());
		assertEquals("bairro", c3.getEndereco().getBairro());
		assertEquals(22, c3.getEndereco().getNumero());
		assertTrue(c3.getAtivo());
	}

	@Test
	void testQueryByFindAllByAtivoAndNome() {
		createListCliente();
		
		var result = clienteRepository.queryByFindAllByAtivoAndNome("1",pageable);
		
		var c1 = result.getContent().get(0);
		
		assertNotNull(c1.getId());
		assertEquals("nome1", c1.getNome());
		assertEquals("sobrenome1", c1.getSobrenome());
		assertEquals("99232", c1.getTelefone());
		assertEquals(LocalDate.of(1999, 1, 1), c1.getDataNascimento());
		assertEquals(Sexo.M, c1.getSexo());
		assertEquals("rua", c1.getEndereco().getRua());
		assertEquals("bairro", c1.getEndereco().getBairro());
		assertEquals(22, c1.getEndereco().getNumero());
		assertTrue(c1.getAtivo());
	}

	@Test
	@DisplayName("Deveria devolver o cliente caso exista e esteja ativo")
	void testFindByIdAndAtivoTrue() {
		var cliente = createCliente();
		
		var result = clienteRepository.findByIdAndAtivoTrue(cliente.getId());
		
		var c1 = result.get();
		
		assertNotNull(c1.getId());
		assertEquals("nome1", c1.getNome());
		assertEquals("sobrenome1", c1.getSobrenome());
		assertEquals("99232", c1.getTelefone());
		assertEquals(LocalDate.of(1999, 1, 1), c1.getDataNascimento());
		assertEquals(Sexo.M, c1.getSexo());
		assertEquals("rua", c1.getEndereco().getRua());
		assertEquals("bairro", c1.getEndereco().getBairro());
		assertEquals(22, c1.getEndereco().getNumero());
		assertTrue(c1.getAtivo());
	}
	
	@Test
	@DisplayName("Não deveria devolver o cliente caso não exista ou esteja desativado")
	void testFindByIdAndAtivoTrue01() {
		var cliente = createCliente();
		cliente.desativar();
		var result = clienteRepository.findByIdAndAtivoTrue(cliente.getId());
		
		assertFalse(result.isPresent());
	}


	@Test
	void testFindIdAlls() {
		createListCliente();
		
		var result = clienteRepository.findIdAlls();
		
		assertEquals(5, result.size());
		
		assertNotNull(result.get(0).id());
		assertNotNull(result.get(1).id());
		assertNotNull(result.get(2).id());
		assertNotNull(result.get(3).id());
		assertNotNull(result.get(4).id());
	}
	
	private Cliente createCliente() {
		var cliente = mockCliente(1);
		em.persist(cliente);
		return cliente;
	}
	
	private void createListCliente() {
		for(int i = 0; i < 5; i++) {
			var cliente = mockCliente(i);
			em.persist(cliente);
		}
	}
	
	private Cliente mockCliente(int i) {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(null, "nome" + i, "sobrenome" + i, "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
}
