package br.com.leuxam.acougue.domain.vendas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.ByteArrayOutputStream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import br.com.leuxam.acougue.controller.VendasController;
import br.com.leuxam.acougue.domain.ExisteException;
import br.com.leuxam.acougue.domain.arquivosCompras.ArquivosComprasService;
import br.com.leuxam.acougue.domain.arquivosCompras.FileException;
import br.com.leuxam.acougue.domain.cliente.ClienteRepository;
import br.com.leuxam.acougue.domain.compras.DadosCriarVendas;
import br.com.leuxam.acougue.domain.utils.Utils;
import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoqueRepository;
import jakarta.transaction.Transactional;

@Service
public class VendasService {
	
	private VendasRepository vendasRepository;
	
	private ClienteRepository clienteRepository;
	
	private VendasEstoqueRepository vendasEstoqueRepository;
	
	private ArquivosComprasService service;
	
	private ModelMapper modelMapper;
	
	private PagedResourcesAssembler<VendasDTO> assembler;
	
	@Autowired
	public VendasService(VendasRepository vendasRepository,
			ClienteRepository clienteRepository, VendasEstoqueRepository vendasEstoqueRepository,
			PagedResourcesAssembler<VendasDTO> assembler, ModelMapper modelMapper) {
		this.vendasRepository = vendasRepository;
		this.clienteRepository = clienteRepository;
		this.vendasEstoqueRepository = vendasEstoqueRepository;
		this.assembler = assembler;
		this.modelMapper = modelMapper;
	}

	@Transactional
	public DadosDetalhamentoVendas create(DadosCriarVendas dados) {
		
		if(!clienteRepository.existsById(dados.idCliente())) throw new ExisteException("Não existe cliente nº " + dados.idCliente());
		
		var cliente = clienteRepository.getReferenceById(dados.idCliente());
		
		var vendas = new Vendas(cliente);
		vendasRepository.save(vendas);
		
		return new DadosDetalhamentoVendas(vendas);
	}

	public PagedModel<EntityModel<VendasDTO>> findAll(Pageable pageable, String idCliente) {
		Page<Vendas> vendas = null;
		
		if(idCliente.equals("0")) vendas = vendasRepository.findAll(pageable);	
		
		if(!idCliente.equals("0")) vendas = vendasRepository.findVendasIdCliente(pageable, Long.valueOf(idCliente));
		
		var vendasDTO = vendas.map(v -> modelMapper.map(v, VendasDTO.class));
		
		vendasDTO.forEach(v -> {
			if(v.getFileName() != null) {
				v.add(linkTo(methodOn(VendasController.class).downloadPdf(v.getId())).withRel("download"));
			}
		});
		
		Link link = linkTo(methodOn(VendasController.class).findAll(pageable, idCliente)).withSelfRel();
		
		return assembler.toModel(vendasDTO, link);
//		return vendas.map(DadosDetalhamentoVendas::new);
	}
	
	@Transactional
	public DadosDetalhamentoVendas update(Long id, DadosAtualizarVenda dados) {
		
		if(!vendasRepository.existsById(id)) throw new ExisteException("Não existe venda nº " + id);
		
		var venda = vendasRepository.findById(id);
		venda.get().atualizar(dados);
		return new DadosDetalhamentoVendas(venda.get());
	}
	
	@Transactional
	public ByteArrayOutputStream gerarPdf(Long id){
		if(!vendasRepository.existsById(id)) throw new ExisteException("Não existe venda nº " + id);
		
		var vendas = vendasRepository.findById(id).get();
		var vendasEstoque = vendasEstoqueRepository.findAllVendasEstoque(id);
		ByteArrayOutputStream outputStream = null;
		
		try {
			outputStream = Utils.GeradorDePdf(vendasEstoque, vendas);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		String tituloArquivo = "venda " + id + ".pdf";
		vendas.arquivarPdf(outputStream, tituloArquivo);
		
		return outputStream;
	}

	public Vendas findByIdAndArchive(Long id) {
		
		if(!vendasRepository.existsById(id)) throw new ExisteException("Não existe venda nº " + id);
		
		var vendas = vendasRepository.getReferenceById(id);
		
		if(vendas.getData() == null) throw new FileException("Não existe nenhum cupom não fiscal para a Venda nº " + id);
		
		return vendas;
	}

	public DadosDetalhamentoVendas findById(Long id) {
		if(!vendasRepository.existsById(id)) throw new ExisteException("Não existe venda nº " + id);
		
		var vendas = vendasRepository.getReferenceById(id);
		
		return new DadosDetalhamentoVendas(vendas);
	}
}
























