package br.com.leuxam.acougue.domain.vendas;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.compras.DadosCriarVendas;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosAtualizarVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.DadosCriarVendaEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	@ManyToOne
	private Cliente cliente;
	
	@Enumerated(EnumType.STRING)
	private CondicaoPagamento condicaoPagamento;
	
	@Lob
	private byte[] data;
	
	@JoinColumn(name = "file_name")
	private String fileName;
	
	@OneToMany(mappedBy = "vendas")
	private List<VendasEstoque> vendasEstoque;
	
	public Vendas(Cliente c, CondicaoPagamento condicaoPagamento) {
		this.cliente = c;
		this.dataVenda = LocalDateTime.now(ZoneOffset.ofHours(-3));
		this.condicaoPagamento = condicaoPagamento;
		this.valorTotal = BigDecimal.ZERO;
	}

	public void atualizar(DadosAtualizarVenda dados) {
		if(dados.condicaoPagamento() != null) this.condicaoPagamento = dados.condicaoPagamento();
	}

	public void atualizar(DadosCriarVendaEstoque dados) {
		var valorTotal = this.valorTotal.add(dados.valorUnitario()
				.multiply(BigDecimal.valueOf(dados.quantidade())));
		this.valorTotal = valorTotal;
	}

	public void atualizar(VendasEstoque vendaEstoque) {
		var valorTotal = this.valorTotal.subtract(vendaEstoque.getValorUnitario().multiply(BigDecimal.valueOf(vendaEstoque.getQuantidade())));
		this.valorTotal = valorTotal;
	}

	public void arquivarPdf(ByteArrayOutputStream outputStream, String tituloArquivo) {
		this.data = outputStream.toByteArray();
		this.fileName = tituloArquivo;
	}

}
