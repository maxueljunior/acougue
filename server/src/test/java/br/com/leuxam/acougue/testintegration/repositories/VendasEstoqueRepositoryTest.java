package br.com.leuxam.acougue.testintegration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
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
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.vendas.CondicaoPagamento;
import br.com.leuxam.acougue.domain.vendas.Vendas;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VendasEstoqueRepositoryTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private VendasEstoqueRepository vendasEstoqueRepository;
	
	private Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

	@Test
	@DisplayName("Deveria devolver todos os produtos alocados em uma venda")
	void testFindByVendas() {
		var cliente = cadastrarCliente();
		var vendas = cadastrarVendas(cliente);
		var produtos = cadastrarProdutos();
		cadastrarVendasProdutos(vendas, produtos);
		
		var result = vendasEstoqueRepository.findByVendas(vendas.getId(), pageable);
		
		assertNotNull(result);
		
		var ve1 = result.getContent().get(1);
		
		assertNotNull(ve1.getId());
		assertNotNull(ve1.getEstoque().getId());
		assertNotNull(ve1.getVendas().getId());
		assertEquals(2.0, ve1.getQuantidade());
		assertEquals(new BigDecimal("1"), ve1.getValorUnitario());
		
		var ve3 = result.getContent().get(2);
		
		assertNotNull(ve3.getId());
		assertNotNull(ve3.getEstoque().getId());
		assertNotNull(ve3.getVendas().getId());
		assertEquals(2.0, ve3.getQuantidade());
		assertEquals(new BigDecimal("2"), ve3.getValorUnitario());
	}

	@Test
	@DisplayName("Deveria devolver todos os produtos alocados em uma venda, porem retornando uma lista")
	void testFindAllVendasEstoque() {
		var cliente = cadastrarCliente();
		var vendas = cadastrarVendas(cliente);
		var produtos = cadastrarProdutos();
		cadastrarVendasProdutos(vendas, produtos);
		
		var result = vendasEstoqueRepository.findAllVendasEstoque(vendas.getId());
		
		assertNotNull(result);
		
		var ve1 = result.get(1);
		
		assertNotNull(ve1.getId());
		assertNotNull(ve1.getEstoque().getId());
		assertNotNull(ve1.getVendas().getId());
		assertEquals(2.0, ve1.getQuantidade());
		assertEquals(new BigDecimal("1"), ve1.getValorUnitario());
		
		var ve3 = result.get(2);
		
		assertNotNull(ve3.getId());
		assertNotNull(ve3.getEstoque().getId());
		assertNotNull(ve3.getVendas().getId());
		assertEquals(2.0, ve3.getQuantidade());
		assertEquals(new BigDecimal("2"), ve3.getValorUnitario());
	}

	@Test
	@DisplayName("Deveria devolver um produto alocado em uma venda")
	void testFindByVendasAndEstoque() {
		var cliente = cadastrarCliente();
		var vendas = cadastrarVendas(cliente);
		var produto = cadastrarProduto();
		cadastrarVendasProduto(vendas, produto);
		
		var result = vendasEstoqueRepository.findByVendasAndEstoque(vendas, produto);
		
		var ve = result.get();
		
		assertNotNull(ve.getId());
		assertNotNull(ve.getEstoque().getId());
		assertNotNull(ve.getVendas().getId());
		assertEquals(2.0, ve.getQuantidade());
		assertEquals(new BigDecimal("1"), ve.getValorUnitario());
	}
	
	@Test
	@DisplayName("Não deveria devolver um produto alocado em uma venda caso não exista")
	void testFindByVendasAndEstoque01() {
		var cliente = cadastrarCliente();
		var vendas = cadastrarVendas(cliente);
		var produto = cadastrarProduto();
		
		var result = vendasEstoqueRepository.findByVendasAndEstoque(vendas, produto);
		
		assertFalse(result.isPresent());
	}
	
	private Cliente cadastrarCliente() {
		var cliente = mockCliente();
		em.persist(cliente);
		return cliente;
	}
	
	private Vendas cadastrarVendas(Cliente c) {
		var vendas = mockVendas(c);
		em.persist(vendas);
		return vendas;
	}
	
	private List<Estoque> cadastrarProdutos(){
		List<Estoque> result = new ArrayList<>();
		
		for(int i = 0; i < 3; i++) {
			var produto = mockProduto(i);
			em.persist(produto);
			result.add(produto);
		}
		
		return result;
	}
	
	private Estoque cadastrarProduto() {
		var produto = mockProduto(1);
		em.persist(produto);
		return produto;
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(null, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Vendas mockVendas(Cliente c) {
		var cliente = c;
		var venda = new Vendas(null, LocalDateTime.of(1999, 1, 1, 1, 1), new BigDecimal("1"), cliente, CondicaoPagamento.PIX, null, null, null);
		return venda;
	}
	
	private VendasEstoque mockVendaProdutos(int i, Vendas v, Estoque p) {
		var vendas = v;
		var produto = p;
		var vendaEstoque = new VendasEstoque(null, vendas, produto, new BigDecimal(i), 2.0);
		return vendaEstoque;
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(null, "Carne" + i, Unidade.KG, null, null, null, null);
		return produto;
	}
	
	private void cadastrarVendasProdutos(Vendas v, List<Estoque> estoque){
		List<VendasEstoque> result = new ArrayList<>();
		
		for(int i = 0; i < 3; i++) {
			var vendasProduto = mockVendaProdutos(i, v, estoque.get(i));
			em.persist(vendasProduto);
			result.add(vendasProduto);
		}
	}
	
	private void cadastrarVendasProduto(Vendas v, Estoque p) {
		var vendasProduto = mockVendaProdutos(1, v, p);
		em.persist(vendasProduto);
	}
}
















