package br.com.leuxam.acougue.domain.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorRepository;
import jakarta.transaction.Transactional;

@Service
public class ComprasService {
	
	private ComprasRepository comprasRepository;
	
	private FornecedorRepository fornecedorRepository;

	@Autowired
	public ComprasService(ComprasRepository comprasRepository, FornecedorRepository fornecedorRepository) {
		this.comprasRepository = comprasRepository;
		this.fornecedorRepository = fornecedorRepository;
	}
	
	@Transactional
	public DadosDetalhamentoCompras create(DadosCriarCompras dados) {
		
		if(!fornecedorRepository.existsById(dados.idFornecedor())) throw new ExisteException("O fornecedor " + dados.idFornecedor() + " não existe");
		
		var fornecedor = fornecedorRepository.getReferenceById(dados.idFornecedor());
		
		var compras = new Compras(null, fornecedor, new BigDecimal("0.0"), null,null, LocalDateTime.now());
		comprasRepository.save(compras);
		return new DadosDetalhamentoCompras(compras);
	}

	public Page<DadosDetalhamentoCompras> findAll(Pageable pageable) {
		var compras = comprasRepository.findAll(pageable);
		return compras.map(DadosDetalhamentoCompras::new);
	}

	public DadosDetalhamentoCompras findById(Long id) {
		if(!comprasRepository.existsById(id)) throw new ValidacaoException("Compra nº " + id + " não existe!");
		
		var compra = comprasRepository.getReferenceById(id);
		return new DadosDetalhamentoCompras(compra);
	}
	
	@Transactional
	public DadosDetalhamentoCompras update(Long id, DadosAtualizarCompras dados) {
		var compra = comprasRepository.findById(id);
		
		if(!compra.isPresent()) throw new ValidacaoException("Compra nº " + id + " não existe!");
		
		if(dados.idFornecedor() != null) {
			if(!fornecedorRepository.existsById(dados.idFornecedor())) throw new ExisteException("O fornecedor " + dados.idFornecedor() + " não existe");
		}
		
		var fornecedor = fornecedorRepository.getReferenceById(dados.idFornecedor());
		compra.get().atualizar(fornecedor);
		
		return new DadosDetalhamentoCompras(compra.get());
	}
	
	/*
	 * Aqui da para fazer alguma coisa relacionada com o estado da aplicação,
	 * como por exemplo: Em cotação, Efetuado pagamento, Finalizada, Cancelada!
	 */
	
//	public Compras saveAttachment(MultipartFile file) throws Exception{
//		
//		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//		try {
//			if(fileName.contains("..")) {
//				throw new Exception("Filename contains invalid path sequence... " + fileName);
//			}
//			if(file.getBytes().length > (1024*1024)) {
//				throw new Exception("File size exceeds maxium limit");
//			}
//			
//			Compras attachment = new Compras(null, null, null, null, null, file.getBytes(), fileName, file.getContentType());
//			return comprasRepository.save(attachment);
//		} catch (MaxUploadSizeExceededException e) {
//            throw new MaxUploadSizeExceededException(file.getSize());
//        } catch (Exception e) {
//            throw new Exception("Could not save File: " + fileName);
//        }
//	}
//	
//	public void saveFiles(MultipartFile[] files) {
//		Arrays.asList(files).forEach(file -> {
//			try {
//				saveAttachment(file);
//			}catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		});
//	}
//	
//	public Compras findByIdFile(Long id) {
//		return comprasRepository.findById(id).get();
//	}
	
}








