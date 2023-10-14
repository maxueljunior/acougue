package br.com.leuxam.acougue.domain.fornecedor;

public record DadosDetalhamentoFornecedor(
		Long id,
		String razaoSocial,
		String cnpj,
		String nomeContato,
		String telefone) {

	public DadosDetalhamentoFornecedor(Fornecedor f) {
		this(f.getId(), f.getRazaoSocial(), f.getCnpj(), f.getNomeContato(), f.getTelefone());
	}
}
