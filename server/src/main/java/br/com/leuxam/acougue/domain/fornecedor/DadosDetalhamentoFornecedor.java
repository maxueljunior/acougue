package br.com.leuxam.acougue.domain.fornecedor;

public record DadosDetalhamentoFornecedor(
		Long id,
		String razaoSocial,
		String cnpj,
		String nomeContato,
		String telefone) {
	
	public DadosDetalhamentoFornecedor(Fornecedor fornecedor) {
		this(fornecedor.getId(), fornecedor.getRazaoSocial(), fornecedor.getCnpj(),
				fornecedor.getNomeContato(), fornecedor.getTelefone());
	}
}
