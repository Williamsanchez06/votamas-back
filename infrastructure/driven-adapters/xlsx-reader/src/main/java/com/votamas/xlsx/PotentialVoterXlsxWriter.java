package com.votamas.xlsx;

import com.votamas.model.potentialvoter.PotentialVoterExportRow;
import com.votamas.model.potentialvoter.gateways.PotentialVoterSpreadsheetWriter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.List;

@Component
public class PotentialVoterXlsxWriter implements PotentialVoterSpreadsheetWriter {
    private static final String[] HEADERS = {
            "Identificación", "Nombres", "Apellidos", "Comuna", "Lugar de votación",
            "Mesa", "Fecha de registro", "Líder asignado"
    };
    private static final int MAX_COLUMN_WIDTH = 50 * 256;
    private static final byte HEADER_RED = (byte) 0xBD;
    private static final byte HEADER_GREEN = (byte) 0x16;
    private static final byte HEADER_BLUE = (byte) 0x22;

    @Override
    public Mono<byte[]> write(List<PotentialVoterExportRow> rows) {
        return Mono.fromCallable(() -> createWorkbook(rows))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private byte[] createWorkbook(List<PotentialVoterExportRow> rows) throws java.io.IOException {
        try (var workbook = new XSSFWorkbook(); var output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Posibles votantes");
            sheet.createFreezePane(0, 1);

            var headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            var headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(
                    new XSSFColor(
                            new byte[]{HEADER_RED, HEADER_GREEN, HEADER_BLUE},
                            new DefaultIndexedColorMap()));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            var dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.getCreationHelper()
                    .createDataFormat().getFormat("yyyy-mm-dd"));

            var header = sheet.createRow(0);
            for (int index = 0; index < HEADERS.length; index++) {
                var cell = header.createCell(index);
                cell.setCellValue(HEADERS[index]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (PotentialVoterExportRow voter : rows) {
                var row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(voter.identification());
                row.createCell(1).setCellValue(voter.firstName());
                row.createCell(2).setCellValue(voter.lastName());
                row.createCell(3).setCellValue(voter.votingZoneName());
                row.createCell(4).setCellValue(voter.pollingPlaceName());
                row.createCell(5).setCellValue(voter.tableNumber());
                var registrationDate = row.createCell(6);
                registrationDate.setCellValue(Date.valueOf(voter.registrationDate()));
                registrationDate.setCellStyle(dateStyle);
                row.createCell(7).setCellValue(voter.assignedLeaderName());
            }

            sheet.setAutoFilter(new CellRangeAddress(0, Math.max(0, rowIndex - 1), 0, HEADERS.length - 1));
            for (int index = 0; index < HEADERS.length; index++) {
                sheet.autoSizeColumn(index);
                sheet.setColumnWidth(index, Math.min(sheet.getColumnWidth(index), MAX_COLUMN_WIDTH));
            }

            workbook.write(output);
            return output.toByteArray();
        }
    }
}
