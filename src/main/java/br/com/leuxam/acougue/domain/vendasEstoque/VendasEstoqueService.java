package br.com.leuxam.acougue.domain.vendasEstoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.estoque.EstoqueRepository;
import br.com.leuxam.acougue.domain.vendas.VendasRepository;

@Service
public class VendasEstoqueService {
	
	private VendasEstoqueRepository vendasEstoqueRepository;
	
	private VendasRepository vendasRepository;
	
	private EstoqueRepository estoqueRepository;

	@Autowired
	public VendasEstoqueService(VendasEstoqueRepository vendasEstoqueRepository,
			VendasRepository vendasRepository, EstoqueRepository estoqueRepository) {
		this.vendasEstoqueRepository = vendasEstoqueRepository;
		this.vendasRepository = vendasRepository;
		this.estoqueRepository = estoqueRepository;
	}

	public DadosDetalhamentoVendaEstoque create(DadosCriarVendaEstoque dados) {
		
		if(!vendasRepository.existsById(dados.idVendas())) throw new ExisteException("A venda nº " + dados.idVendas() + " não existe");
		
		if(!estoqueRepository.existsById(dados.idEstoque())) throw new ExisteException("O item nº " + dados.idEstoque() + " não existe");
		
		var vendas = vendasRepository.findById(dados.idVendas());
		var estoque = estoqueRepository.findById(dados.idEstoque());
		
		vendas.get().atualizar(dados);
		estoque.get().atualizar(dados);
		
		var vendasEstoque = new VendasEstoque(dados, vendas.get(), estoque.get());
		vendasEstoqueRepository.save(vendasEstoque);
		
		return new DadosDetalhamentoVendaEstoque(vendasEstoque);
	}
}
