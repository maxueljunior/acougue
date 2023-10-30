package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.acougue.domain.permissoes.Permissoes;
import br.com.leuxam.acougue.domain.usuario.CredencialsException;
import br.com.leuxam.acougue.domain.usuario.DadosLogin;
import br.com.leuxam.acougue.domain.usuario.Usuario;
import br.com.leuxam.acougue.infra.security.DadosAutenticaticao;
import br.com.leuxam.acougue.infra.security.TokenService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AuthenticationControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private AuthenticationManager authenticationManager;
	
	@MockBean
	private TokenService tokenService;
	
	@Autowired
	private JacksonTester<DadosAutenticaticao> dadosAutent;

	@Autowired
	private JacksonTester<DadosLogin> dadosLogin;
	
	@Test
	@DisplayName("Deveria realizar o login, retornar o token e codigo HTTP 200")
	void test_cenario01() throws Exception {
		var usuario = mockUsuario();
		
		var authenticationToken = new UsernamePasswordAuthenticationToken(usuario.getUsername(),
				usuario.getPassword());
		
		var authenticate = new UsernamePasswordAuthenticationToken(usuario, authenticationToken.getCredentials(), usuario.getAuthorities());
		
		var token = geraToken();
		
		when(authenticationManager.authenticate(any())).thenReturn(authenticate);
		when(tokenService.createToken(any())).thenReturn(token);
		
		var result = mvc.perform(post("/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(dadosLogin.write(new DadosLogin("maxuel",
									"1234")).getJson()
									)
						).andReturn().getResponse();
		
		var jsonEsperado = dadosAutent.write(new DadosAutenticaticao(token)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(authenticationManager, times(1)).authenticate(any());
		verify(tokenService, times(1)).createToken(any());
	}
	
	@Test
	@DisplayName("Não deveria realizar o login, retornar o token e codigo HTTP 403")
	void test_cenario02() throws Exception {
		
		when(authenticationManager.authenticate(any())).thenThrow(CredencialsException.class);
		
		var result = mvc.perform(post("/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(dadosLogin.write(new DadosLogin("ab",
									"ac")).getJson()
									)
						).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
		verify(authenticationManager, times(1)).authenticate(any());
		verify(tokenService, times(0)).createToken(any());
	}
	
	private Usuario mockUsuario() {
		List<Permissoes> permissoes = new ArrayList<>();
		permissoes.add(new Permissoes(1L, "ADMIN", null));
		return new Usuario(1L, "maxuel", "1234", permissoes, LocalDateTime.now(), true);
	}
	
	private String geraToken() {
		return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJBUEkgYWx1cmEgY2hhbGxlbmdlIDEiLCJzdWIiOiJtYXh1ZWwiLCJleHAiOjE2OTU3NDc0ODZ9.tbl_BtgDtOl8j0BOaX6DNjhZ6jPLU_Dg0DkqV0iB30M";
	}
}
