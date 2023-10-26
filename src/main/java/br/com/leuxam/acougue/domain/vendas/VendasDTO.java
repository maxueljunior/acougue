package br.com.leuxam.acougue.domain.vendas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

public class VendasDTO extends RepresentationModel<VendasDTO>{
	
	private Long id;
	private CondicaoPagamento condicaoPagamento;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime dataVenda;
	private BigDecimal valorTotal;
	private Long idCliente;
	
	private String fileName;
	
	public VendasDTO() {}
	
	public VendasDTO(Long id, CondicaoPagamento condicaoPagamento, LocalDateTime dataVenda, BigDecimal valorTotal,
			Long idCliente, String fileName) {
		this.id = id;
		this.condicaoPagamento = condicaoPagamento;
		this.dataVenda = dataVenda;
		this.valorTotal = valorTotal;
		this.idCliente = idCliente;
		this.fileName = fileName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CondicaoPagamento getCondicaoPagamento() {
		return condicaoPagamento;
	}

	public void setCondicaoPagamento(CondicaoPagamento condicaoPagamento) {
		this.condicaoPagamento = condicaoPagamento;
	}

	public LocalDateTime getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(LocalDateTime dataVenda) {
		this.dataVenda = dataVenda;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(condicaoPagamento, dataVenda, fileName, id, idCliente, valorTotal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendasDTO other = (VendasDTO) obj;
		return condicaoPagamento == other.condicaoPagamento && Objects.equals(dataVenda, other.dataVenda)
				&& Objects.equals(fileName, other.fileName) && Objects.equals(id, other.id)
				&& Objects.equals(idCliente, other.idCliente) && Objects.equals(valorTotal, other.valorTotal);
	}
}
