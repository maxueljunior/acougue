package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class ResumoLucratividadeControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private JacksonTester<List<ResumoLucratividade>> listResumo;
	
	private ClienteEstoqueService service;
	
	@MockBean
	private ClienteEstoqueRepository clienteEstoqueRepository;
	
	@BeforeAll
	void beforeAll() {
		service = new ClienteEstoqueService(clienteEstoqueRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar o resumo e codigo HTTP 200")
	void test_cenario01() throws Exception {
		var resumoLucratividade = mockResumoLucratividade();
		
		when(clienteEstoqueRepository.gerarResumoLucratividade(any(Pageable.class)))
			.thenReturn(new PageImpl<>(resumoLucratividade));
		
		var result = mvc.perform(get("/resumo/lucratividade")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		var jsonEsperado = listResumo.write(resumoLucratividade).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(clienteEstoqueRepository, times(1)).gerarResumoLucratividade(any(Pageable.class));
	}
	
	private List<ResumoLucratividade> mockResumoLucratividade() {
		List<ResumoLucratividade> result = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			var clienteEstoque = mockClienteEstoque();
			result.add(new ResumoLucratividade(clienteEstoque.getEstoque().getDescricao(),
					clienteEstoque.getLucratividade().doubleValue()));
		}
		return result;
	}
	
	private ClienteEstoque mockClienteEstoque() {
		var estoque = mockProduto();
		var cliente = mockCliente();
		var clienteEstoque = new ClienteEstoque(null, estoque, cliente, new BigDecimal("150"), LocalDateTime.now());
		return clienteEstoque;
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(1L, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Estoque mockProduto() {
		var produto = new Estoque(1L, "Carne", Unidade.KG, null, null, null, null);
		return produto;
	}
}
