package br.com.leuxam.acougue.domain.estoque;

public record DadosDetalhamentoEstoque(
		Long id,
		String descricao,
		Unidade unidade) {
	public DadosDetalhamentoEstoque(Estoque e) {
		this(e.getId(), e.getDescricao(), e.getUnidade());
	}
}
