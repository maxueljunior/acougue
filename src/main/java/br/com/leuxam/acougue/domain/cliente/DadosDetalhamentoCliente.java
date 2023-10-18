package br.com.leuxam.acougue.domain.cliente;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;

public record DadosDetalhamentoCliente(
		Long id,
		String nome,
		String sobrenome,
		String telefone,
		Sexo sexo,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate dataNascimento,
		Endereco endereco
//		BigDecimal lucratividade
		) {

	public DadosDetalhamentoCliente(Cliente cliente) {
		this(cliente.getId(), cliente.getNome(), cliente.getSobrenome(), cliente.getTelefone(),
				cliente.getSexo(), cliente.getDataNascimento(),
				cliente.getEndereco()
				/*, cliente.getLucratividade()*/);
	}

}
