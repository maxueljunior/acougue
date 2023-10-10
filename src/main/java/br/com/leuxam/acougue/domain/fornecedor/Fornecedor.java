package br.com.leuxam.acougue.domain.fornecedor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Fornecedor")
@Table(name = "tb_fornecedor")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Fornecedor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JoinColumn(name = "razao_social")
	private String razaoSocial;
	
	private String cnpj;
	private String telefone;
	
	@JoinColumn(name = "nome_contato")
	private String nomeContato;
}
