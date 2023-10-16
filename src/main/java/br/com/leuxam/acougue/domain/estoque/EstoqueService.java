package br.com.leuxam.acougue.domain.estoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ValidacaoException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class EstoqueService {
	
	private EstoqueRepository estoqueRepository;

	@Autowired
	public EstoqueService(EstoqueRepository estoqueRepository) {
		this.estoqueRepository = estoqueRepository;
	}
	
	@Transactional
	public DadosDetalhamentoEstoque create(DadosCriarEstoque dados) {
		var estoque = new Estoque(dados);
		estoqueRepository.save(estoque);
		return new DadosDetalhamentoEstoque(estoque);
	}

	public Page<DadosDetalhamentoEstoque> findAll(Pageable pageable) {
		var estoque = estoqueRepository.findAll(pageable);
		return estoque.map(DadosDetalhamentoEstoque::new);
	}

	public DadosDetalhamentoEstoque findById(Long id) {
		var estoque = estoqueRepository.findById(id);
		
		if(!estoque.isPresent()) throw new ValidacaoException("O produto nº " + id + " não existe");
	
		return new DadosDetalhamentoEstoque(estoque.get());
	}

	public DadosDetalhamentoEstoque update(Long id, DadosAtualizarEstoque dados) {
		var estoque = estoqueRepository.findById(id);
		
		if(!estoque.isPresent()) throw new ValidacaoException("O produto nº " + id + " não existe");
		
		estoque.get().atualizar(dados);
		return null;
	}
}
