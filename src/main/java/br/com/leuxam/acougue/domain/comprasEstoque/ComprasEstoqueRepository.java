package br.com.leuxam.acougue.domain.comprasEstoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprasEstoqueRepository extends JpaRepository<ComprasEstoque, Long>{
	
}
