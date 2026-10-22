package nttdata.bank.service.transacao;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import nttdata.bank.domain.dto.transacao.TransacaoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class RelatorioTransacaoService {

    private static final Logger log = LoggerFactory.getLogger(RelatorioTransacaoService.class);

    private static final String R_SIFRAO = "R$";

    public void gerarRelatorioTransacaoPdf(List<TransacaoDTO> transacoes, OutputStream outputStream) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
        pdfDoc.setDefaultPageSize(PageSize.A2);
        Document document = new Document(pdfDoc);

        float[] columnWidths = {0.8f, 0.7f,0.7f ,0.9f , 0.6f, 1, 0.6f, 0.7f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        montarTabela(table, transacoes);
        configurarDocumento(document, transacoes.get(0), table);

        document.close();
    }

    private void montarTabela(Table table, List<TransacaoDTO> transacoes){
        table.setWidth(UnitValue.createPercentValue(101));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        try {
            addHeader(table);
        } catch (IOException e) {
            log.warn("Erro ao adicionar cabeçalho na tabela");
            throw new RuntimeException(e);
        }

        for (TransacaoDTO transacao : transacoes) {
            addTransactionRow(table, transacao);
        }
    }

    private void configurarDocumento(Document document, TransacaoDTO transacao, Table table) throws IOException {
        document.add(new Paragraph("RELATÓRIO DE TRANSAÇÕES - NTT BANK")
                .setFontSize(14)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Nome: " + transacao.contaOrigem().getUsuario().getNome())
                .setFontSize(14));
        document.add(new Paragraph("Tipo de Conta: " +  "Conta Corrente")
                .setFontSize(14));
        document.add(new Paragraph("Email: " +  transacao.contaOrigem().getUsuario().getEmail())
                .setFontSize(14));

        document.add(new Paragraph("\n"));
        document.add(new LineSeparator(new SolidLine()));
        document.add(new Paragraph("\n"));
        document.add(table);
        document.add(new LineSeparator(new SolidLine()));
        document.add(new Paragraph("NTT Bank - Confidential")
                .setFontSize(10)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addHeader(Table table) throws IOException {
        table.addHeaderCell(createHeaderCell("Data"));
        table.addHeaderCell(createHeaderCell("Conta Origem"));
        table.addHeaderCell(createHeaderCell("Conta Destino"));
        table.addHeaderCell(createHeaderCell("Valor"));
        table.addHeaderCell(createHeaderCell("Taxa Cambio"));
        table.addHeaderCell(createHeaderCell("Tipo Transacao"));
        table.addHeaderCell(createHeaderCell("Despesa"));
        table.addHeaderCell(createHeaderCell("Status"));
    }

    private Cell createHeaderCell(String text) throws IOException {
        return new Cell().add(new Paragraph(text))
                .addStyle(createHeaderCellStyle())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Style createHeaderCellStyle() {
        return new Style()
                .setFontSize(12)
                .setWidth(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    private void addTransactionRow(Table table, TransacaoDTO transacao) {
        table.addCell(transacao.dataTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        table.addCell(transacao.getCodigoEDigitoContaOrigem());
        table.addCell(transacao.getCodigoEDigitoContaDestino());
        table.addCell(R_SIFRAO.concat(transacao.valorTransacao().toString()));
        table.addCell(Objects.isNull(transacao.taxaCambio()) ? "-" : R_SIFRAO.concat(transacao.taxaCambio().toString()));
        table.addCell(transacao.tipoTransacaoFinanceira().getDescricao());
        table.addCell(transacao.tipoDespesa().getDescricao());
        table.addCell(transacao.statusTransacao().getDescricao()).addStyle(createStatusCellStyle());
    }

    private Style createStatusCellStyle() {
        return new Style()
                .setFontSize(12)
                .setWidth(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

}
