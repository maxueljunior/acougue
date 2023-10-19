package br.com.leuxam.acougue.domain.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosDetalhamentoCompras(
		Long id,
		BigDecimal valorTotal,
		Long idFornecedor,
		@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
		LocalDateTime data) {
	
	public DadosDetalhamentoCompras(Compras c) {
		this(c.getId(), c.getValorTotal(), c.getFornecedor().getId(), c.getData());
	}
}
