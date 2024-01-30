package br.com.leuxam.acougue.domain.compras;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.leuxam.acougue.controller.ArquivosComprasController;
import br.com.leuxam.acougue.controller.ComprasController;
import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.ValidacaoException;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueRepository;
import br.com.leuxam.acougue.domain.comprasEstoque.ComprasEstoqueService;
import br.com.leuxam.acougue.domain.fornecedor.FornecedorRepository;
import jakarta.transaction.Transactional;

@Service
public class ComprasService {

	private ComprasRepository comprasRepository;

	private FornecedorRepository fornecedorRepository;
	
	private ArquivosComprasRepository arquivosComprasRepository;
	
	private ComprasEstoqueRepository comprasEstoqueRepository;
	
	private ComprasEstoqueService comprasEstoqueService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PagedResourcesAssembler<ComprasDTO> assembler;

	@Autowired
	public ComprasService(
			ComprasRepository comprasRepository,
			FornecedorRepository fornecedorRepository,
			ArquivosComprasRepository arquivosComprasRepository,
			ComprasEstoqueRepository comprasEstoqueRepository,
			ComprasEstoqueService comprasEstoqueService) {
		this.comprasRepository = comprasRepository;
		this.fornecedorRepository = fornecedorRepository;
		this.arquivosComprasRepository = arquivosComprasRepository;
		this.comprasEstoqueRepository = comprasEstoqueRepository;
		this.comprasEstoqueService = comprasEstoqueService;
	}
	
	@Transactional
	public DadosDetalhamentoCompras create(DadosCriarCompras dados) {

		if (!fornecedorRepository.existsById(dados.idFornecedor()))
			throw new ExisteException("O fornecedor " + dados.idFornecedor() + " não existe");

		var fornecedor = fornecedorRepository.getReferenceById(dados.idFornecedor());

		var compras = new Compras(null, fornecedor, new BigDecimal("0.0"), null, null, LocalDateTime.now());
		compras = comprasRepository.save(compras);
		return new DadosDetalhamentoCompras(compras);
	}

//	@Transactional
//	public PagedModel<EntityModel<ComprasDTO>> findAll(Pageable pageable) {
//		var compras = comprasRepository.findAll(pageable);
//		
//		var comprasDTO = compras.map(c -> modelMapper.map(c, ComprasDTO.class));
//		comprasDTO.forEach(c -> {
//			if(c.getArquivosCompras().size() > 0) {
//				c.add(linkTo(methodOn(ArquivosComprasController.class).downloadFile(c.getId())).withRel("download"));
//			}
//		});
//		
//		Link link = linkTo(methodOn(ComprasController.class).findAll(pageable)).withSelfRel();
//		
//		return assembler.toModel(comprasDTO, link);
//	}
//	
	@Transactional
	public PagedModel<EntityModel<ComprasDTO>> findAllByRazaoFornecedor(Pageable pageable, String razaoSocial) {
		
		Page<Compras> compras;
	
		if(razaoSocial.equalsIgnoreCase("")) {
			compras = comprasRepository.findAll(pageable);
		}else {
			compras = comprasRepository.findAllByRazaoFornecedor(pageable, razaoSocial);
		}
		
		var comprasDTO = compras.map(c -> modelMapper.map(c, ComprasDTO.class));
		comprasDTO.forEach(c -> {
			if(c.getArquivosCompras().size() > 0) {
				c.add(linkTo(methodOn(ArquivosComprasController.class).downloadFile(c.getId())).withRel("download"));
			}
		});
		
		Link link = linkTo(methodOn(ComprasController.class).findAll(pageable, razaoSocial)).withSelfRel();
		
		return assembler.toModel(comprasDTO, link);
	}
	
	@Transactional
	public ComprasDTO findById(Long id) {
		if (!comprasRepository.existsById(id))
			throw new ValidacaoException("Compra nº " + id + " não existe!");

		var compra = comprasRepository.findById(id);
		var compraDTO = modelMapper.map(compra, ComprasDTO.class);
		
		if(compra.get().getArquivosCompras().size() > 0) {
			Link selfLink = linkTo(methodOn(ArquivosComprasController.class).downloadFile(compra.get().getId())).withRel("download");
			compraDTO.add(selfLink);
		}
		return compraDTO;
	}

	@Transactional
	public DadosDetalhamentoCompras update(Long id, DadosAtualizarCompras dados) {
		var compra = comprasRepository.findById(id);

		if (!compra.isPresent())
			throw new ValidacaoException("Compra nº " + id + " não existe!");

		if (dados.idFornecedor() != null) {
			if (!fornecedorRepository.existsById(dados.idFornecedor()))
				throw new ExisteException("O fornecedor " + dados.idFornecedor() + " não existe");
		}

		var fornecedor = fornecedorRepository.getReferenceById(dados.idFornecedor());
		compra.get().atualizar(fornecedor);

		return new DadosDetalhamentoCompras(compra.get());
	}

	@Transactional
	public void delete(Long id) {
		var compra = comprasRepository.findById(id);
		
		if (!compra.isPresent())
			throw new ValidacaoException("Compra nº " + id + " não existe!");
		
		var arquivosCompras = arquivosComprasRepository.findByCompras(compra.get());
		
		if(arquivosCompras.isPresent()) arquivosComprasRepository.delete(arquivosCompras.get());
		
		var comprasEstoque = comprasEstoqueRepository.findByCompras(compra.get().getId());
		
		if(comprasEstoque.size() > 0) {
			comprasEstoque.forEach((ce) -> comprasEstoqueService.delete(compra.get().getId(), ce.getEstoque().getId()));
		}
		
		comprasRepository.delete(compra.get());
	}

	/*
	 * Aqui da para fazer alguma coisa relacionada com o estado da aplicação, como
	 * por exemplo: Em cotação, Efetuado pagamento, Finalizada, Cancelada!
	 */
}
