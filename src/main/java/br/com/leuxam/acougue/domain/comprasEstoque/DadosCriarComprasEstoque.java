package br.com.leuxam.acougue.domain.comprasEstoque;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record DadosCriarComprasEstoque(
		@NotNull
		BigDecimal precoUnitario,
		@NotNull
		Double quantidade,
		@NotNull
		Long idCompras,
		@NotNull
		Long idEstoque) {

}
