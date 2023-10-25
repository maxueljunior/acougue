package br.com.leuxam.acougue.infra.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.leuxam.acougue.domain.compras.Compras;
import br.com.leuxam.acougue.domain.compras.ComprasDTO;

@Configuration
public class ModelMapperConfig {
	
	@Bean
	public ModelMapper modelMapper() {
		
		return new ModelMapper();
		
//		Caso queira modelar o modelMapper para retornar oque eu gostaria....
		
		
//		ModelMapper modelMapper = new ModelMapper();
//		
//		modelMapper.addMappings(new PropertyMap<Compras, ComprasVO>() {
//			@Override
//			protected void configure() {
//				map().setidFornecedor(source.getFornecedor().getId());
//			}
//		});
	}
}
