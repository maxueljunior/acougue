package br.com.leuxam.acougue.domain.estoque;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosDetalhamentoEstoque(
		Long id,
		String descricao,
		Double quantidade,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataCompra,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataValidade,
		Unidade unidade) {
	public DadosDetalhamentoEstoque(Estoque e) {
		this(e.getId(), e.getDescricao(), e.getQuantidade(), e.getDataCompra(),
				e.getDataValidade(), e.getUnidade());
	}
}
