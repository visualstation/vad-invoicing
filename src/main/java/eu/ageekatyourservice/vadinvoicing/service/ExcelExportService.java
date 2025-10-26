package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public byte[] exportInterventionLogsToExcel(List<InterventionLog> logs) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Intervention Logs");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Timestamp", "Client ID", "Username", "Description", "Duration (s)", "Billed Duration (s)"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            
            int rowNum = 1;
            for (InterventionLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(log.getId());
                cell0.setCellStyle(dataCellStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(log.getTimestamp().format(DATE_FORMATTER));
                cell1.setCellStyle(dataCellStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(log.getClientId());
                cell2.setCellStyle(dataCellStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(log.getUsername());
                cell3.setCellStyle(dataCellStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(log.getDescription());
                cell4.setCellStyle(dataCellStyle);
                
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(log.getDuration());
                cell5.setCellStyle(dataCellStyle);
                
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(log.getBilledDuration());
                cell6.setCellStyle(dataCellStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Add some padding
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
