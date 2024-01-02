package br.com.leuxam.acougue.domain.clienteEstoque;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.leuxam.acougue.domain.cliente.Cliente;
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

@Entity(name = "ClienteEstoque")
@Table(name = "tb_cliente_estoque")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ClienteEstoque {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_estoque")
	private Estoque estoque;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	private BigDecimal lucratividade;
	
	@JoinColumn(name = "data_atualizado")
	private LocalDateTime dataAtualizado;

	public void atualizar(BigDecimal lucratividade) {
		this.lucratividade = lucratividade;
		this.dataAtualizado = LocalDateTime.now();
	}
}
