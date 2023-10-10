package br.com.leuxam.acougue.domain.compras;

import java.math.BigDecimal;
import java.util.List;

import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "Compras")
@Table(name = "tb_compras")
public class Compras {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Fornecedor fornecedor;
	
	@JoinColumn(name = "valor_total")
	private BigDecimal valorTotal;
	
	@OneToMany(mappedBy = "compras")
	private List<ComprasEstoque> comprasEstoque;
	
	/*
	 * 
	 * Aqui ficara faltando a parte de download e upload de Pdf's
	 * para deixarem eles salvos e conseguir consulta-lós através do Id
	 * 
	 * 
	 */
}
