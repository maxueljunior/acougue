package br.com.leuxam.acougue.domain.arquivosCompras;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.compras.ComprasRepository;

@Service
public class ArquivosComprasService {
	
	private ArquivosComprasRepository arquivosComprasRepository;
	
	private ComprasRepository comprasRepository;
	
	@Autowired
	public ArquivosComprasService(ArquivosComprasRepository arquivosComprasRepository,
			ComprasRepository comprasRepository) {
		this.arquivosComprasRepository = arquivosComprasRepository;
		this.comprasRepository = comprasRepository;
	}
	
	public ArquivosCompras saveArchive(MultipartFile file, Long id){
		
		if(!comprasRepository.existsById(id)) throw new ValidacaoException("Compra nº " + id + " não existe");
		
		var compras = comprasRepository.getReferenceById(id);
		
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		try {
			if(fileName.contains("..")) {
				throw new FileException("O filename do arquivo contem uma sequencia invalida " + fileName);
			}
			ArquivosCompras arquivosCompras = new ArquivosCompras(null, compras, file.getBytes(),
					fileName, file.getContentType());
			arquivosComprasRepository.save(arquivosCompras);
			return arquivosCompras;
		}catch(MaxUploadSizeExceededException | IOException e) {
			throw new MaxUploadSizeExceededException(file.getSize());
		}
	}
	
	public ArquivosCompras findByIdCompras(Long id) {
		if(!comprasRepository.existsById(id)) throw new ValidacaoException("Compra nº " + id + " não existe");
		
		var compras = comprasRepository.getReferenceById(id);
		
		var arquivo = arquivosComprasRepository.findByCompras(compras);
		if(!arquivo.isPresent()) throw new FileException("A compra nº " + id + " não contempla arquivo");
		return arquivo.get();
	}
	
}
