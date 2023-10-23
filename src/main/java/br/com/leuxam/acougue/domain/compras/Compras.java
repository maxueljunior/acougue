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
import jakarta.persistence.Lob;
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
	
	@Lob
	private byte[] dat;
	
	@JoinColumn(name = "file_name")
	private String fileName;
	
	@JoinColumn(name = "file_type")
	private String fileType;
	
	public Compras(Long id, Fornecedor fornecedor, BigDecimal valorTotal, List<ComprasEstoque> comprasEstoque,
			LocalDateTime data) {
		this.id = id;
		this.fornecedor = fornecedor;
		this.valorTotal = valorTotal;
		this.comprasEstoque = comprasEstoque;
		this.data = data;
	}
	
	public Compras(Long id, byte[] dat, String fileName, String fileType) {
		this.id = id;
		this.dat = dat;
		this.fileName = fileName;
		this.fileType = fileType;
	}

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
