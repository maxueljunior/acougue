package br.com.leuxam.acougue.domain.estoqueData;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosDetalhamentoEstoqueData(
		Long id,
		Long idEstoque,
		Double quantidade,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataCompra,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataValidade) {
	
	public DadosDetalhamentoEstoqueData(EstoqueData ed) {
		this(ed.getId(), ed.getEstoque().getId(), ed.getQuantidade(), ed.getDataCompra(), ed.getDataValidade());
	}
	
}
