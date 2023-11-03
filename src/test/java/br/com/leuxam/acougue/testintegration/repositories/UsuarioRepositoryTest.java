package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.usuario.Usuario;
import br.com.leuxam.acougue.domain.usuario.UsuarioRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UsuarioRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private UsuarioRepository repository;
	
	@Test
	void testFindByUsername() {
		cadastrarUsuario();
		var result = repository.findByUsername("maxuel");
		
		assertEquals("maxuel", result.getUsername());
		assertEquals("1234", result.getPassword());
		assertNull(result.getAuthorities());
	}
	
	private void cadastrarUsuario() {
		var usuario = mockUsuario();
		em.persist(usuario);
	}
	
	private Usuario mockUsuario() {
		return new Usuario(null, "maxuel", "1234", null, LocalDateTime.now(), true);
	}
}
