package br.com.leuxam.acougue.domain.vendasEstoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendasEstoqueRepository extends JpaRepository<VendasEstoque, Long>{
	
}
