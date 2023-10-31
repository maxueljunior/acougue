package br.com.leuxam.acougue.testunits.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosCompras;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasRepository;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasService;
import br.com.leuxam.acougue.domain.arquivosCompras.FileException;
import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ArquivosComprasServiceTest {
	
	@InjectMocks
	private ArquivosComprasService service;
	
	@MockBean
	private ArquivosComprasRepository arquivosComprasRepository;
	
	@MockBean
	private ComprasRepository comprasRepository;
	
	@BeforeAll
	void beforeAll() {
		service = new ArquivosComprasService(arquivosComprasRepository, comprasRepository);
	}
	
	@Test
	@DisplayName("Deveria salvar caso dados estejam corretos")
	void test_cenario01() throws IOException {
		var compras = mockCompras();
		MockMultipartFile file = new MockMultipartFile(
				"files", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "Ola mundo!".getBytes());
		var arquivosCompras = mockArquivosCompras(file);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.getReferenceById(1L)).thenReturn(compras);

		when(arquivosComprasRepository.save(any(ArquivosCompras.class))).thenReturn(arquivosCompras);
		var result = service.saveArchive(file, 1L);
		
		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getCompras().getId());
		assertNotNull(result.getData());
		assertNotNull(result.getFileType());
		
		assertEquals("filename.txt", result.getFileName());
	}
	
	@Test
	@DisplayName("Não deveria salvar caso dados estejam incorretos")
	void test_cenario02() throws IOException {
		var compras = mockCompras();
		MockMultipartFile file = new MockMultipartFile(
				"files", "file..name.txt", MediaType.TEXT_PLAIN_VALUE, "Ola mundo!".getBytes());
		var arquivosCompras = mockArquivosCompras(file);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(comprasRepository.getReferenceById(1L)).thenReturn(compras);

		when(arquivosComprasRepository.save(any(ArquivosCompras.class))).thenReturn(arquivosCompras);
		
		Exception exception = assertThrows(FileException.class, () ->{
			service.saveArchive(file, 1L);
		});
		
		String expectedMessage = "O filename do arquivo contem uma sequencia invalida ";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	@DisplayName("Deveria retornar os dados caso exista um arquivo")
	void test_cenario03() throws IOException {
		var compras = mockCompras();
		MockMultipartFile file = new MockMultipartFile(
				"files", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "Ola mundo!".getBytes());
		var arquivosCompras = mockArquivosCompras(file);
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(arquivosComprasRepository.findByCompras(any())).thenReturn(Optional.of(arquivosCompras));
		
		var result = service.findByIdCompras(1L);
		
		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getCompras().getId());
		assertNotNull(result.getData());
		assertNotNull(result.getFileType());
		
		assertEquals("filename.txt", result.getFileName());
	}
	
	@Test
	@DisplayName("Não deveria retornar os dados caso não exista um arquivo")
	void test_cenario04() throws IOException {
		var compras = mockCompras();
		
		when(comprasRepository.existsById(1L)).thenReturn(true);
		when(arquivosComprasRepository.findByCompras(any())).thenReturn(Optional.ofNullable(null));
		
		Exception ex = assertThrows(FileException.class, () -> {
			service.findByIdCompras(1L);
		});
		
		String expectedMessage = "A compra nº 1 não contempla arquivo";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	private ArquivosCompras mockArquivosCompras(MockMultipartFile file) throws IOException {
		var compras = mockCompras();
		return new ArquivosCompras(1L, compras, file.getBytes(), file.getOriginalFilename(), file.getContentType());
	}

	private Compras mockCompras() {
		return new Compras(1L, new Fornecedor(), new BigDecimal("100"), null, null, LocalDateTime.of(1999, 1, 1, 1, 1));
	}

}




























