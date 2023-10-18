package br.com.leuxam.acougue.domain.clienteEstoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoque;

@Repository
public interface ClienteEstoqueRepository extends JpaRepository<ComprasEstoque, Long>{
	
}
