package br.com.leuxam.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.leuxam.acougue.domain.usuario.CredencialsException;
import br.com.leuxam.acougue.domain.usuario.DadosLogin;
import br.com.leuxam.acougue.domain.usuario.Usuario;
import br.com.leuxam.acougue.infra.security.DadosAutenticaticao;
import br.com.leuxam.acougue.infra.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/login")
@Tag(description = "Login", name = "Login")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private TokenService tokenService;
	
	@Operation(summary = "Autenticação")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = DadosAutenticaticao.class))}),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content)
	})
	@PostMapping
	public ResponseEntity<DadosAutenticaticao> login(
			@RequestBody @Valid DadosLogin dados){
		
		var authenticate = new UsernamePasswordAuthenticationToken(dados.username(), dados.password());
		
		try {
			var user = authenticationManager.authenticate(authenticate);
			var token = tokenService.createToken((Usuario) user.getPrincipal());
			
			return ResponseEntity.ok().body(new DadosAutenticaticao(token));
		}catch(AuthenticationException ex) {
			throw new CredencialsException("Token invalido ou expirado!");
		}
	}
	
}
