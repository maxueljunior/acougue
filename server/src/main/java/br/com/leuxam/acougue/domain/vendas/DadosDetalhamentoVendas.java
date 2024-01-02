package br.com.leuxam.acougue.domain.vendas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosDetalhamentoVendas(
		Long id,
		CondicaoPagamento condicaoPagamento,
		@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
		LocalDateTime dataVenda,
		BigDecimal valorTotal,
		Long idCliente) {

	public DadosDetalhamentoVendas(Vendas vendas) {
		this(vendas.getId(), vendas.getCondicaoPagamento(), vendas.getDataVenda(),
				vendas.getValorTotal(), vendas.getCliente().getId());
	}
	
}
