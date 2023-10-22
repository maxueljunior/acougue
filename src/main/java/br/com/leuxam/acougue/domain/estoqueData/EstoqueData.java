package br.com.leuxam.acougue.domain.estoqueData;

import java.time.LocalDate;

import br.com.leuxam.acougue.domain.estoque.Estoque;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "EstoqueData")
@Table(name = "tb_estoque_data")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EstoqueData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_estoque")
	private Estoque estoque;

	private Double quantidade;
	
	@JoinColumn(name = "data_compra")
	private LocalDate dataCompra;
	
	@JoinColumn(name = "data_validade")
	private LocalDate dataValidade;

	public void atualizarQuantidade(Double quantidade) {
		this.quantidade -= quantidade;
	}

}













