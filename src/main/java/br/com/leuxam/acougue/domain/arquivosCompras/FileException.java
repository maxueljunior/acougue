package br.com.leuxam.acougue.domain.arquivosCompras;

public class FileException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public FileException(String msg) {
		super(msg);
	}
}
