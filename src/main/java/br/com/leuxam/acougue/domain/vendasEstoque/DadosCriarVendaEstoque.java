package br.com.leuxam.acougue.domain.vendasEstoque;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

public record DadosCriarVendaEstoque(
		@NotNull
		Long idEstoque,
		@NotNull
		Long idVendas,
		@NotNull
		Double quantidade,
		@NotNull
		BigDecimal valorUnitario,
		@NotNull
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataEstoque) {
	
}
