package co.com.postobon.qa.proyectoapis.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    private ExcelReader(){}

    /**
     * Obtiene los datos de un archivo de excel, teniendo en cuenta el nombre de la hoja
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetName     Nombre de la hoja que contiene los datos
     * @return retorna los datos de la hoja de excel indicada
     * @throws IOException Manejo de error para el proceso de entrada y salida de datos
     */
    static List<Map<String, String>> getData(String excelFilePath, String sheetName) throws IOException {
        Sheet sheet = getSheetByName(excelFilePath, sheetName);
        return readSheet(sheet);
    }

    /**
     * Obtiene los datos de un archivo de excel, teniendo en cuenta el numero de la hoja
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetNumber   Nombre de la hoja que contiene los datos
     * @return retorna los datos de la hoja de excel indicada
     * @throws IOException Manejo de error para el proceso de entrada y salida de datos
     */
    public static List<Map<String, String>> getData(String excelFilePath, int sheetNumber) throws IOException {
        Sheet sheet = getSheetByIndex(excelFilePath, sheetNumber);
        return readSheet(sheet);
    }

    /**
     * Obtiene la hoja de trabajo donde se encuentran los datos de acuerdo a la ruta del archivo
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetName     Nombre de la hoja que contiene los datos
     * @return retorna la hoja de excel con los datos
     */
    private static Sheet getSheetByName(String excelFilePath, String sheetName) throws IOException {
        return getWorkBook(excelFilePath).getSheet(sheetName);
    }

    /**
     * Obtiene los hoja de trabajo donde se encuentran los datos de acuerdo al index de la hoja
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetNumber   Indice de tipo entero de la hoja en el libro de excel
     * @author bgaona
     * @since 27/11/2017
     */
    private static Sheet getSheetByIndex(String excelFilePath, int sheetNumber) throws IOException {
        return getWorkBook(excelFilePath).getSheetAt(sheetNumber);

    }

    /**
     * Devuelve el libro correspondiente a la hoja determinada con antelación
     *
     * @param excelFilePath Ruta del archivo de excel
     * @author bgaona
     * @since 27/11/2017
     */
    private static Workbook getWorkBook(String excelFilePath) throws IOException {
        return WorkbookFactory.create(new File(excelFilePath));
    }

    /**
     * Retorna la lista en forma de Map de todas las filas que contiene la hoja de
     * excel, teniendo en cuenta la primera fila como los nombres de la columna
     *
     * @param sheet Hoja de excel
     * @author bgaona
     * @since 27/11/2017
     */
    private static List<Map<String, String>> readSheet(Sheet sheet) {
        Row row;
        int totalRow = sheet.getPhysicalNumberOfRows(); //-------------> se deja la última fila Se quita una de las filas que la librería considera que tiene datos debido a que deja una en blanco
        List<Map<String, String>> excelRows = new ArrayList<>();
        int headerRowNumber = getHeaderRowNumber(sheet);
        if (headerRowNumber != -1) {
            int totalColumn = sheet.getRow(headerRowNumber).getLastCellNum();
            int setCurrentRow = 1;
            for (int currentRow = setCurrentRow; currentRow <= totalRow; currentRow++) {
                row = getRow(sheet, sheet.getFirstRowNum() + currentRow);
                LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
                for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                    columnMapdata.putAll(getCellValue(sheet, row, currentColumn));
                }
                excelRows.add(columnMapdata);
            }
        }
        return excelRows;
    }

    /**
     * Obtiene el número de filas conceniernte a encabezado de la hoja
     *
     * @author bgaona
     * @since 27/11/2017
     */
    private static int getHeaderRowNumber(Sheet sheet) {
        Row row;
        int totalRow = sheet.getLastRowNum();
        for (int currentRow = 0; currentRow <= totalRow + 1; currentRow++) {
            row = getRow(sheet, currentRow);
            if (row != null) {
                int totalColumn = row.getLastCellNum();
                for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                    Cell cell;
                    cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.BOOLEAN || cell.getCellType() == CellType.ERROR) {
                        return row.getRowNum();
                    }
                }
            }
        }
        return (-1);
    }

    /**
     * Obtiene la fila de acuerdo a la hoja y el número de ésta
     *
     * @author bgaona
     * @since 27/11/2017
     */
    private static Row getRow(Sheet sheet, int rowNumber) {
        return sheet.getRow(rowNumber);
    }

    /**
     * Obtiene el valor de cada una de las celdas -------> reevaluar y dejar como texto todos los valores
     *
     * @author bgaona
     * @since 27/11/2017
     */
    private static LinkedHashMap<String, String> getCellValue(Sheet sheet, Row row, int currentColumn) {
        LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
        Cell cell;
        if (row == null) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn)
                        .getStringCellValue();
                columnMapdata.put(columnHeaderName, "");

            }
        } else {
            cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            columnMapdata = ifLargo(cell, sheet);
        }
        return columnMapdata;
    }

    private static LinkedHashMap<String, String> ifLargo(Cell cell, Sheet sheet) {
        LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
        if (cell.getCellType() == CellType.STRING) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
                        .getStringCellValue();
                columnMapdata.put(columnHeaderName, cell.getStringCellValue());
            }
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
                        .getStringCellValue();
                columnMapdata.put(columnHeaderName, NumberToTextConverter.toText(cell.getNumericCellValue()));
            }
        } else if (cell.getCellType() == CellType.BLANK) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
                        .getStringCellValue();
                columnMapdata.put(columnHeaderName, "");
            }
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
                        .getStringCellValue();
                columnMapdata.put(columnHeaderName, Boolean.toString(cell.getBooleanCellValue()));
            }
        } else if (cell.getCellType() == CellType.ERROR) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
                        .getStringCellValue();
                columnMapdata.put(columnHeaderName, Byte.toString(cell.getErrorCellValue()));
            }
        }
        return columnMapdata;
    }
}

