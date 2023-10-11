package br.com.leuxam.acougue.domain.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.AtivadoException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {
	
	private ClienteRepository clienteRepository;

	@Autowired
	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}
	
	@Transactional
	public DadosDetalhamentoCliente save(DadosCriarCliente dados) {
		
		var cliente = new Cliente(dados);
		clienteRepository.save(cliente);
		
		return new DadosDetalhamentoCliente(cliente);
	}

	public Page<DadosDetalhamentoCliente> findAllByAtivoAndNome(Pageable pageable, String nome) {
		
		var clientes = clienteRepository.queryByFindAllByAtivoAndNome(nome, pageable);
		
		return clientes.map(DadosDetalhamentoCliente::new);
	}

	public DadosDetalhamentoCliente findByIdAndAtivo(Long id) {
		
		var cliente = clienteRepository.findByIdAndAtivoTrue(id);
		
		if(!cliente.isPresent()) throw new ValidacaoException("Cliente não existe ou está desativado");
		
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















