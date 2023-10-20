package br.com.leuxam.acougue.domain.clienteEstoque;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.estoque.Estoque;

@Repository
public interface ClienteEstoqueRepository extends JpaRepository<ClienteEstoque, Long>{

	ClienteEstoque findByClienteAndEstoque(Cliente cliente, Estoque estoque);
	
	@Query("""
			SELECT new br.com.leuxam.acougue.domain.clienteEstoque.ResumoLucratividade(e.descricao, AVG(ce.lucratividade))
			FROM ClienteEstoque ce
			INNER JOIN ce.estoque e
			WHERE ce.estoque = e
			GROUP BY e.descricao
			""")
	List<ResumoLucratividade> gerarResumoLucratividade();
}
