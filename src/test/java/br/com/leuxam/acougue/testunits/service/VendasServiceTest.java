package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.arquivosCompras.FileException;
import br.com.leuxam.acougue.domain.cliente.Cliente;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.cliente.Sexo;
import br.com.leuxam.acougue.domain.cliente.endereco.Endereco;
import br.com.leuxam.acougue.domain.compras.DadosCriarVendas;
import br.com.leuxam.acougue.domain.estoque.Estoque;
import br.com.leuxam.acougue.domain.estoque.Unidade;
import br.com.leuxam.acougue.domain.vendas.CondicaoPagamento;
import br.com.leuxam.acougue.domain.vendas.DadosAtualizarVenda;
import br.com.leuxam.acougue.domain.vendas.Vendas;
import br.com.leuxam.acougue.domain.vendas.VendasDTO;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;
import br.com.leuxam.acougue.domain.vendas.VendasService;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class VendasServiceTest {
	
	@Autowired
	private VendasService service;
	
	@MockBean
	private VendasRepository vendasRepository;

	@MockBean
	private ClienteRepository clienteRepository;

	@MockBean
	private VendasEstoqueRepository vendasEstoqueRepository;
	
	private Pageable pageable;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@BeforeAll
	void beforeAll() {
		service = new VendasService(vendasRepository, clienteRepository, vendasEstoqueRepository);
		pageable = PageRequest.of(0, 10, Sort.by("id"));
	}
	
	@Test
	@DisplayName("Deveria salvar caso dados estejam corretos")
	void test_cenario01() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(clienteRepository.existsById(1L)).thenReturn(true);
		when(clienteRepository.getReferenceById(1L)).thenReturn(cliente);
		when(vendasRepository.save(any())).thenReturn(vendas);
		
		var result = service.create(new DadosCriarVendas(1L));
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals(1L, result.idCliente());
		assertEquals(CondicaoPagamento.DEBITO, result.condicaoPagamento());
		assertNotNull(result.dataVenda());
		assertEquals(new BigDecimal("1"), result.valorTotal());

	}
	
	@Test
	@DisplayName("Não deveria salvar caso dados estejam incorretos")
	void test_cenario02() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(clienteRepository.existsById(1L)).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.create(new DadosCriarVendas(1L));
		});
		
		String expectedMessage = "Não existe cliente nº 1";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria devolver todos passando ou não passando o id do cliente com links de download")
	void test_cenario03() {
		var vendas = mockVendas();
		
		when(vendasRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(vendas));
		
		var result = service.findAll(pageable, "0");
		var resultado = result.getContent()
				.stream()
				.map(c ->
					modelMapper.map(c.getContent(), VendasDTO.class))
				.collect(Collectors.toList());
		
		var vendas1 = resultado.get(1);
		
		assertNotNull(vendas1);
		assertEquals(1L, vendas1.getId());
		assertEquals(2L, vendas1.getIdCliente());
		assertEquals(CondicaoPagamento.DEBITO, vendas1.getCondicaoPagamento());
		assertNotNull(vendas1.getDataVenda());
		assertEquals(new BigDecimal("1"), vendas1.getValorTotal());
		assertEquals("<http://localhost/vendas/download/1>;rel=\"download\"", vendas1.getLinks().toString());
	
		var vendas2 = resultado.get(2);
		
		assertNotNull(vendas2);
		assertEquals(1L, vendas2.getId());
		assertEquals(1L, vendas2.getIdCliente());
		assertEquals(CondicaoPagamento.DEBITO, vendas2.getCondicaoPagamento());
		assertNotNull(vendas2.getDataVenda());
		assertEquals(new BigDecimal("1"), vendas2.getValorTotal());
		assertEquals("<http://localhost/vendas/download/1>;rel=\"download\"", vendas2.getLinks().toString());
	}
	
	@Test
	@DisplayName("Deveria devolver todos passando o id do cliente com links de download")
	void test_cenario04() {
		var vendas = mockVendasTwo();
		
		when(vendasRepository.findVendasIdCliente(any(Pageable.class), any())).thenReturn(new PageImpl<>(vendas));
		
		var result = service.findAll(pageable, "1");
		var resultado = result.getContent()
				.stream()
				.map(c ->
					modelMapper.map(c.getContent(), VendasDTO.class))
				.collect(Collectors.toList());
		
		var vendas1 = resultado.get(1);
		
		assertNotNull(vendas1);
		assertEquals(1L, vendas1.getId());
		assertEquals(1L, vendas1.getIdCliente());
		assertEquals(CondicaoPagamento.DEBITO, vendas1.getCondicaoPagamento());
		assertNotNull(vendas1.getDataVenda());
		assertEquals(new BigDecimal("1"), vendas1.getValorTotal());
		assertEquals("<http://localhost/vendas/download/1>;rel=\"download\"", vendas1.getLinks().toString());
	
		var vendas2 = resultado.get(2);
		
		assertNotNull(vendas2);
		assertEquals(1L, vendas2.getId());
		assertEquals(1L, vendas2.getIdCliente());
		assertEquals(CondicaoPagamento.DEBITO, vendas2.getCondicaoPagamento());
		assertNotNull(vendas2.getDataVenda());
		assertEquals(new BigDecimal("1"), vendas2.getValorTotal());
		assertEquals("<http://localhost/vendas/download/1>;rel=\"download\"", vendas2.getLinks().toString());
	}
	
	@Test
	@DisplayName("Deveria atualizar a venda caso dados sejam validos")
	void test_cenario05() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.findById(any())).thenReturn(Optional.of(vendas));
		
		var result = service.update(1L, new DadosAtualizarVenda(CondicaoPagamento.PIX));
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals(1L, result.idCliente());
		assertEquals(CondicaoPagamento.PIX, result.condicaoPagamento());
		assertNotNull(result.dataVenda());
		assertEquals(new BigDecimal("1"), result.valorTotal());
	}
	
	@Test
	@DisplayName("Não deveria atualizar a venda caso dados sejam invalidos")
	void test_cenario06() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		
		when(vendasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.update(1L, new DadosAtualizarVenda(CondicaoPagamento.PIX));
		});
		
		String expectedMessage = "Não existe venda nº 1";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria gerar pdf caso dados estejam corretos")
	void test_cenario07() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		List<VendasEstoque> vendasEstoque = new ArrayList<>();
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.findById(any())).thenReturn(Optional.of(vendas));
		when(vendasEstoqueRepository.findAllVendasEstoque(any())).thenReturn(vendasEstoque);
		
		var result = service.gerarPdf(1L);
		
		assertNotNull(result);
	}
	
	@Test
	@DisplayName("Não deveria gerar pdf caso dados estejam incorretos")
	void test_cenario08() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.gerarPdf(1l);
		});
		
		String expectedMessage = "Não existe venda nº 1";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
		
	}
	
	@Test
	@DisplayName("Deveria devolver a venda caso contenha arquivo")
	void test_cenario09() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.getReferenceById(any())).thenReturn(vendas);
		
		var result = service.findByIdAndArchive(1l);
		
		assertNotNull(result);
		
		assertEquals(1L, result.getId());
		assertEquals(1L, result.getCliente().getId());
		assertNotNull(result.getDataVenda());
		assertEquals(new BigDecimal("1"), result.getValorTotal());
		assertEquals(CondicaoPagamento.DEBITO, result.getCondicaoPagamento());
		assertNotNull(result.getData());
		assertEquals("venda 1.pdf",result.getFileName());
	}
	
	@Test
	@DisplayName("Não deveria devolver a venda caso não contenha arquivo")
	void test_cenario10() {
		var cliente = mockCliente();
		var vendas = mockVenda(cliente);
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.getReferenceById(any())).thenReturn(vendas);
		
		Exception ex = assertThrows(FileException.class, () -> {
			service.findByIdAndArchive(1L);
		});
		
		String expectedMessage = "Não existe nenhum cupom não fiscal para a Venda nº 1";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria devolver a venda passando o id")
	void test_cenario11() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(any())).thenReturn(true);
		when(vendasRepository.getReferenceById(any())).thenReturn(vendas);
		
		var result = service.findById(1l);
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals(1L, result.idCliente());
		assertEquals(CondicaoPagamento.DEBITO, result.condicaoPagamento());
		assertNotNull(result.dataVenda());
		assertEquals(new BigDecimal("1"), result.valorTotal());
	}
	
	@Test
	@DisplayName("Não deveria devolver a venda passando o id que não existe")
	void test_cenario12() {
		var cliente = mockCliente();
		var vendas = mockVendaComId(cliente);
		
		when(vendasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.findById(1l);
		});
		
		String expectedMessage = "Não existe venda nº 1";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	private Cliente mockCliente() {
		var endereco = new Endereco("rua", "bairro", 22);
		var cliente = new Cliente(1L, "nome", "sobrenome", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private Cliente mockClienteTwo() {
		var endereco = new Endereco("rua 2", "bairro 2 ", 2222);
		var cliente = new Cliente(2L, "nome 2", "sobrenome 2", "99232", LocalDate.of(1999, 1, 1),
				endereco, Sexo.M, true, null);
		return cliente;
	}
	
	private List<Vendas> mockVendas(){
		var cliente1 = mockCliente();
		var cliente2 = mockClienteTwo();
		
		List<Vendas> lista = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			if(i % 2 == 0) {
				lista.add(mockVendaComId(cliente1));
			}else {
				lista.add(mockVendaComId(cliente2));
			}
		}
		return lista;
	}
	
	private List<Vendas> mockVendasTwo(){
		var cliente1 = mockCliente();
		
		List<Vendas> lista = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			lista.add(mockVendaComId(cliente1));
		}
		return lista;
	}
	
	private Page<VendasDTO> mockVendasDTO(Page<Vendas> lista){
		List<VendasDTO> listaDTO = new ArrayList<>();
		
		for (Vendas vendas : lista) {
			listaDTO.add(new VendasDTO(vendas.getId(), vendas.getCondicaoPagamento(), vendas.getDataVenda(),
					vendas.getValorTotal(), vendas.getCliente().getId(), vendas.getFileName()));
		}
		
		return new PageImpl<>(listaDTO);
	}
	
	private Vendas mockVenda(Cliente cliente) {
		var venda = new Vendas(cliente);
		return venda;
	}
	
	private Vendas mockVendaComId(Cliente cliente) {
		byte[] bytes = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
		var venda = new Vendas(1L, LocalDateTime.now(), new BigDecimal("1"), cliente, CondicaoPagamento.DEBITO,bytes,"venda 1.pdf",null);
		return venda;
	}
}
