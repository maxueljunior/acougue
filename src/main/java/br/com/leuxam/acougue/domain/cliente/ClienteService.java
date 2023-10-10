package br.com.leuxam.acougue.domain.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ValidacaoException;

@Service
public class ClienteService {
	
	private ClienteRepository clienteRepository;

	@Autowired
	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	public DadosDetalhamentoCliente save(DadosCriarCliente dados) {
		
		var cliente = new Cliente(dados);
		clienteRepository.save(cliente);
		
		return new DadosDetalhamentoCliente(cliente);
	}

	public Page<DadosDetalhamentoCliente> findAllByAtivo(Pageable pageable) {
		
		var clientes = clienteRepository.findAllByAtivoTrue(pageable);
		
		return clientes.map(DadosDetalhamentoCliente::new);
	}

	public DadosDetalhamentoCliente findByIdAndAtivo(Long id) {
		
		var cliente = clienteRepository.findByIdAndAtivoTrue(id);
		
		if(!cliente.isPresent()) throw new ValidacaoException("Cliente não existe ou está desativado");
		
		return new DadosDetalhamentoCliente(cliente.get());
	}
	
}















