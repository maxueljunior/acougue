package br.com.leuxam.acougue.domain.estoque;

import java.time.LocalDate;
import java.util.List;

import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Estoque")
@Table(name = "tb_estoque")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Estoque {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String descricao;
	private Double quantidade;
	
	@JoinColumn(name = "data_compra")
	private LocalDate dataCompra;
	
	@JoinColumn(name = "data_validade")
	private LocalDate dataValidade;
	
	@Enumerated(EnumType.STRING)
	private Unidade unidade;
	
	@OneToMany(mappedBy = "estoque")
	private List<VendasEstoque> vendasEstoque;
	
	@OneToMany(mappedBy = "estoque")
	private List<ComprasEstoque> comprasEstoque;
	
	public Estoque(DadosCriarEstoque dados) {
		this.descricao = dados.descricao();
		this.unidade = dados.unidade();
		this.quantidade = 0.0;
		if (dados.quantidade() != null) this.quantidade = dados.quantidade();
		this.dataCompra = LocalDate.of(1900, 1, 1);
		this.dataValidade = LocalDate.of(1900, 1, 1);
	}

	public void atualizar(DadosAtualizarEstoque dados) {
		if(dados.descricao() != null) this.descricao = dados.descricao();
	}

	public void atualizarQuantidadeEstoque(Double quantidade) {
		this.quantidade += quantidade;
		this.dataCompra = LocalDate.now();
		this.dataValidade = LocalDate.now().plusMonths(1);
	}

	public void atualizarQuantidadeEstoque(ComprasEstoque comprasEstoque) {
		this.quantidade -= comprasEstoque.getQuantidade();
	}
}
