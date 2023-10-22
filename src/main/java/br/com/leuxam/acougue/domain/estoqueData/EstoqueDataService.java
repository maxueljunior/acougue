package br.com.leuxam.acougue.domain.estoqueData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstoqueDataService {
	
	private EstoqueDataRepository estoqueDataRepository;
	
	@Autowired
	public EstoqueDataService(EstoqueDataRepository estoqueDataRepository) {
		this.estoqueDataRepository = estoqueDataRepository;
	}
}
