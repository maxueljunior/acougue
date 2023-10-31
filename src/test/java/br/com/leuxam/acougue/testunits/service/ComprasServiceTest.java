package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosCompras;
import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasDTO;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.compras.ComprasService;
import br.com.leuxam.acougue.domain.compras.DadosAtualizarCompras;
import br.com.leuxam.acougue.domain.compras.DadosCriarCompras;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorRepository;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ComprasServiceTest {
	
	@Autowired
	private ComprasService service;
	
	@MockBean
	private ComprasRepository comprasRepository;

	@MockBean
	private FornecedorRepository fornecedorRepository;
	
	private Pageable pageable;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@BeforeAll
	void beforeAll() {
		service = new ComprasService(comprasRepository, fornecedorRepository);
		pageable = PageRequest.of(0, 5, Sort.by("fornecedor"));
	}
	
	@Test
	@DisplayName("Deveria salvar caso informações estejam corretas")
	void test_cenario01() {
		var fornecedor = mockFornecedor();
		var compras = mockCompras(1);
		
		when(fornecedorRepository.existsById(1L)).thenReturn(true);
		when(fornecedorRepository.getReferenceById(1L)).thenReturn(fornecedor);
		when(comprasRepository.save(any())).thenReturn(compras);
		
		var result = service.create(new DadosCriarCompras(1L));
		
		assertNotNull(result);
		assertNotNull(result.id());
		assertNotNull(result.idFornecedor());
		
		assertEquals(new BigDecimal("1"), result.valorTotal());
		assertEquals(LocalDateTime.of(1999, 1, 1, 1, 1), result.data());
	}
	
	@Test
	@DisplayName("Não deveria salvar caso informações estejam incorretas")
	void test_cenario02() {
		var compras = mockCompras(1);
		
		when(fornecedorRepository.existsById(1L)).thenReturn(false);
		
		Exception ex = assertThrows(ExisteException.class, () -> {
			service.create(new DadosCriarCompras(1L));
		});
		
		String expectedMessage = "O fornecedor 1 não existe";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria retornar todos e adicionar link nos que contem download")
	void test_cenario03() {
		var compras = listMockCompras();
		
		when(comprasRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(compras));
		
		var result = service.findAll(pageable);
		var resultado = result.getContent()
							.stream()
							.map(c ->
								modelMapper.map(c.getContent(), ComprasDTO.class))
							.collect(Collectors.toList());
		
		var compras1 = resultado.get(1);
		
		assertNotNull(compras1);
		assertEquals(1L, compras1.getId());
		assertEquals(1L, compras1.getFornecedor().getId());
		assertEquals(new BigDecimal("1"), compras1.getValorTotal());
		assertEquals("<http://localhost/arquivos/compras/1/download>;rel=\"download\"", compras1.getLinks().toString());
		
		var compras3 = resultado.get(3);
		
		assertNotNull(compras3);
		assertEquals(3L, compras3.getId());
		assertEquals(1L, compras3.getFornecedor().getId());
		assertEquals(new BigDecimal("3"), compras3.getValorTotal());
		assertEquals("<http://localhost/arquivos/compras/3/download>;rel=\"download\"", compras3.getLinks().toString());
	}
	
	@Test
	@DisplayName("Deveria retornar a compra pelo ID e link")
	void test_cenario04() {
		var compras = mockCompras(1);
		
		when(comprasRepository.existsById(any())).thenReturn(true);
		when(comprasRepository.findById(any())).thenReturn(Optional.of(compras));
		
		var result = service.findById(1L);
		
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals(1L, result.getFornecedor().getId());
		assertEquals(new BigDecimal("1"), result.getValorTotal());
		assertEquals("<http://localhost/arquivos/compras/1/download>;rel=\"download\"", result.getLinks().toString());
	}
	
	@Test
	@DisplayName("Não deveria retornar a compra pelo ID e link")
	void test_cenario05() {
		var compras = mockCompras(1);
		
		when(comprasRepository.existsById(any())).thenReturn(false);
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.findById(1L);
		});
		
		String expectedMessage = "Compra nº 1 não existe!";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria atualizar caso dados estejam corretos")
	void test_cenario06() {
		var compras = mockCompras(1);
		var fornecedorAtualizado = mockFornecedor2();
		when(comprasRepository.findById(any())).thenReturn(Optional.of(compras));
		when(fornecedorRepository.existsById(2L)).thenReturn(true);
		when(fornecedorRepository.getReferenceById(2L)).thenReturn(fornecedorAtualizado);
		
		var result = service.update(1L, new DadosAtualizarCompras(2L));
		
		assertNotNull(result);
		assertEquals(1L, result.id());
		assertEquals(2L, result.idFornecedor());
		assertEquals(new BigDecimal("1"), result.valorTotal());
		assertEquals(LocalDateTime.of(1999, 1, 1, 1, 1), result.data());
	}
	
	@Test
	@DisplayName("Não deveria atualizar caso dados estejam incorretos")
	void test_cenario07() {
		var compras = mockCompras(1);
		var fornecedorAtualizado = mockFornecedor2();
		when(comprasRepository.findById(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () -> {
			service.findById(1L);
		});
		
		String expectedMessage = "Compra nº 1 não existe!";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	private Fornecedor mockFornecedor2() {
		return new Fornecedor(2L, "razao social 2", "cnpj 2", "telefone 2", "contato 2", true);
	}
	
	private Fornecedor mockFornecedor() {
		return new Fornecedor(1L, "razao social", "cnpj", "telefone", "contato", true);
	}
	
	private Compras mockCompras(int i) {
		var fornecedor = mockFornecedor();
		List<ArquivosCompras> arquivos = new ArrayList<>();
		arquivos.add(new ArquivosCompras());
		return new Compras(Long.valueOf(i), fornecedor, new BigDecimal(i), null, arquivos, LocalDateTime.of(1999, 1, 1, 1, 1));
	}
	
	private List<Compras> listMockCompras(){
		List<Compras> result = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			result.add(mockCompras(i));
		}
		return result;
	}

}
