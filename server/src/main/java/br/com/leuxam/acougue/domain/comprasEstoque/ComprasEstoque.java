package br.com.leuxam.acougue.domain.comprasEstoque;

import java.math.BigDecimal;

import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "ComprasEstoque")
@Table(name = "tb_compras_estoque")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComprasEstoque {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estoque")
	private Estoque estoque;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compras")
	private Compras compras;
	
	@JoinColumn(name = "preco_unitario")
	private BigDecimal precoUnitario;
	
	private Double quantidade;
}
