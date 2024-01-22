package br.com.leuxam.acougue.domain.estoque;

public record DadosDetalhamentoEstoqueComQuantidade(
		Long id,
		String descricao,
		Unidade unidade,
		Double totalQuantidade) {

}
