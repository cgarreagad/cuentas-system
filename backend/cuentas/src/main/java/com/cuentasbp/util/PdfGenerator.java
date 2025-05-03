package com.cuentasbp.util;

import com.cuentasbp.domain.dto.response.EstadoCuentaResponseDTO;
import com.cuentasbp.domain.dto.response.MovimientoResponseDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class PdfGenerator {


    public byte[] generarEstadoCuentaPDF(EstadoCuentaResponseDTO estadoCuenta) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // Título
            Paragraph titulo = new Paragraph("ESTADO DE CUENTA")
                    .setFontSize(18)
                    .setFont(bold)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Información del cliente
            document.add(new Paragraph("Cliente: " + estadoCuenta.getCliente()));
            document.add(new Paragraph("Identificación: " + estadoCuenta.getIdentificacion()));
            document.add(new Paragraph("Período: " +
                    estadoCuenta.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " - " +
                    estadoCuenta.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

            document.add(new Paragraph("\n"));

            // Resumen
            document.add(new Paragraph("RESUMEN:").setFont(bold));
            document.add(new Paragraph("Total Débitos: $" + estadoCuenta.getTotalDebitos()));
            document.add(new Paragraph("Total Créditos: $" + estadoCuenta.getTotalCreditos()));

            document.add(new Paragraph("\n"));

            // Por cada cuenta
            for (EstadoCuentaResponseDTO.CuentaEstadoDTO cuenta : estadoCuenta.getCuentas()) {
                // Información de la cuenta
                document.add(new Paragraph("CUENTA: " + cuenta.getNumeroCuenta()).setFont(bold));
                document.add(new Paragraph("Tipo: " + cuenta.getTipoCuenta()));
                document.add(new Paragraph("Saldo Inicial: $" + cuenta.getSaldoInicial()));
                document.add(new Paragraph("Saldo Final: $" + cuenta.getSaldoFinal()));

                document.add(new Paragraph("\n"));

                // Tabla de movimientos
                Table table = new Table(5);
                table.setWidth(512);

                // Encabezados
                table.addHeaderCell(createHeaderCell("Fecha"));
                table.addHeaderCell(createHeaderCell("Tipo"));
                table.addHeaderCell(createHeaderCell("Valor"));
                table.addHeaderCell(createHeaderCell("Saldo"));
                table.addHeaderCell(createHeaderCell("Descripción"));

                // Filas de movimientos
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (MovimientoResponseDTO movimiento : cuenta.getMovimientos()) {
                    table.addCell(movimiento.getFecha().format(formatter));
                    table.addCell(movimiento.getTipoMovimiento());

                    String valorStr = "$" + movimiento.getValor();
                    if ("RETIRO".equals(movimiento.getTipoMovimiento())) {
                        valorStr = "-" + valorStr;
                    }
                    table.addCell(valorStr);

                    table.addCell("$" + movimiento.getSaldo());
                    table.addCell(movimiento.getTipoMovimiento() + " en cuenta " + movimiento.getNumeroCuenta());
                }

                document.add(table);
                document.add(new Paragraph("\n"));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }

    private Cell createHeaderCell(String text) {
        PdfFont bold = null;
        try {
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Cell cell = new Cell().add(new Paragraph(text));
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cell.setFont(bold);
            cell.setTextAlignment(TextAlignment.CENTER);
            return cell;
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }

    }
}
