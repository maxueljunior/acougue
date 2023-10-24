package br.com.leuxam.acougue.domain.arquivosCompras;

public record DadosDetalhamentoArquivo(
		String fileName,
		String fileType,
		Long size,
		String downloadUrl) {
	
}
