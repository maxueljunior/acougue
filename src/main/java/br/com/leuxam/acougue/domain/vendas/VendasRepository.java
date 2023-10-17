package br.com.leuxam.acougue.domain.vendas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VendasRepository extends JpaRepository<Vendas, Long>{

	@Query("""
			SELECT v
			FROM Vendas v
			WHERE v.cliente.id = :idCliente
			""")
	Page<Vendas> findVendasIdCliente(Pageable pageable, Long idCliente);
	
}
