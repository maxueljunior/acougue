package br.com.leuxam.acougue.domain.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;
import br.com.leuxam.acougue.domain.comprasEstoque.DadosCriarComprasEstoque;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Compras")
@Table(name = "tb_compras")
@AllArgsConstructor
@NoArgsConstructor
@Getter
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
	
	private LocalDateTime data;

	/*
	 * 
	 * Aqui ficara faltando a parte de download e upload de Pdf's para deixarem eles
	 * salvos e conseguir consulta-lós através do Id
	 * 
	 * 
	 */

	public void atualizar(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public void atualizarValorCompra(DadosCriarComprasEstoque dados) {
		BigDecimal valorTotal = this.valorTotal.add(dados.precoUnitario().multiply(BigDecimal.valueOf(dados.quantidade())));
		this.valorTotal = valorTotal;
	}

	public void atualizarValorCompra(ComprasEstoque comprasEstoque) {
		BigDecimal valorTotal = this.valorTotal.subtract(comprasEstoque.getPrecoUnitario().multiply(BigDecimal.valueOf(comprasEstoque.getQuantidade())));
		this.valorTotal = valorTotal;
	}
}
