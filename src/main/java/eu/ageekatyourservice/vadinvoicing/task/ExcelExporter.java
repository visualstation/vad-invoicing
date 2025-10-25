package eu.ageekatyourservice.vadinvoicing.task;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class ExcelExporter {
    public static void writeDataToSheet(Sheet sheet, List<List<String>> data) {
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data.get(i).size(); j++) {
                row.createCell(j).setCellValue(data.get(i).get(j));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Example datasets; replace these with CSV parsing if needed
        List<List<String>> dataSheet1 = Arrays.asList(
                Arrays.asList("Name", "Age"),
                Arrays.asList("Alice", "30"),
                Arrays.asList("Bob", "25")
        );
        List<List<String>> dataSheet2 = Arrays.asList(
                Arrays.asList("Product", "Price"),
                Arrays.asList("Pen", "1.5"),
                Arrays.asList("Notebook", "2.0")
        );

        Sheet sheet1 = workbook.createSheet("People");
        writeDataToSheet(sheet1, dataSheet1);

        Sheet sheet2 = workbook.createSheet("Products");
        writeDataToSheet(sheet2, dataSheet2);

        try (FileOutputStream fileOut = new FileOutputStream("multi_tab_excel_file.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}
