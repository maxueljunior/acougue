package br.com.leuxam.acougue.domain.estoque;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoque;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import jakarta.transaction.Transactional;

@Service
public class EstoqueService {
	
	private EstoqueRepository estoqueRepository;
	
	private ClienteRepository clienteRepository;
	
	private ClienteEstoqueRepository clienteEstoqueRepository;

	@Autowired
	public EstoqueService(EstoqueRepository estoqueRepository,
			ClienteRepository clienteRepository,
			ClienteEstoqueRepository clienteEstoqueRepository) {
		this.estoqueRepository = estoqueRepository;
		this.clienteRepository = clienteRepository;
		this.clienteEstoqueRepository = clienteEstoqueRepository;
	}
	
	@Transactional
	public DadosDetalhamentoEstoque create(DadosCriarEstoque dados) {
		var estoque = new Estoque(dados);
		var estoqueFim = estoqueRepository.save(estoque);
		
		var listaId = clienteRepository.findIdAlls();
		
		listaId.forEach(c -> {
			var cliente = clienteRepository.getReferenceById(c.id());
			var clienteEstoque = new ClienteEstoque(null, estoqueFim, cliente,
					new BigDecimal("53"), LocalDateTime.now());
			clienteEstoqueRepository.save(clienteEstoque);
		});
		
		return new DadosDetalhamentoEstoque(estoqueFim);
	}

	@Transactional
	public Page<DadosDetalhamentoEstoque> findAll(Pageable pageable) {
		var estoque = estoqueRepository.findAll(pageable);
		return estoque.map(DadosDetalhamentoEstoque::new);
	}
	
	@Transactional
	public Page<DadosDetalhamentoEstoque> searchEstoqueByAtivoTrueAndLikeDescricao(String descricao, Pageable pageable) {
		var estoque = estoqueRepository.searchEstoqueByAtivoTrueAndLikeDescricao(descricao, pageable);
		
		return estoque.map(DadosDetalhamentoEstoque::new);
	}

	@Transactional
	public DadosDetalhamentoEstoque findById(Long id) {
		var estoque = estoqueRepository.findById(id);
		
		if(!estoque.isPresent()) throw new ValidacaoException("O produto nº " + id + " não existe");
	
		return new DadosDetalhamentoEstoque(estoque.get());
	}
	
	@Transactional
	public DadosDetalhamentoEstoque update(Long id, DadosAtualizarEstoque dados) {
		var estoque = estoqueRepository.findById(id);
		
		if(!estoque.isPresent()) throw new ValidacaoException("O produto nº " + id + " não existe");
		
		estoque.get().atualizar(dados);
		return new DadosDetalhamentoEstoque(estoque.get());
	}
	
	@Transactional
	public void delete(Long id) {
		var estoque = estoqueRepository.findByIdAndAtivoTrue(id);
		if(!estoque.isPresent()) throw new ValidacaoException("O produto nº " + id + " não existe");
		estoque.get().desativar();
	}
}
