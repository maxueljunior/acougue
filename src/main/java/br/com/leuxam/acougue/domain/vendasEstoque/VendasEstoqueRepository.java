package br.com.leuxam.acougue.domain.vendasEstoque;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
	@Query("""
			SELECT ve
			FROM VendasEstoque ve
			WHERE ve.vendas.id = :id
			""")
	List<VendasEstoque> findAllVendasEstoque(
			@Param("id") Long id);
	
	Optional<VendasEstoque> findByVendasAndEstoque(Vendas vendas, Estoque estoque);
	
}
