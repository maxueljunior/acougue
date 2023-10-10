package br.com.leuxam.acougue.domain.compras;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComprasService {
	
	private ComprasRepository comprasRepository;

	@Autowired
	public ComprasService(ComprasRepository ComprasRepository) {
		this.comprasRepository = comprasRepository;
	}
	
}
