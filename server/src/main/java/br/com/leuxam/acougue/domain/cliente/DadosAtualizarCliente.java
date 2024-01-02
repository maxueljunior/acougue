package br.com.leuxam.acougue.domain.cliente;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.leuxam.acougue.domain.cliente.endereco.DadosEndereco;

public record DadosAtualizarCliente(
		String nome,
		String sobrenome,
		Sexo sexo,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataNascimento,
		String telefone,
		DadosEndereco endereco) {
	
}
