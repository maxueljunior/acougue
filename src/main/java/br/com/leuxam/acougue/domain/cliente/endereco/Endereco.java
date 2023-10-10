package br.com.leuxam.acougue.domain.cliente.endereco;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
	
	public String rua;
	public String bairro;
	public Integer numero;
	
	public Endereco(DadosEndereco endereco) {
		this.rua = endereco.rua();
		this.bairro = endereco.bairro();
		this.numero = endereco.numero();
	}
}
