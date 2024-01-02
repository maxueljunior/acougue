package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

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

import br.com.leuxam.acougue.domain.usuario.UserService;
import br.com.leuxam.acougue.domain.usuario.Usuario;
import br.com.leuxam.acougue.domain.usuario.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class UserServiceTest {
	
	@InjectMocks
	private UserService service;
	
	@Mock
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void beforeAll() {
		MockitoAnnotations.openMocks(this);
		service = new UserService(usuarioRepository);
	}
	
	@Test
	@DisplayName("Deveria retornar o usuario")
	void test_cenario01() {
		var usuario = mockUsuario();
		when(usuarioRepository.findByUsername(any())).thenReturn(usuario);
		
		var result = service.loadUserByUsername("maxuel");
		
		assertNotNull(result);
		
		assertEquals("maxuel", result.getUsername());
		assertEquals("1234", result.getPassword());
	}
	
	private Usuario mockUsuario() {
		return new Usuario(1L, "maxuel", "1234", null, LocalDateTime.now(), true);
	}

}
