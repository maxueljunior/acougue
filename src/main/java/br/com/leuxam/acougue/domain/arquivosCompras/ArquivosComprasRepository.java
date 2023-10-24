package br.com.leuxam.acougue.domain.arquivosCompras;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.leuxam.acougue.domain.compras.Compras;

@Repository
public interface ArquivosComprasRepository extends JpaRepository<ArquivosCompras, Long>{

	Optional<ArquivosCompras> findByCompras(Compras compras);

}
