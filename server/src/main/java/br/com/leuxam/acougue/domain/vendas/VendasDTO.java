package br.com.leuxam.acougue.domain.vendas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.leuxam.acougue.domain.cliente.Cliente;

public class VendasDTO extends RepresentationModel<VendasDTO>{
	
	private Long id;
	private CondicaoPagamento condicaoPagamento;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime dataVenda;
	
	private BigDecimal valorTotal;
	
	private Cliente cliente;
//	private Long idCliente;
	
	@JsonIgnore
	private String fileName;
	
	public VendasDTO() {}
	
	public VendasDTO(Long id, CondicaoPagamento condicaoPagamento, LocalDateTime dataVenda, BigDecimal valorTotal,
			 Cliente cliente, String fileName) {
		this.id = id;
		this.condicaoPagamento = condicaoPagamento;
		this.dataVenda = dataVenda;
		this.valorTotal = valorTotal;
		this.cliente = cliente;
//		this.idCliente = idCliente;
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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(cliente, condicaoPagamento, dataVenda, fileName, id, valorTotal);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendasDTO other = (VendasDTO) obj;
		return Objects.equals(cliente, other.cliente) && condicaoPagamento == other.condicaoPagamento
				&& Objects.equals(dataVenda, other.dataVenda) && Objects.equals(fileName, other.fileName)
				&& Objects.equals(id, other.id) && Objects.equals(valorTotal, other.valorTotal);
	}
}
