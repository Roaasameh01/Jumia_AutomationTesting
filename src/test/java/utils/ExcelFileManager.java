package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelFileManager is a utility class to handle Excel file operations.
 * It provides methods to read data from an Excel sheet including headers, row data, and column data.
 */
public class ExcelFileManager {

    private static final Logger log = LogManager.getLogger(ExcelFileManager.class);
    private final DataFormatter formatter = new DataFormatter();
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    /**
     * Opens an Excel file and loads the given sheet.
     *
     * @param excelFilePath the path to the Excel file
     * @param sheetName the name of the sheet to be loaded
     */
    public ExcelFileManager(String excelFilePath, String sheetName) {
        log.info("📗 Opening Excel file: {}", excelFilePath);
        try (FileInputStream fileInputStream = new FileInputStream(excelFilePath)) {
            workbook = new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            log.error("❌ Could not open Excel file: {}", excelFilePath, e);
            throw new UncheckedIOException("Could not open Excel file: " + excelFilePath, e);
        }

        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            log.error("❌ Sheet [{}] not found in {}", sheetName, excelFilePath);
            closeExcelFile();
            throw new IllegalArgumentException("Sheet not found: " + sheetName);
        }
        log.info("✅ Sheet loaded: {}", sheetName);
    }

    /**
     * Retrieves the total number of rows in the Excel sheet.
     *
     * @return the number of physical rows in the sheet
     */
    public int getRowCount() {
        int rowCount = sheet.getPhysicalNumberOfRows();
        log.info("📊 Total rows: {}", rowCount);
        return rowCount;
    }

    /**
     * Retrieves the total number of columns in the first row of the sheet.
     *
     * @return the number of columns, or 0 if the header row is empty
     */
    public int getColumnCount() {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            log.warn("⚠️ Header row is empty in sheet {}", sheet.getSheetName());
            return 0;
        }
        int columnCount = Math.max(headerRow.getLastCellNum(), 0);
        log.debug("🧮 Total columns: {}", columnCount);
        return columnCount;
    }

    /**
     * Retrieves the formula present in a specific cell if available.
     *
     * @param rowNum the 0-based index of the row
     * @param colNum the 0-based index of the column
     * @return the formula as a string, or a message if no formula is found
     */
    public String getSpecificCellFormula(int rowNum, int colNum) {
        Cell cell = getCell(rowNum, colNum);
        if (cell != null && cell.getCellType() == CellType.FORMULA) {
            log.info("🧾 Formula found at row {}, column {}", rowNum, colNum);
            return cell.getCellFormula();
        }
        log.warn("⚠️ No formula found at row {}, column {}", rowNum, colNum);
        return "There's No Formula here";
    }

    /**
     * Retrieves the data present in a specific cell.
     *
     * @param rowNum the 0-based index of the row
     * @param colNum the 0-based index of the column
     * @return the cell data as a formatted string, or an empty string if the cell is blank
     */
    public String getSpecificCellData(int rowNum, int colNum) {
        String cellData = formatter.formatCellValue(getCell(rowNum, colNum));
        log.debug("🔤 Cell data at row {}, column {}: {}", rowNum, colNum, cellData);
        return cellData;
    }

    /**
     * Retrieves all column headers from the first row of the sheet.
     *
     * @return a list of header names
     */
    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        int columns = getColumnCount();
        for (int i = 0; i < columns; i++) {
            headers.add(getSpecificCellData(0, i));
        }
        log.info("🏷️ Headers retrieved: {}", headers);
        return headers;
    }

    /**
     * Retrieves all column data mapped to their respective headers.
     *
     * @return a map where keys are column headers and values are lists of column data
     */
    public Map<String, List<String>> getColumnsData() {
        Map<String, List<String>> columnData = new LinkedHashMap<>();
        List<String[]> rows = getRows();
        List<String> headers = getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            List<String> columnValues = new ArrayList<>();
            for (String[] row : rows) {
                columnValues.add(row[i]);
            }
            columnData.put(headers.get(i), columnValues);
        }
        log.info("🗂️ Column data retrieved for {} columns", columnData.size());
        return columnData;
    }

    /**
     * Retrieves all rows of data from the sheet, excluding headers.
     *
     * @return a list of string arrays where each array represents a row
     */
    public List<String[]> getRows() {
        int columns = getColumnCount();
        List<String[]> data = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            String[] rowData = new String[columns];
            for (int j = 0; j < columns; j++) {
                rowData[j] = getSpecificCellData(i, j);
            }
            data.add(rowData);
        }
        log.info("📋 Rows retrieved: {}", data.size());
        return data;
    }

    /**
     * Closes the Excel file and releases resources.
     */
    public void closeExcelFile() {
        if (workbook == null) {
            return;
        }
        try {
            workbook.close();
            log.info("🛑 Excel file closed successfully");
        } catch (IOException e) {
            log.error("❌ Error closing Excel file", e);
            throw new UncheckedIOException("Could not close Excel file", e);
        }
    }

    /**
     * Returns the cell at the given position, or null if its row or cell does not exist.
     */
    private Cell getCell(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        return row == null ? null : row.getCell(colNum);
    }
}