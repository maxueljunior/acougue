package br.com.leuxam.acougue.domain.fornecedor;

import jakarta.validation.constraints.NotBlank;

public record DadosCriarFornecedor(
		@NotBlank
		String razaoSocial,
		@NotBlank
		String cnpj,
		@NotBlank
		String nomeContato,
		@NotBlank
		String telefone) {
	
}
