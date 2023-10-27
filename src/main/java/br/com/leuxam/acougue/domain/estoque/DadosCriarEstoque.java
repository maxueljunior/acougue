package br.com.leuxam.acougue.domain.estoque;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCriarEstoque(
		@NotBlank
		String descricao,
		@NotNull
		Unidade unidade) {

}
