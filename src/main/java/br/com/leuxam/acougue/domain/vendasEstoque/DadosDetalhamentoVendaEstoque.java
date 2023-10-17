package br.com.leuxam.acougue.domain.vendasEstoque;

import java.math.BigDecimal;

public record DadosDetalhamentoVendaEstoque(
		Long id,
		Double quantidade,
		BigDecimal valorUnitario,
		Long idEstoque,
		Long idVendas) {

	public DadosDetalhamentoVendaEstoque(VendasEstoque ve) {
		this(ve.getId(), ve.getQuantidade(), ve.getValorUnitario(), ve.getEstoque().getId(),
				ve.getVendas().getId());
	}
	
}
