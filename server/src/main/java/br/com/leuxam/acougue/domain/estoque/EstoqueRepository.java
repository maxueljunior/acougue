package br.com.leuxam.acougue.domain.estoque;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long>{
	
	@Query("""
			SELECT new br.com.leuxam.acougue.domain.estoque.IdProdutosEstoque(e.id)
			FROM Estoque e
			""")
	List<IdProdutosEstoque> findIdsAll();
	
	@Query("""
			SELECT e
			FROM Estoque e
			WHERE e.descricao LIKE %:descricao%
			AND e.ativo = true
			""")
	Page<Estoque> searchEstoqueByAtivoTrueAndLikeDescricao(String descricao, Pageable pageable);
	
	Optional<Estoque> findByIdAndAtivoTrue(Long id);
}
