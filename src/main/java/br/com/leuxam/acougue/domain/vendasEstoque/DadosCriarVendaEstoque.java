package br.com.leuxam.acougue.domain.vendasEstoque;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record DadosCriarVendaEstoque(
		@NotNull
		Long idEstoque,
		@NotNull
		Long idVendas,
		@NotNull
		Double quantidade,
		@NotNull
		BigDecimal valorUnitario) {
	
}
