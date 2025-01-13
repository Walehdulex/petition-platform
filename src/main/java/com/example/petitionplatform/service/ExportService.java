package com.example.petitionplatform.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import com.example.petitionplatform.model.Petition;
import com.example.petitionplatform.repository.PetitionRepository;


@Service
@Transactional(readOnly = true)
public class ExportService {
    private final PetitionRepository petitionRepository;

    public ExportService(PetitionRepository petitionRepository) {
        this.petitionRepository = petitionRepository;
    }

    public ByteArrayResource generateExcelReport() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Petitions Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Title", "Status", "Creator", "Signatures", "Created Date", "Response"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Add data rows
        List<Petition> petitions = petitionRepository.findAll();
        int rowNum = 1;
        for (Petition petition : petitions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(petition.getId());
            row.createCell(1).setCellValue(petition.getTitle());
            row.createCell(2).setCellValue(petition.getStatus().toString());
            row.createCell(3).setCellValue(petition.getCreator().getEmail());
            row.createCell(4).setCellValue(petition.getSignatures().size());
            row.createCell(5).setCellValue(petition.getCreatedAt().toString());
            row.createCell(6).setCellValue(petition.getResponse() != null ? petition.getResponse() : "");
        }

        // Autosize columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return new ByteArrayResource(outputStream.toByteArray());
    }

    public ByteArrayResource generatePdfReport() throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Petitions Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Create table
        PdfPTable table = new PdfPTable(5); // 5 columns
        table.setWidthPercentage(100);

        // Add header cells
        Stream.of("Title", "Status", "Creator", "Signatures", "Response")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });

        // Add data rows
        List<Petition> petitions = petitionRepository.findAll();
        for (Petition petition : petitions) {
            table.addCell(petition.getTitle());
            table.addCell(petition.getStatus().toString());
            table.addCell(petition.getCreator().getEmail());
            table.addCell(String.valueOf(petition.getSignatures().size()));
            table.addCell(petition.getResponse() != null ? petition.getResponse() : "");
        }

        document.add(table);
        document.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }
}