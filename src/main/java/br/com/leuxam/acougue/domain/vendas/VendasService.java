package br.com.leuxam.acougue.domain.vendas;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

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
	
	@Autowired
	public VendasService(VendasRepository vendasRepository,
			ClienteRepository clienteRepository, VendasEstoqueRepository vendasEstoqueRepository) {
		this.vendasRepository = vendasRepository;
		this.clienteRepository = clienteRepository;
		this.vendasEstoqueRepository = vendasEstoqueRepository;
	}

	@Transactional
	public DadosDetalhamentoVendas create(DadosCriarVendas dados) {
		
		if(!clienteRepository.existsById(dados.idCliente())) throw new ExisteException("Não existe cliente nº " + dados.idCliente());
		
		var cliente = clienteRepository.getReferenceById(dados.idCliente());
		
		var vendas = new Vendas(cliente);
		vendasRepository.save(vendas);
		
		return new DadosDetalhamentoVendas(vendas);
	}

	public Page<DadosDetalhamentoVendas> findAll(Pageable pageable, String idCliente) {
		Page<Vendas> vendas = null;
		
		if(idCliente.equals("0")) vendas = vendasRepository.findAll(pageable);	
		
		if(!idCliente.equals("0")) vendas = vendasRepository.findVendasIdCliente(pageable, Long.valueOf(idCliente));
		
		return vendas.map(DadosDetalhamentoVendas::new);
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
























