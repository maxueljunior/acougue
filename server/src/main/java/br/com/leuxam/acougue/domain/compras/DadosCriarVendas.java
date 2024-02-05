package br.com.leuxam.acougue.domain.compras;

import br.com.leuxam.acougue.domain.vendas.CondicaoPagamento;
import jakarta.validation.constraints.NotNull;

public record DadosCriarVendas(
		@NotNull
		Long idCliente,
		@NotNull
		CondicaoPagamento condicaoPagamento) {
	
}
