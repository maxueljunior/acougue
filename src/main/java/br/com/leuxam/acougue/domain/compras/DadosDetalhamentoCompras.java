package br.com.leuxam.acougue.domain.compras;

import java.math.BigDecimal;

public record DadosDetalhamentoCompras(
		Long id,
		BigDecimal valorTotal,
		Long idFornecedor) {
	
	public DadosDetalhamentoCompras(Compras c) {
		this(c.getId(), c.getValorTotal(), c.getFornecedor().getId());
	}
}
