package br.com.leuxam.acougue.domain.compras;

public record DadosArquivoCompras(
		String fileName,
		String downloadUrl,
		String fileType,
		Long fileSize) {

}
