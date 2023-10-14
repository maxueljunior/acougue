package br.com.leuxam.acougue.domain;

public class ExisteException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ExisteException(String msg) {
		super(msg);
	}
	
}
