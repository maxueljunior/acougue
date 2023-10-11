package br.com.leuxam.acougue.domain.fornecedor;

public record DadosDetalhamentoFornecedor(
		Long id,
		String razaoSocial,
		String cnpj,
		String nomeContato,
		String telefone) {
	
}
