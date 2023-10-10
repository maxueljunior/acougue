package br.com.leuxam.acougue.domain.vendas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendasService {
	
	private VendasRepository vendasRepository;

	@Autowired
	public VendasService(VendasRepository vendasRepository) {
		this.vendasRepository = vendasRepository;
	}
}
