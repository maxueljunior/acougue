package br.com.leuxam.acougue.domain.compras;

import jakarta.validation.constraints.NotNull;

public record DadosCriarVendas(
		@NotNull
		Long idCliente) {
	
}
