package br.com.leuxam.acougue.domain.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;

public class ComprasDTO extends RepresentationModel<ComprasDTO>{
	
	private Long id;
	private BigDecimal valorTotal;
	
	private Fornecedor fornecedor;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime data;
	
	public ComprasDTO() {}
	
	public ComprasDTO(Long id, BigDecimal valorTotal, Fornecedor fornecedor, LocalDateTime data) {
		this.id = id;
		this.valorTotal = valorTotal;
		this.fornecedor = fornecedor;
		this.data = data;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, fornecedor, id, valorTotal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComprasDTO other = (ComprasDTO) obj;
		return Objects.equals(data, other.data) && Objects.equals(fornecedor, other.fornecedor)
				&& Objects.equals(id, other.id) && Objects.equals(valorTotal, other.valorTotal);
	}
}
