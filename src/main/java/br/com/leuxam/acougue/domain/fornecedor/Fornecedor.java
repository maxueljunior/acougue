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
	
	private Boolean ativo;
	
	public Fornecedor(DadosCriarFornecedor dados) {
		this.razaoSocial = dados.razaoSocial();
		this.cnpj = dados.cnpj();
		this.telefone = dados.telefone();
		this.nomeContato = dados.nomeContato();
		this.ativo = true;
	}

	public void atualizar(DadosAtualizacaoFornecedor dados) {
		if(dados.razaoSocial() != null) this.razaoSocial = dados.razaoSocial();
		if(dados.cnpj() != null) this.cnpj = dados.cnpj();
		if(dados.nomeContato() != null) this.nomeContato = dados.nomeContato();
		if(dados.telefone() != null) this.telefone = dados.telefone();
	}

	public void desativar() {
		this.ativo = false;
	}

	public void ativar() {
		this.ativo = true;
	}

}
