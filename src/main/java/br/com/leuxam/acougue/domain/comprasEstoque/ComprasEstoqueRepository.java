package br.com.leuxam.acougue.domain.comprasEstoque;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.estoque.Estoque;

@Repository
public interface ComprasEstoqueRepository extends JpaRepository<ComprasEstoque, Long>{
	
	@Query("""
			SELECT ce
			FROM ComprasEstoque ce
			WHERE ce.compras.id = :id
			""")
	Page<ComprasEstoque> findByCompras(Long id, Pageable pageable);
	
	Optional<ComprasEstoque> findByComprasAndEstoque(Compras compras, Estoque estoque);
	
}
