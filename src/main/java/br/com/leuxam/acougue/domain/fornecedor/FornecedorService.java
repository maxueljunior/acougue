package br.com.leuxam.acougue.domain.fornecedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.AtivadoException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class FornecedorService {
	
	private FornecedorRepository fornecedorRepository;

	@Autowired
	public FornecedorService(FornecedorRepository fornecedorRepository) {
		this.fornecedorRepository = fornecedorRepository;
	}
	
	@Transactional
	public DadosDetalhamentoFornecedor create(DadosCriarFornecedor dados) {
		
		var fornecedor = new Fornecedor(dados);
		fornecedorRepository.save(fornecedor);
		return new DadosDetalhamentoFornecedor(fornecedor);
	}

	public Page<DadosDetalhamentoFornecedor> searchFornecedorByAtivoTrueAndLikeRazao(
			String razao, Pageable pageable) {
		var fornecedores = fornecedorRepository
				.searchFornecedorByAtivoTrueAndLikeRazao(razao, pageable);
		
		return fornecedores.map(DadosDetalhamentoFornecedor::new);
	}

	public DadosDetalhamentoFornecedor findByIdAndAtivoTrue(Long id) {
		var fornecedor = fornecedorRepository.findByIdAndAtivoTrue(id);
		
		if(!fornecedor.isPresent()) throw new ValidacaoException("Fornecedor não existe ou está destivado");
		
		return new DadosDetalhamentoFornecedor(fornecedor.get());
	}
	
	@Transactional
	public DadosDetalhamentoFornecedor update(Long id, 
			DadosAtualizacaoFornecedor dados) {
		var fornecedor = fornecedorRepository.findByIdAndAtivoTrue(id);
		
		if(!fornecedor.isPresent()) throw new ValidacaoException("Fornecedor não existe ou está destivado");
		
		fornecedor.get().atualizar(dados);
		
		return new DadosDetalhamentoFornecedor(fornecedor.get());
	}
	
	@Transactional
	public void desativar(Long id) {
		var fornecedor = fornecedorRepository.findByIdAndAtivoTrue(id);
		
		if(!fornecedor.isPresent()) throw new ValidacaoException("Fornecedor não existe ou está destivado");
		
		fornecedor.get().desativar();
	}
	
	@Transactional
	public void ativar(Long id) {
		var fornecedor = fornecedorRepository.findById(id);
		
		if(fornecedor.get().getAtivo()) throw new AtivadoException("Fornecedor não existe ou já está Ativado");
		
		fornecedor.get().ativar();
	}
}
