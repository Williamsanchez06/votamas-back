package com.votamas.xlsx;

import com.votamas.model.exception.BusinessException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.ValidationException;
import com.votamas.model.potentialvoter.PotentialVoterImportData;
import com.votamas.model.potentialvoter.PotentialVoterImportRow;
import com.votamas.model.potentialvoter.gateways.PotentialVoterSpreadsheetReader;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class PotentialVoterXlsxReader implements PotentialVoterSpreadsheetReader {
    private static final String IDENTIFICATION = "identificacion";
    private static final String FIRST_NAME = "nombres";
    private static final String LAST_NAME = "apellidos";
    private static final String VOTING_ZONE = "comuna";
    private static final String POLLING_PLACE = "lugar de votacion";
    private static final String TABLE_NUMBER = "mesa de votacion";
    private static final Set<String> REQUIRED_HEADERS = Set.of(
            IDENTIFICATION, FIRST_NAME, LAST_NAME, VOTING_ZONE,
            POLLING_PLACE, TABLE_NUMBER);

    private final int maxRows;

    public PotentialVoterXlsxReader(
            @Value("${imports.potential-voters.max-rows:5000}") int maxRows) {
        this.maxRows = maxRows;
    }

    @Override
    public Mono<PotentialVoterImportData> read(byte[] content) {
        return Mono.fromCallable(() -> parse(content)).subscribeOn(Schedulers.boundedElastic());
    }

    private PotentialVoterImportData parse(byte[] content) {
        if (content == null || content.length == 0) {
            throw new ValidationException(MessageError.INVALID_SPREADSHEET);
        }
        try (var input = new ByteArrayInputStream(content); var workbook = new XSSFWorkbook(input)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new ValidationException(MessageError.INVALID_SPREADSHEET);
            }
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                throw new ValidationException(MessageError.MISSING_SPREADSHEET_HEADERS);
            }

            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Map<String, Integer> columns = columns(headerRow, formatter, evaluator);
            if (!columns.keySet().containsAll(REQUIRED_HEADERS)) {
                throw new ValidationException(MessageError.MISSING_SPREADSHEET_HEADERS);
            }

            List<PotentialVoterImportRow> rows = new ArrayList<>();
            int skippedRows = 0;
            for (int index = headerRow.getRowNum() + 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                if (isEmpty(row, columns.values(), formatter, evaluator)) {
                    skippedRows++;
                    continue;
                }
                if (rows.size() >= maxRows) {
                    throw new ValidationException(MessageError.SPREADSHEET_ROW_LIMIT_EXCEEDED);
                }
                rows.add(new PotentialVoterImportRow(
                        index + 1,
                        value(row, columns.get(IDENTIFICATION), formatter, evaluator),
                        value(row, columns.get(FIRST_NAME), formatter, evaluator),
                        value(row, columns.get(LAST_NAME), formatter, evaluator),
                        value(row, columns.get(VOTING_ZONE), formatter, evaluator),
                        value(row, columns.get(POLLING_PLACE), formatter, evaluator),
                        value(row, columns.get(TABLE_NUMBER), formatter, evaluator)
                ));
            }
            return new PotentialVoterImportData(rows, skippedRows);
        } catch (BusinessException exception) {
            throw exception;
        } catch (IOException | POIXMLException | IllegalArgumentException exception) {
            throw new ValidationException(MessageError.INVALID_SPREADSHEET, exception);
        }
    }

    private Map<String, Integer> columns(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        Map<String, Integer> columns = new LinkedHashMap<>();
        for (Cell cell : row) {
            String header = normalizeHeader(formatter.formatCellValue(cell, evaluator));
            if (!header.isBlank()) {
                columns.putIfAbsent(header, cell.getColumnIndex());
            }
        }
        return columns;
    }

    private boolean isEmpty(Row row, Iterable<Integer> columns, DataFormatter formatter,
                            FormulaEvaluator evaluator) {
        if (row == null) {
            return true;
        }
        for (Integer column : columns) {
            if (!value(row, column, formatter, evaluator).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String value(Row row, int column, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim();
    }

    private String normalizeHeader(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .trim()
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("\\s+", " ");
    }
}
