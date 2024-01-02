package br.com.leuxam.acougue.domain.vendasEstoque;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosAtualizarVendaEstoque(
		Double quantidade,
		BigDecimal valorUnitario,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataEstoque) {

}
