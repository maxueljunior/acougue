package br.com.leuxam.acougue.domain.cliente;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{

	Page<Cliente> findAllByAtivoTrue(Pageable pageable);
	
	@Query("""
			SELECT c
			FROM Cliente c
			WHERE c.nome LIKE %:nome%
			AND c.ativo = true
			""")
	Page<Cliente> queryByFindAllByAtivoAndNome(
			@Param("nome") String nome,
			Pageable pageable);

	Optional<Cliente> findByIdAndAtivoTrue(Long id);
	
}
