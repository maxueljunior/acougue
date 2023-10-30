package br.com.leuxam.acougue.testunits.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import br.com.leuxam.acougue.domain.fornecedor.DadosAtualizacaoFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.DadosCriarFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.DadosDetalhamentoFornecedor;
import br.com.leuxam.acougue.domain.fornecedor.Fornecedor;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorRepository;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class FornecedorControllerTest {
	
	private FornecedorService service;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private FornecedorRepository fornecedorRepository;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoFornecedor> dadosDetalhamento;
	
	@Autowired
	private JacksonTester<DadosCriarFornecedor> dadosCriar;
	
	@Autowired
	private JacksonTester<List<DadosDetalhamentoFornecedor>> listDadosDetalhamento;
	
	@Autowired
	private JacksonTester<DadosAtualizacaoFornecedor> dadosAtualizar;
	
	@BeforeAll
	void beforeAll() {
		service = new FornecedorService(fornecedorRepository);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria criar um fornecedor caso dados sejam validos e retornar codigo HTTP 201")
	void test_cenario1() throws Exception {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.save(any())).thenReturn(fornecedor);
		
		var result = mvc.perform(post("/fornecedor")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar
							.write(new DadosCriarFornecedor(fornecedor.getRazaoSocial(),
									fornecedor.getCnpj(), fornecedor.getNomeContato(),
									fornecedor.getTelefone())
									).getJson()
							)
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoFornecedor(fornecedor)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(fornecedorRepository, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria criar um fornecedor caso dados sejam invalidos e retornar codigo HTTP 400")
	void test_cenario2() throws Exception {
		var fornecedor = mockFornecedor(1);
		
		var result = mvc.perform(post("/fornecedor")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar
							.write(new DadosCriarFornecedor(null, null, null, null)).getJson()
							)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(fornecedorRepository, times(0)).save(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar todos os fornecedores não passando razão social e devolver codigo HTTP 200")
	void test_cenario3() throws Exception {
		var fornecedores = mockListFornecedor();
		
		when(fornecedorRepository.searchFornecedorByAtivoTrueAndLikeRazao(any(), any(Pageable.class))).thenReturn(new PageImpl<>(fornecedores));
		
		var result = mvc.perform(get("/fornecedor")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento
				.write(fornecedores.stream().map(DadosDetalhamentoFornecedor::new)
						.collect(Collectors.toList())).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(fornecedorRepository, times(1)).searchFornecedorByAtivoTrueAndLikeRazao(any(), any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar todos os fornecedores passando razão social e devolver codigo HTTP 200")
	void test_cenario4() throws Exception {
		var fornecedores = mockListFornecedor();
		
		when(fornecedorRepository.searchFornecedorByAtivoTrueAndLikeRazao(any(), any(Pageable.class))).thenReturn(new PageImpl<>(fornecedores));
		
		var result = mvc.perform(get("/fornecedor")
					.contentType(MediaType.APPLICATION_JSON)
					.param("q", "razao social")
				).andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento
				.write(fornecedores.stream().map(DadosDetalhamentoFornecedor::new)
						.collect(Collectors.toList())).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).contains(jsonEsperado);
		
		verify(fornecedorRepository, times(1)).searchFornecedorByAtivoTrueAndLikeRazao(any(), any(Pageable.class));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria retornar o fornecedores passando o ID e devolver codigo HTTP 200")
	void test_cenario5() throws Exception {
		var fornecedor = mockFornecedor(4);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(4L)).thenReturn(Optional.of(fornecedor));
		
		var result = mvc.perform(get("/fornecedor/4")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento
				.write(new DadosDetalhamentoFornecedor(fornecedor)).getJson();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(fornecedorRepository, times(1)).findByIdAndAtivoTrue(4L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria retornar o fornecedores passando o ID invalido e devolver codigo HTTP 404")
	void test_cenario6() throws Exception {
		var fornecedor = mockFornecedor(4);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(any())).thenReturn(Optional.ofNullable(null));
		
		var result = mvc.perform(get("/fornecedor/4")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(fornecedorRepository, times(1)).findByIdAndAtivoTrue(any());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria atualizar o fornecedor caso dados sejam validos e retornar codigo HTTP 200")
	void test_cenario7() throws Exception {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(fornecedor));
		
		var result = mvc.perform(put("/fornecedor/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizar.write(
							new DadosAtualizacaoFornecedor("razao 2", "22.222.222/0002-22",
									"nome contato 2", "telefone 2")).getJson()
					)
				).andReturn().getResponse();
		
		fornecedor.atualizar(new DadosAtualizacaoFornecedor("razao 2", "22.222.222/0002-22",
									"nome contato 2", "telefone 2"));
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoFornecedor(fornecedor)).getJson();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo(jsonEsperado);
		
		verify(fornecedorRepository, times(1)).findByIdAndAtivoTrue(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria atualizar o fornecedor caso dados sejam invalidos e retornar codigo HTTP 404")
	void test_cenario8() throws Exception {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.ofNullable(null));
		
		var result = mvc.perform(put("/fornecedor/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizar.write(
							new DadosAtualizacaoFornecedor("razao 2", "22.222.222/0002-22",
									"nome contato 2", "telefone 2")).getJson()
					)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(fornecedorRepository, times(1)).findByIdAndAtivoTrue(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria desativar o fornecedor caso dados sejam invalidos e retornar codigo HTTP 204")
	void test_cenario9() throws Exception {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(fornecedor));
		
		var result = mvc.perform(delete("/fornecedor/1")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
		
		verify(fornecedorRepository, times(1)).findByIdAndAtivoTrue(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria desativar o fornecedor caso dados sejam invalidos e retornar codigo HTTP 404")
	void test_cenario10() throws Exception {
		var fornecedor = mockFornecedor(1);
		
		when(fornecedorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.ofNullable(null));
		
		var result = mvc.perform(delete("/fornecedor/1")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
		verify(fornecedorRepository, times(1)).findByIdAndAtivoTrue(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Deveria ativar o fornecedor caso ele esteja desativado e retornar codigo HTTP 200")
	void test_cenario11() throws Exception {
		var fornecedor = mockFornecedor(1);
		fornecedor.desativar();
		when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
		
		var result = mvc.perform(patch("/fornecedor/1")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getContentAsString()).isEqualTo("Usuario ativado!");
		
		verify(fornecedorRepository, times(1)).findById(1L);
	}
	
	@Test
	@WithMockUser
	@DisplayName("Não deveria ativar o fornecedor caso ele esteja ativado e retornar codigo HTTP 400")
	void test_cenario12() throws Exception {
		var fornecedor = mockFornecedor(1);
		when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
		
		var result = mvc.perform(patch("/fornecedor/1")
					.contentType(MediaType.APPLICATION_JSON)
				).andReturn().getResponse();
		
		assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
		verify(fornecedorRepository, times(1)).findById(1L);
	}
	
	private Fornecedor mockFornecedor(int i) {
		return new Fornecedor(null, "razao social " + i, "11.111.111/0001-11", "telefone " + i, "nome contato "  + i, true);
	}
	
	private List<Fornecedor> mockListFornecedor(){
		List<Fornecedor> result = new ArrayList<>();
		for(int i=0;i<5;i++) {
			result.add(mockFornecedor(i));
		}
		return result;
	}
}









































