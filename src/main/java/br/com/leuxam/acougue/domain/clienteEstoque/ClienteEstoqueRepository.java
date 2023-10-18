package br.com.leuxam.acougue.domain.clienteEstoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteEstoqueRepository extends JpaRepository<ClienteEstoque, Long>{
	
}
