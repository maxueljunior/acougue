package br.com.leuxam.acougue.domain.vendasEstoque;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.vendas.Vendas;

@Repository
public interface VendasEstoqueRepository extends JpaRepository<VendasEstoque, Long>{

	@Query("""
			SELECT ve
			FROM VendasEstoque ve
			WHERE ve.vendas.id = :id
			""")
	Page<VendasEstoque> findByVendas(Long id, Pageable pageable);
	
	Optional<VendasEstoque> findByVendasAndEstoque(Vendas vendas, Estoque estoque);
	
}
