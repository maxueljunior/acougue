package br.com.leuxam.acougue.domain.estoque;

import java.util.List;

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
}
