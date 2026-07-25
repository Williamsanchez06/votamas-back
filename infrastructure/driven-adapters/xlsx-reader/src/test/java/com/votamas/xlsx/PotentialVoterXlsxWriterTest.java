package com.votamas.xlsx;

import com.votamas.model.potentialvoter.PotentialVoterExportRow;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PotentialVoterXlsxWriterTest {
    private static final int HEADER_COLUMN_COUNT = 8;

    private final PotentialVoterXlsxWriter writer = new PotentialVoterXlsxWriter();

    @Test
    void shouldApplyBrandColorToEveryHeaderColumn() {
        var row = new PotentialVoterExportRow(
                "1090123456",
                "Ana",
                "Pérez",
                "Zona 1",
                "Colegio Central",
                12,
                LocalDate.of(2026, 7, 25),
                "Líder Uno"
        );

        StepVerifier.create(writer.write(List.of(row)))
                .assertNext(this::assertHeaderStyle)
                .verifyComplete();
    }

    private void assertHeaderStyle(byte[] content) {
        try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(content))) {
            var header = workbook.getSheetAt(0).getRow(0);
            assertThat(header.getPhysicalNumberOfCells()).isEqualTo(HEADER_COLUMN_COUNT);

            for (int index = 0; index < HEADER_COLUMN_COUNT; index++) {
                var style = header.getCell(index).getCellStyle();
                assertThat(style.getFillPattern()).isEqualTo(FillPatternType.SOLID_FOREGROUND);
                assertThat(style.getFillForegroundColorColor().getRGB())
                        .containsExactly((byte) 0xBD, (byte) 0x16, (byte) 0x22);
            }
        } catch (java.io.IOException exception) {
            throw new AssertionError("No fue posible leer el XLSX generado", exception);
        }
    }
}
