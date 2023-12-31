package br.com.leuxam.acougue.domain.cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.AtivadoException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoque;
import br.com.leuxam.acougue.domain.clienteEstoque.ClienteEstoqueRepository;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {
	
	private ClienteRepository clienteRepository;
	
	private ClienteEstoqueRepository clienteEstoqueRepository;
	
	private EstoqueRepository estoqueRepository;

	@Autowired
	public ClienteService(ClienteRepository clienteRepository,
			ClienteEstoqueRepository clienteEstoqueRepository,
			EstoqueRepository estoqueRepository) {
		this.clienteRepository = clienteRepository;
		this.clienteEstoqueRepository = clienteEstoqueRepository;
		this.estoqueRepository = estoqueRepository;
	}
	
	@Transactional
	public DadosDetalhamentoCliente save(DadosCriarCliente dados) {
		
		var cliente = new Cliente(dados);
		var clienteSalvo = clienteRepository.save(cliente);
		
		var listaId = estoqueRepository.findIdsAll();
		
		listaId.forEach(x -> {
			var estoque = estoqueRepository.getReferenceById(x.id());
			var clienteEstoque = new ClienteEstoque(null, estoque, clienteSalvo,
					new BigDecimal("53.00"), LocalDateTime.now());
			clienteEstoqueRepository.save(clienteEstoque);
		});
		
		return new DadosDetalhamentoCliente(clienteSalvo);
	}

	@Transactional
	public Page<DadosDetalhamentoCliente> findAllByAtivoAndNome(Pageable pageable, String nome) {
		
		var clientes = clienteRepository.queryByFindAllByAtivoAndNome(nome, pageable);
		
		return clientes.map(DadosDetalhamentoCliente::new);
	}

	@Transactional
	public DadosDetalhamentoCliente findByIdAndAtivo(Long id) {
		
		var cliente = clienteRepository.findByIdAndAtivoTrue(id);
		
		if(!cliente.isPresent()) throw new ValidacaoException("Cliente não existe ou está desativado");
		
//		linkTo(Cliente.class).slash(cliente.get().getId()).withSelfRel();
		
		return new DadosDetalhamentoCliente(cliente.get());
	}

	@Transactional
	public DadosDetalhamentoCliente update(Long id, DadosAtualizarCliente dados) {
		var cliente = clienteRepository.findByIdAndAtivoTrue(id);
		
		if(!cliente.isPresent()) throw new ValidacaoException("Cliente não existe ou está desativado");
		
		cliente.get().atualizarDados(dados);
		
		return new DadosDetalhamentoCliente(cliente.get());
	}

	@Transactional
	public void desativar(Long id) {
		var cliente = clienteRepository.findByIdAndAtivoTrue(id);
		
		if(!cliente.isPresent()) throw new ValidacaoException("Cliente não existe ou está desativado");
		
		cliente.get().desativar();
	}

	@Transactional
	public void ativar(Long id) {
		var cliente = clienteRepository.findById(id);
		
		if(!cliente.isPresent()) throw new ValidacaoException("Cliente não existe ou está desativado");
		
		if(cliente.get().getAtivo()) throw new AtivadoException("Cliente já está ativo!");
	
		cliente.get().ativar();
	}
	
}















