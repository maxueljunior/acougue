package br.com.leuxam.acougue.domain.vendas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Vendas")
@Table(name = "tb_vendas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vendas {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JoinColumn(name = "data_venda")
	private LocalDateTime dataVenda;
	
	@JoinColumn(name = "valor_total")
	private BigDecimal valorTotal;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Cliente cliente;
	
	@Enumerated(EnumType.STRING)
	private CondicaoPagamento condicaoPagamento;
	
	@OneToMany(mappedBy = "vendas")
	private List<VendasEstoque> vendasEstoque;
}
