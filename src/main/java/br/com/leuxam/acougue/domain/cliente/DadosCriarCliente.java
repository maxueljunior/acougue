package br.com.leuxam.acougue.domain.cliente;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.leuxam.acougue.domain.cliente.endereco.DadosEndereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCriarCliente(
		@NotBlank
		String nome,
		@NotBlank
		String sobrenome,
		@NotNull
		Sexo sexo,
		@JsonFormat(pattern = "dd/MM/yyyy")
		@NotNull
		LocalDate dataNascimento,
		@NotBlank
		String telefone,
		DadosEndereco endereco) {

}
