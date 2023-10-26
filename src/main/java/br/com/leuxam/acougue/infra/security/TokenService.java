package br.com.leuxam.acougue.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import br.com.leuxam.acougue.domain.usuario.Usuario;

@Service
public class TokenService {
	
	@Value("${secret.key.jwt.auth}")
	private String secretKey;
	
	public String createToken(Usuario usuario) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secretKey);
			return JWT.create()
					.withIssuer("API with açougue")
					.withSubject(usuario.getUsername())
					.withExpiresAt(dataExpiracao())
					.withClaim("roles", usuario.getPermissoes())
					.sign(algorithm);
		}catch(JWTCreationException e) {
			throw new JWTCreationException(e.getMessage(), e);
		}
	}
	
	public String getSubject(String token) {
		DecodedJWT decodedJWT;
		try {
			Algorithm algorithm = Algorithm.HMAC256(secretKey);
			JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer("API with açougue")
					.build();
			return verifier.verify(token).getSubject();
		}catch(JWTVerificationException e) {
			throw new JWTVerificationException(e.getMessage());
		}
	}

	private Instant dataExpiracao() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
}
