package br.com.leuxam.acougue.domain.comprasEstoque;

import java.math.BigDecimal;

public record DadosDetalhamentoComprasEstoque(
		Long id,
		BigDecimal precoUnitario,
		Double quantidade,
		Long idCompras,
		Long idEstoque) {
	
}
