package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoque;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueService;
import br.com.leuxam.acougue.domain.clienteEstoque.ResumoLucratividade;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.Unidade;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ClienteEstoqueServiceTest {

	private ClienteEstoqueService service;
	
	@MockBean
	private ClienteEstoqueRepository clienteEstoqueRepository;
	
	private Pageable pageable;
	
	@BeforeAll
	void beforeAll() {
		service = new ClienteEstoqueService(clienteEstoqueRepository);
		pageable = PageRequest.of(0, 15);
	}
	
	@Test
	@DisplayName("Deveria devolver o relatorio de carnes e lucratividade")
	void test() {
		var resumoLucratividade = mockResumoLucratividade();
		
		when(clienteEstoqueRepository.gerarResumoLucratividade(any(Pageable.class))).thenReturn(new PageImpl<>(resumoLucratividade));
		
		var result = service.resumoLucratividade(pageable);
		
		assertNotNull(result);
		assertTrue(result.getContent().size() == 3);
		
		var resumo1 = result.getContent().get(1);
		
		assertNotNull(resumo1);
		assertEquals("Carne1", resumo1.descricao());
		assertEquals(1.0, resumo1.total());
		
		var resumo2 = result.getContent().get(2);
		
		assertNotNull(resumo2);
		assertEquals("Carne2", resumo2.descricao());
		assertEquals(2.0, resumo2.total());
	}
	
	private List<ResumoLucratividade> mockResumoLucratividade() {
		List<ResumoLucratividade> result = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			var clienteEstoque = mockClienteEstoque(i);
			result.add(new ResumoLucratividade(clienteEstoque.getEstoque().getDescricao(),
					clienteEstoque.getLucratividade().doubleValue()));
		}
		return result;
	}
	
	private ClienteEstoque mockClienteEstoque(int i) {
		var estoque = mockProduto(i);
		var cliente = mockCliente();
		var clienteEstoque = new ClienteEstoque(Long.valueOf(i), estoque, cliente, new BigDecimal(i), LocalDateTime.now());
		return clienteEstoque;
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(1L, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Estoque mockProduto(int i) {
		var produto = new Estoque(Long.valueOf(i), "Carne"+i, Unidade.KG, null, null, null, null);
		return produto;
	}
}
