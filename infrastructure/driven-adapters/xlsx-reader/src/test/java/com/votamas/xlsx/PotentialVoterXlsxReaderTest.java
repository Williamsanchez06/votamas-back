package com.votamas.xlsx;

import com.votamas.model.exception.ValidationException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class PotentialVoterXlsxReaderTest {
    private final PotentialVoterXlsxReader reader = new PotentialVoterXlsxReader();

    @Test
    void shouldReadStringsNumbersFormulasAndSkipEmptyRows() throws Exception {
        byte[] file;
        try (var workbook = workbookWithHeaders("Identificación", " NOMBRES ", "apellidos", "barrio",
                "COMUNA", "lugar de votación", "mesa de votacion")) {
            var sheet = workbook.getSheetAt(0);
            var numeric = sheet.createRow(1);
            numeric.createCell(0).setCellValue(1090123456d);
            numeric.createCell(1).setCellValue("Ana");
            numeric.createCell(2).setCellValue("Pérez");
            numeric.createCell(3).setCellValue("Centro");
            numeric.createCell(4).setCellValue("Comuna 1");
            numeric.createCell(5).setCellValue("Colegio Central");
            numeric.createCell(6).setCellValue(12);

            sheet.createRow(2);
            var text = sheet.createRow(3);
            text.createCell(0, CellType.STRING).setCellValue("00123");
            text.createCell(1).setCellValue("Luis");
            text.createCell(2).setCellValue("Díaz");
            text.createCell(3).setCellValue("Norte");
            text.createCell(4).setCellValue("Comuna 2");
            text.createCell(5).setCellValue("Escuela Norte");
            text.createCell(6, CellType.STRING).setCellValue("7");
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
