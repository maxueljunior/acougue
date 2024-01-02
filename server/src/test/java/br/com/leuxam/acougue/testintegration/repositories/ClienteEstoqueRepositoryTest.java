package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoque;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.Unidade;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ClienteEstoqueRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private ClienteEstoqueRepository clienteEstoqueRepository;
	
	private Pageable pageable = PageRequest.of(0, 10);
	
	@Test
	void testFindByClienteAndEstoque() {
		var estoque = createProduto();
		var cliente = createCliente();
		var clienteEstoque = createClienteEstoque(estoque, cliente);
		
		var result = clienteEstoqueRepository.findByClienteAndEstoque(cliente, estoque);
		
		assertNotNull(result.getId());
		assertNotNull(result.getCliente().getId());
		assertNotNull(result.getEstoque().getId());
		assertEquals(new BigDecimal(1), result.getLucratividade());
		assertNotNull(result.getDataAtualizado());
	}

	@Test
	void testGerarResumoLucratividade() {
		var estoque = createProduto();
		var cliente = createCliente();
		var clienteEstoque = createClienteEstoque(estoque, cliente);
		
		var result = clienteEstoqueRepository.gerarResumoLucratividade(pageable);
		
		var lucrat1 = result.getContent().get(0);
		
		assertEquals("Carne1", lucrat1.descricao());
		assertEquals(1.0, lucrat1.total());
	}

	private Estoque createProduto() {
		var produto = mockProduto(1);
		em.persist(produto);
		return produto;
	}
	
	private Cliente createCliente() {
		var cliente = mockCliente();
		em.persist(cliente);
		return cliente;
	}
	
	private ClienteEstoque createClienteEstoque(Estoque p, Cliente c) {
		var clienteEstoque = mockClienteEstoque(p, c, 1);
		em.persist(clienteEstoque);
		return clienteEstoque;
	}
	
	private ClienteEstoque mockClienteEstoque(Estoque p, Cliente c, int i) {
		var estoque = p;
		var cliente = c;
		var clienteEstoque = new ClienteEstoque(null, estoque, cliente, new BigDecimal(i), LocalDateTime.now());
		return clienteEstoque;
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(null, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(null, "Carne"+i, Unidade.KG, null, null, null, null);
		return produto;
	}
}
