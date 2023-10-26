package br.com.leuxam.acougue.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosLogin(
		@NotBlank
		String username,
		@NotBlank
		String password) {

}
