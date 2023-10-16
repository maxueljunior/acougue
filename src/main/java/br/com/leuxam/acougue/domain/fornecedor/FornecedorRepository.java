package br.com.leuxam.acougue.domain.fornecedor;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long>{
	
	@Query("""
			SELECT f
			FROM Fornecedor f
			WHERE f.razaoSocial LIKE %:razao%
			AND f.ativo = true
			""")
	Page<Fornecedor> searchFornecedorByAtivoTrueAndLikeRazao(String razao, Pageable pageable);
	
	Optional<Fornecedor> findByIdAndAtivoTrue(Long id);
	
}
