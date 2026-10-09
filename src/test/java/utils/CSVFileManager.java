package utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

public class CSVFileManager {
    private static final Logger log = LogManager.getLogger(CSVFileManager.class);

    private final String csvFilePath;
    private final List<String> headers = new ArrayList<>();
    private final List<String[]> rows = new ArrayList<>();

    /**
     * Reads the CSV file once and keeps the header names and rows in memory.
     *
     * @param csvFilePath target test data CSV file path
     */
    public CSVFileManager(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        log.info("📄 Reading test data from [{}]", csvFilePath);
        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(csvFilePath))) {
            headers.addAll(parser.getHeaderNames());
            for (CSVRecord record : parser) {
                String[] row = new String[record.size()];
                for (int i = 0; i < record.size(); i++) {
                    row[i] = record.get(i);
                }
                rows.add(row);
            }
            log.debug("✅ Loaded {} rows and {} columns from [{}]", rows.size(), headers.size(), csvFilePath);
        } catch (IOException e) {
            log.error("❌ Couldn't read test data file [{}]", csvFilePath, e);
            throw new UncheckedIOException("Could not read CSV file: " + csvFilePath, e);
        }
    }

    /**
     * Retrieves all rows from the CSV file as a list of string arrays.
     *
     * @return a new list containing every data row
     */
    public List<String[]> getRows() {
        log.debug("📋 Returning {} rows from [{}]", rows.size(), csvFilePath);
        return new ArrayList<>(rows);
    }

    /**
     * Retrieves the column names from the CSV file.
     *
     * @return a list of column names
     */
    public List<String> getColumns() {
        return new ArrayList<>(headers);
    }

    /**
     * Maps each column name to its list of values.
     *
     * @return a map where keys are column names and values are lists of column data
     */
    public Map<String, List<String>> getColumnsWithData() {
        Map<String, List<String>> columnWithRows = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            List<String> columnData = new ArrayList<>();
            for (String[] row : rows) {
                if (i < row.length) {
                    columnData.add(row[i]);
                }
            }
            columnWithRows.put(headers.get(i), columnData);
        }
        log.debug("🗂️ Mapped {} columns to data from [{}]", columnWithRows.size(), csvFilePath);
        return columnWithRows;
    }

    /**
     * Retrieves the name of the last column in the CSV file.
     *
     * @return the name of the last column, or null if there are no columns
     */
    public String getLastColumn() {
        if (headers.isEmpty()) {
            log.warn("⚠️ No columns found in [{}]", csvFilePath);
            return null;
        }
        String last = headers.get(headers.size() - 1);
        log.debug("🔚 Last column: {}", last);
        return last;
    }

    /**
     * Retrieves the name of the first column in the CSV file.
     *
     * @return the name of the first column, or null if there are no columns
     */
    public String getFirstColumn() {
        if (headers.isEmpty()) {
            log.warn("⚠️ No columns found in [{}]", csvFilePath);
            return null;
        }
        log.debug("🔝 First column: {}", headers.get(0));
        return headers.get(0);
    }

    /**
     * Retrieves the name of a specific column based on its index.
     *
     * @param ColumnNum the 1-based index of the column
     * @return the column name, or null if the index is out of range
     */
    public String getSpecificColumnName(int ColumnNum) {
        if (ColumnNum < 1 || ColumnNum > headers.size()) {
            log.warn("⚠️ Column index {} is out of range (1-{})", ColumnNum, headers.size());
            return null;
        }
        log.debug("🏷️ Column {} is {}", ColumnNum, headers.get(ColumnNum - 1));
        return headers.get(ColumnNum - 1);
    }

    /**
     * Retrieves all data for a specific column.
     *
     * @param ColumnName the name of the column
     * @return the column values, or an empty list if the column does not exist
     */
    public List<String> GetSpecificColumnData(String ColumnName) {
        List<String> data = getColumnsWithData().get(ColumnName);
        if (data == null) {
            log.warn("⚠️ Column [{}] not found in [{}]", ColumnName, csvFilePath);
            return Collections.emptyList();
        }
        log.debug("📦 Column {} has {} values", ColumnName, data.size());
        return data;
    }

    /**
     * Retrieves all data for a specific column.
     *
     * @param ColumnIndex the 1-based index of the column
     * @return the column values, or an empty list if the index is out of range
     */
    public List<String> GetSpecificColumnData(int ColumnIndex) {
        String name = getSpecificColumnName(ColumnIndex);
        if (name == null) {
            return Collections.emptyList();
        }
        return GetSpecificColumnData(name);
    }

    /**
     * Retrieves a specific cell's data based on row number and column name.
     *
     * @param RowNum the 0-based index of the row
     * @param ColumnName the name of the column
     * @return the cell value, or null if the row or column is not found
     */
    public String getCellData(int RowNum, String ColumnName) {
        int col = headers.indexOf(ColumnName);
        if (col < 0 || RowNum < 0 || RowNum >= rows.size()) {
            log.warn("⚠️ Cell not found. Row: {}, Column: {}", RowNum, ColumnName);
            return null;
        }
        String[] row = rows.get(RowNum);
        if (col >= row.length) {
            log.warn("⚠️ Row {} has no value for column {}", RowNum, ColumnName);
            return null;
        }
        return row[col];
    }

    /**
     * Retrieves a specific cell's data based on row number and column index.
     *
     * @param RowNum the 0-based index of the row
     * @param ColumnIndex the 1-based index of the column
     * @return the cell value, or null if the row or column is not found
     */
    public String getCellData(int RowNum, int ColumnIndex) {
        String name = getSpecificColumnName(ColumnIndex);
        if (name == null) {
            return null;
        }
        return getCellData(RowNum, name);
    }

    /**
     * Retrieves the minimum value from a specific column.
     *
     * @param columnName the name of the column
     * @return the minimum value, or Double.NaN if it cannot be calculated
     */
    public double getMinCellValue(String columnName) {
        return findMin(GetSpecificColumnData(columnName), columnName);
    }

    /**
     * Retrieves the minimum value from a specific column.
     *
     * @param columnIndex the 0-based index of the column
     * @return the minimum value, or Double.NaN if it cannot be calculated
     */
    public double getMinCellValue(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= headers.size()) {
            log.warn("⚠️ Column index {} is out of range", columnIndex);
            return Double.NaN;
        }
        return findMin(GetSpecificColumnData(columnIndex + 1), headers.get(columnIndex));
    }

    /**
     * Retrieves the maximum value from a specific column.
     *
     * @param columnName the name of the column
     * @return the maximum value, or Double.NaN if it cannot be calculated
     */
    public double getMaxCellValue(String columnName) {
        return findMax(GetSpecificColumnData(columnName), columnName);
    }

    /**
     * Retrieves the maximum value from a specific column.
     *
     * @param columnIndex the 0-based index of the column
     * @return the maximum value, or Double.NaN if it cannot be calculated
     */
    public double getMaxCellValue(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= headers.size()) {
            log.warn("⚠️ Column index {} is out of range", columnIndex);
            return Double.NaN;
        }
        return findMax(GetSpecificColumnData(columnIndex + 1), headers.get(columnIndex));
    }

    /**
     * Retrieves the total count of cells in a specific column.
     *
     * @param columnName the name of the column
     * @return the number of cells, or 0 if the column does not exist
     */
    public int getCellCount(String columnName) {
        return GetSpecificColumnData(columnName).size();
    }

    /**
     * Retrieves the total count of cells in a specific column.
     *
     * @param columnIndex the 0-based index of the column
     * @return the number of cells, or 0 if the index is out of range
     */
    public int getCellCount(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= headers.size()) {
            log.warn("⚠️ Column index {} is out of range", columnIndex);
            return 0;
        }
        return GetSpecificColumnData(columnIndex + 1).size();
    }

    private double findMin(List<String> values, String column) {
        if (values.isEmpty()) {
            log.warn("⚠️ No values to compare in column {}", column);
            return Double.NaN;
        }
        try {
            double min = Double.MAX_VALUE;
            for (String value : values) {
                min = Math.min(min, Double.parseDouble(value));
            }
            log.debug("📉 Min of column {} is {}", column, min);
            return min;
        } catch (NumberFormatException e) {
            log.error("❌ Non-numeric value in column {}", column, e);
            return Double.NaN;
        }
    }

    private double findMax(List<String> values, String column) {
        if (values.isEmpty()) {
            log.warn("⚠️ No values to compare in column {}", column);
            return Double.NaN;
        }
        try {
            double max = -Double.MAX_VALUE;
            for (String value : values) {
                max = Math.max(max, Double.parseDouble(value));
            }
            log.debug("📈 Max of column {} is {}", column, max);
            return max;
        } catch (NumberFormatException e) {
            log.error("❌ Non-numeric value in column {}", column, e);
            return Double.NaN;
        }
    }
}