package br.com.leuxam.acougue.domain.cliente;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.leuxam.acougue.domain.endereco.Endereco;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Cliente")
@Table(name = "tb_cliente")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	private String sobrenome;
	private String telefone;
	
	@JoinColumn(name = "data_nascimento")
	private LocalDate dataNascimento;
	
	@Embedded
	private Endereco endereco;
	
	private BigDecimal lucratividade;
	
	@Enumerated(EnumType.STRING)
	private Sexo sexo;
}
