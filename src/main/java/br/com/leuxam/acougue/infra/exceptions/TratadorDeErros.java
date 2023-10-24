package br.com.leuxam.acougue.infra.exceptions;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.leuxam.acougue.domain.AtivadoException;
import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.arquivosCompras.FileException;

@RestControllerAdvice
public class TratadorDeErros {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity ErroValidacao400(MethodArgumentNotValidException ex) {
		var errors = ex.getFieldErrors().stream().map(DadosErro::new).collect(Collectors.toList());
		return ResponseEntity.badRequest().body(errors);
	}
	
	@ExceptionHandler({ValidacaoException.class, FileException.class})
	public ResponseEntity ErroBuscaPorId404(RuntimeException ex) {
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler({AtivadoException.class, ExisteException.class})
	public ResponseEntity ErroAtivacao400(RuntimeException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
	
	public record DadosErro(String field, String message) {
		
		public DadosErro(FieldError ex) {
			this(ex.getField(), ex.getDefaultMessage());
		}
	}
}
