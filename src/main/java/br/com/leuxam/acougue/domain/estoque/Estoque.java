package br.com.leuxam.acougue.domain.estoque;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "Estoque")
@Table(name = "tb_estoque")
public class Estoque {
	
	private Long id;
	
	private String descricao;
	private BigDecimal quantidade;
	
}
