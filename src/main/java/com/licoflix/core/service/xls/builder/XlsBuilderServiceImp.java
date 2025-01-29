package com.licoflix.core.service.xls.builder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class XlsBuilderServiceImp implements XlsBuilderService {

    @Override
    public byte[] generateXlsFile(String[] header, List<String[]> lines, String fileName, String timezone) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(fileName);

            CellStyle headerStyle = createHeaderStyle(workbook);
            createHeaderRow(sheet, header, headerStyle);
            populateDataRows(sheet, lines);
            autoSizeColumns(sheet, header.length);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        return headerStyle;
    }

    private void createHeaderRow(Sheet sheet, String[] header, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void populateDataRows(Sheet sheet, List<String[]> lines) {
        int rowNum = 1;
        for (String[] line : lines) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < line.length; i++) {
                row.createCell(i).setCellValue(line[i]);
            }
        }
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

}