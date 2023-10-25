package br.com.leuxam.acougue.domain.utils;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import br.com.leuxam.acougue.domain.vendasEstoque.VendasEstoque;

public class Utils {
	
	public static ByteArrayOutputStream GeradorDePdf(List<VendasEstoque> vendasEstoque,
			Long id) throws DocumentException {
		Document document = new Document(PageSize.A5);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, outputStream);
		
		float x = (float) (document.getPageSize().getWidth() * 0.55);
		float y = (float) (document.getPageSize().getHeight() * 0.85);
		document.setMargins(15, 15, 15, 15);
		
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        titleFont.setStyle(FontStyle.BOLD.getValue());
        Font textFont = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK);
        Font textHeaderFont = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK);
        textHeaderFont.setStyle(FontStyle.BOLD.getValue());
        Font smallTextFont = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

        Paragraph title = new Paragraph("NOME DE FANTASIA", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph info = new Paragraph();
        info.setFont(textFont);
        info.add("NOME DA RAZÃO SOCIAL COM DE INFORMATIC LTDA\n");
        info.add("Rua dos Equipamentos, 9 - Sobreloja 101 e 102\n");
        info.add("Centro - 20000-000 - Rio de Janeiro/RJ\n");
        info.add("(21) 1111-2222 / 3344-5566\n");
        info.add("CNPJ:11.111.111/0001-11                                    IE:11.222.333\n");
        info.setAlignment(Element.ALIGN_CENTER);
        
        document.add(info);

        Paragraph clientInfo = new Paragraph("CLIENTE: CONSUMIDOR FINAL", textFont);
        clientInfo.setAlignment(Element.ALIGN_LEFT);
        document.add(clientInfo);
        
        Paragraph hourInfo = new Paragraph("01/04/2023 10:15                                               Nº 000001", textFont);
        hourInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(hourInfo);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        float[] columnWidths = {1.5f, 4.75f, 0.95f};
        table.setWidths(columnWidths);
        
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        
        table.addCell(new Phrase("CODIGO", textFont));
        table.addCell(new Phrase("DESCRIÇÃO (QNT x UNIT)", textFont));
        table.addCell(new Phrase("R$ VALOR", textFont));
        
        Paragraph espace = new Paragraph(new Chunk("=================================================================================\n", smallTextFont));
        document.add(espace);
        document.add(table);
        PdfPTable table2 = new PdfPTable(3);
        table2.setWidthPercentage(100);
        table2.setWidths(columnWidths);
        
        table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        
        for (int i = 0; i < 2; i++) {
        	table2.addCell(new Phrase("00001", smallTextFont));
        	table2.addCell(new Phrase("NOME DE ALGUM ITEM AQUI",smallTextFont));
        	table2.addCell(new Phrase("250,00", smallTextFont));

        	table2.addCell(" ");
        	table2.addCell(new Phrase("01 x 100,00", smallTextFont));
        	table2.addCell(" ");
        }
        
        document.add(espace);
        document.add(table2);
        document.add(espace);
        
		
        Paragraph total = new Paragraph();
        total.setFont(textFont);
        total.add("Valor Total da Nota R$                                           250,00\n\n");
        total.add("FORMA DE PGTO.: Á VISTA");
        document.add(total);
        document.add(new Paragraph("\n"));
        
        PdfPTable table3 = new PdfPTable(3);
        table3.setWidthPercentage(100);
        float[] columnWidths2 = {4.75f, 1.5f, 1.25f};
        table3.setWidths(columnWidths2);
        
        table3.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        
        table3.addCell(new Phrase("DATA PGTO", textHeaderFont));
        table3.addCell(new Phrase("R$ VALOR", textHeaderFont));
        table3.addCell(new Phrase("TIPO PGTO", textHeaderFont));
        
        table3.addCell(new Phrase("01/04/2023", textFont));
        table3.addCell(new Phrase("R$ 250,00", textFont));
        table3.addCell(new Phrase("PIX", textFont));
        
        document.add(table3);
        document.add(espace);
        document.add(new Phrase("VENDENDOR(A): RODRIGO", textFont));
        document.add(espace);
        
        Paragraph infoFinal = new Paragraph();
        infoFinal.setAlignment(Element.ALIGN_CENTER);
        infoFinal.setPaddingTop(50f);
        infoFinal.setFont(textHeaderFont);
        infoFinal.add(" *** Declaramos que este ticket não é um documento fiscal *** ");
        
        document.add(infoFinal);
        document.close();
        
		return outputStream;
	}
}






























