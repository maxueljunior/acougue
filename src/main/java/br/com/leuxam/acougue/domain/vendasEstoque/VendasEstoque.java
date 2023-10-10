package br.com.leuxam.acougue.domain.vendasEstoque;

import java.math.BigDecimal;

import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.vendas.Vendas;
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

@Entity(name = "VendasEstoque")
@Table(name = "tb_vendas_estoque")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VendasEstoque {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vendas")
	private Vendas vendas;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estoque")
	private Estoque estoque;
	
	@JoinColumn(name = "valor_unitario")
	private BigDecimal valorUnitario;
	
	private Double quantidade;
}
