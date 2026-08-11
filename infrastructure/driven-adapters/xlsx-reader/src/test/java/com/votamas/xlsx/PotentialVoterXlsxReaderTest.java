package com.votamas.xlsx;

import com.votamas.model.exception.ValidationException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class PotentialVoterXlsxReaderTest {
    private final PotentialVoterXlsxReader reader = new PotentialVoterXlsxReader(5000);

    @Test
    void shouldReadStringsNumbersFormulasAndSkipEmptyRows() throws Exception {
        byte[] file;
        try (var workbook = workbookWithHeaders("Identificación", " NOMBRES ", "apellidos",
                "ZONA", "lugar de votación", "mesa de votacion")) {
            var sheet = workbook.getSheetAt(0);
            var numeric = sheet.createRow(1);
            numeric.createCell(0).setCellValue(1090123456d);
            numeric.createCell(1).setCellValue("Ana");
            numeric.createCell(2).setCellValue("Pérez");
            numeric.createCell(3).setCellValue("Zona 1");
            numeric.createCell(4).setCellValue("Colegio Central");
            numeric.createCell(5).setCellValue(12);

            sheet.createRow(2);
            var text = sheet.createRow(3);
            text.createCell(0, CellType.STRING).setCellValue("00123");
            text.createCell(1).setCellValue("Luis");
            text.createCell(2).setCellValue("Díaz");
            text.createCell(3).setCellValue("Zona 2");
            text.createCell(4).setCellValue("Escuela Norte");
            text.createCell(5, CellType.STRING).setCellValue("7");
            file = bytes(workbook);
        }

        StepVerifier.create(reader.read(file))
                .assertNext(data -> {
                    assertThat(data.rows()).hasSize(2);
                    assertThat(data.skippedRows()).isEqualTo(1);
                    assertThat(data.rows().get(0).identification()).isEqualTo("1090123456");
                    assertThat(data.rows().get(0).tableNumber()).isEqualTo("12");
                    assertThat(data.rows().get(1).identification()).isEqualTo("00123");
                    assertThat(data.rows().get(1).tableNumber()).isEqualTo("7");
                    assertThat(data.rows().get(0).rowNumber()).isEqualTo(2);
                    assertThat(data.rows().get(1).rowNumber()).isEqualTo(4);
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectMissingHeaders() throws Exception {
        byte[] file;
        try (var workbook = workbookWithHeaders("identificacion", "nombres")) {
            file = bytes(workbook);
        }
        StepVerifier.create(reader.read(file)).expectError(ValidationException.class).verify();
    }

    @Test
    void shouldReadTemplateWithTitleRowsAliasesAndNumberedEmptyRows() throws Exception {
        byte[] file;
        try (var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("REGISTRO DE VOTANTES");
            sheet.createRow(0).createCell(0).setCellValue("REGISTRO DE VOTANTES POTENCIALES — CUCUTA");
            var header = sheet.createRow(3);
            String[] headers = {"#", "IDENTIFICACIÓN", "NOMBRES", "APELLIDOS", "ZONA",
                    "LUGAR DE VOTACIÓN", "MESA"};
            for (int index = 0; index < headers.length; index++) {
                header.createCell(index).setCellValue(headers[index]);
            }

            var voter = sheet.createRow(4);
            voter.createCell(0).setCellValue(1);
            voter.createCell(1).setCellValue("23324434343");
            voter.createCell(2).setCellValue("William");
            voter.createCell(3).setCellValue("Sánchez");
            voter.createCell(4).setCellValue("ZONA 99");
            voter.createCell(5).setCellValue("04 - PALMARITO");
            voter.createCell(6).setCellValue(2);

            sheet.createRow(5).createCell(0).setCellValue(2);
            file = bytes(workbook);
        }

        StepVerifier.create(reader.read(file))
                .assertNext(data -> {
                    assertThat(data.rows()).hasSize(1);
                    assertThat(data.skippedRows()).isEqualTo(1);
                    assertThat(data.rows().getFirst().identification()).isEqualTo("23324434343");
                    assertThat(data.rows().getFirst().votingZoneName()).isEqualTo("ZONA 99");
                    assertThat(data.rows().getFirst().pollingPlaceName()).isEqualTo("04 - PALMARITO");
                    assertThat(data.rows().getFirst().tableNumber()).isEqualTo("2");
                    assertThat(data.rows().getFirst().rowNumber()).isEqualTo(5);
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectInvalidAndEmptyFiles() {
        StepVerifier.create(reader.read("not an xlsx".getBytes()))
                .expectError(ValidationException.class).verify();
        StepVerifier.create(reader.read(new byte[0]))
                .expectError(ValidationException.class).verify();
    }

    private XSSFWorkbook workbookWithHeaders(String... headers) {
        var workbook = new XSSFWorkbook();
        var row = workbook.createSheet("Votantes").createRow(0);
        for (int index = 0; index < headers.length; index++) {
            row.createCell(index).setCellValue(headers[index]);
        }
        return workbook;
    }

    private byte[] bytes(XSSFWorkbook workbook) throws Exception {
        var output = new ByteArrayOutputStream();
        workbook.write(output);
        return output.toByteArray();
    }
}
