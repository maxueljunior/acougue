package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.vendas.CondicaoPagamento;
import br.com.leuxam.acougue.domain.vendas.Vendas;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VendasRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private VendasRepository repository;
	
	private Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
	
	@Test
	void testFindVendasIdCliente() {
		var cliente = cadastrarCliente();
		cadastrarListVendas(cliente);
		
		var result = repository.findVendasIdCliente(pageable, cliente.getId());
		
		var venda1 = result.getContent().get(1);
		
		assertNotNull(venda1.getId());
		assertNotNull(venda1.getCliente().getId());
		assertNotNull(venda1.getDataVenda());
		assertEquals(new BigDecimal("1"), venda1.getValorTotal());
		assertEquals(CondicaoPagamento.PIX, venda1.getCondicaoPagamento());
		
		var venda2 = result.getContent().get(2);
		
		assertNotNull(venda2.getId());
		assertNotNull(venda2.getCliente().getId());
		assertNotNull(venda2.getDataVenda());
		assertEquals(new BigDecimal("2"), venda2.getValorTotal());
		assertEquals(CondicaoPagamento.PIX, venda2.getCondicaoPagamento());
		
	}
	
	private Cliente cadastrarCliente() {
		var cliente = mockCliente();
		em.persist(cliente);
		return cliente;
	}
	
	private Vendas cadastrarVendas(Cliente c) {
		var vendas = mockVendas(1,c);
		em.persist(vendas);
		return vendas;
	}
	
	private void cadastrarListVendas(Cliente c) {
		for(int i = 0; i < 3; i++) {
			var vendas = mockVendas(i, c);
			em.persist(vendas);
		}
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(null, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Vendas mockVendas(int i, Cliente c) {
		var cliente = c;
		var venda = new Vendas(null, LocalDateTime.of(1999, 1, 1, 1, 1), new BigDecimal(i), cliente, CondicaoPagamento.PIX, null, null, null);
		return venda;
	}

}
