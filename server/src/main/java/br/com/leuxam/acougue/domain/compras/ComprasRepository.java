package br.com.leuxam.acougue.domain.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.leuxam.acougue.domain.estoque.Estoque;

@Repository
public interface ComprasRepository extends JpaRepository<Compras, Long>{
	
	@Query("""
			SELECT MAX(c.data)
			FROM Compras c
			LEFT JOIN c.comprasEstoque ce
			WHERE ce.estoque = :estoque
			""")
	LocalDateTime searchDataRecente(Estoque estoque);
	
	@Query("""
			SELECT ce.precoUnitario
			FROM Compras c
			LEFT JOIN c.comprasEstoque ce
			WHERE ce.estoque = :estoque
			AND c.data = :dataRecente
			""")
	BigDecimal searchPrecoRecente(Estoque estoque, LocalDateTime dataRecente);
	
	@Query("""
			SELECT c
			FROM Compras c
			LEFT JOIN c.fornecedor f
			ON c.fornecedor.id = f.id
			WHERE f.razaoSocial LIKE %:razaoSocial%
			""")
	Page<Compras> findAllByRazaoFornecedor(Pageable pageable, String razaoSocial);
	
}
