package br.com.leuxam.acougue.domain.estoqueData;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.leuxam.acougue.domain.estoque.Estoque;

@Repository
public interface EstoqueDataRepository extends JpaRepository<EstoqueData, Long>{

	Optional<EstoqueData> findByEstoqueAndQuantidadeAndDataCompra(Estoque estoque, Double quantidade, LocalDate localDate);
	
	Optional<EstoqueData> findByEstoqueAndDataCompra(Estoque estoque, LocalDate localDate);
}
