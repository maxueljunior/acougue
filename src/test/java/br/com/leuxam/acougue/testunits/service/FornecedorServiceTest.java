package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.leuxam.acougue.domain.AtivadoException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.fornecedor.DadosAtualizacaoFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.DadosCriarFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorRepository;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class FornecedorServiceTest {
	
	@InjectMocks
	private FornecedorService service;
	
	@Mock
	private FornecedorRepository fornecedorRepository;

	private Pageable pageable;

	@BeforeAll
	void beforeAll() {
		MockitoAnnotations.openMocks(this);
		service = new FornecedorService(fornecedorRepository);
		pageable = PageRequest.of(0, 5, Sort.by("razaoSocial"));
	}
	
	@Test
	@DisplayName("Deveria salvar caso dados estejam corretos")
	void test_cenario01() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.save(any())).thenReturn(fornecedor);
		
		var result = service.create(new DadosCriarFornecedor(fornecedor.getRazaoSocial(),
				fornecedor.getCnpj(), fornecedor.getNomeContato(), fornecedor.getTelefone()));
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals("razao social 1", result.razaoSocial());
		assertEquals("11.111.111/0001-11", result.cnpj());
		assertEquals("telefone 1", result.telefone());
		assertEquals("nome contato 1", result.nomeContato());
	}
	
	@Test
	@DisplayName("Deveria recuperar todos ativos por razao social ou não")
	void test_cenario02() {
		var fornecedores = mockListFornecedor();
		
		when(fornecedorRepository.searchFornecedorByAtivoTrueAndLikeRazao(any(), any(Pageable.class))).thenReturn(new PageImpl<>(fornecedores));
		
		var result = service.searchFornecedorByAtivoTrueAndLikeRazao("", pageable);
		
		assertNotNull(result);
		
		var fornecedor1 = result.getContent().get(1);
		
		assertEquals(1L, fornecedor1.id());
		assertEquals("razao social 1", fornecedor1.razaoSocial());
		assertEquals("11.111.111/0001-11", fornecedor1.cnpj());
		assertEquals("telefone 1", fornecedor1.telefone());
		assertEquals("nome contato 1", fornecedor1.nomeContato());
		
		var fornecedor3 = result.getContent().get(3);
		
		assertEquals(3L, fornecedor3.id());
		assertEquals("razao social 3", fornecedor3.razaoSocial());
		assertEquals("11.111.111/0001-11", fornecedor3.cnpj());
		assertEquals("telefone 3", fornecedor3.telefone());
		assertEquals("nome contato 3", fornecedor3.nomeContato());
	}
	
	@Test
	@DisplayName("Deveria recuperar pelo codigo do fornecedor")
	void test_cenario03() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(fornecedor));
		
		var result = service.findByIdAndAtivoTrue(1L);
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals("razao social 1", result.razaoSocial());
		assertEquals("11.111.111/0001-11", result.cnpj());
		assertEquals("telefone 1", result.telefone());
		assertEquals("nome contato 1", result.nomeContato());
	}
	
	@Test
	@DisplayName("Não deveria recuperar pelo codigo do fornecedor caso não exista")
	void test_cenario04() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () ->{
			service.findByIdAndAtivoTrue(1L);
		});
		
		String expectedMessage = "Fornecedor não existe ou está destivado";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria atualizar caso fornecedor exista")
	void test_cenario05() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(fornecedor));
		
		var result = service.update(1L, new DadosAtualizacaoFornecedor("razao alterada", null, null, null));
		
		assertNotNull(result);
		
		assertEquals(1L, result.id());
		assertEquals("razao alterada", result.razaoSocial());
		assertEquals("11.111.111/0001-11", result.cnpj());
		assertEquals("telefone 1", result.telefone());
		assertEquals("nome contato 1", result.nomeContato());
	}
	
	@Test
	@DisplayName("Não deveria atualizar caso fornecedor não exista")
	void test_cenario06() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () ->{
			service.update(1L, new DadosAtualizacaoFornecedor("razao alterada", null, null, null));
		});
		
		String expectedMessage = "Fornecedor não existe ou está destivado";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria desativar caso fornecedor exista")
	void test_cenario07() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.of(fornecedor));
		
		service.desativar(1L);
	}
	
	@Test
	@DisplayName("Não deveria desativar caso fornecedor não exista")
	void test_cenario08() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(ValidacaoException.class, () ->{
			service.update(1L, new DadosAtualizacaoFornecedor("razao alterada", null, null, null));
		});
		
		String expectedMessage = "Fornecedor não existe ou está destivado";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	@DisplayName("Deveria ativar caso esteja desativado")
	void test_cenario09() {
		var fornecedor = mockFornecedor(1);
		fornecedor.desativar();
		when(fornecedorRepository.findById(any())).thenReturn(Optional.of(fornecedor));
		
		service.ativar(1L);
	}
	
	@Test
	@DisplayName("Não deveria ativar caso esteja ativado")
	void test_cenario10() {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findById(any())).thenReturn(Optional.of(fornecedor));
		
		Exception ex = assertThrows(AtivadoException.class, () -> {
			service.ativar(1L);
		});
		
		String expectedMessage = "Fornecedor não existe ou já está Ativado";
		String actualMessage = ex.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	private Fornecedor mockFornecedor(int i) {
		return new Fornecedor(Long.valueOf(i), "razao social " + i, "11.111.111/0001-11",
				"telefone " + i, "nome contato "  + i, true);
	}
	
	private List<Fornecedor> mockListFornecedor(){
		List<Fornecedor> result = new ArrayList<>();
		for(int i=0;i<5;i++) {
			result.add(mockFornecedor(i));
		}
		return result;
	}
}

























