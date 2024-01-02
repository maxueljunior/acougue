package br.com.leuxam.acougue.domain.clienteEstoque;

public record ResumoLucratividade(
		String descricao,
		Double total) {
	
	public ResumoLucratividade(ResumoLucratividade r) {
		this(r.descricao, r.total);
	}
}
