package com.productorder.dataprovider.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.productorder.core.domain.product.ProductImportRow;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

class ProductCsvParserTest {

    private final ProductCsvParser parser = new ProductCsvParser();

    @Test
    void shouldParseValidCsvAndIgnoreBlankLines() {
        String csv = """
                name,sku,description,category,price,stock,weight_kg
                Mouse,MOUSE-001,Mouse sem fio,Perifericos,99.90,10,0.120
                ,,,,,,
                """;

        List<ProductImportRow> rows = parser.parse(input(csv));

        assertThat(rows).containsExactly(new ProductImportRow(2, "Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", "99.90", "10", "0.120"));
    }

    @Test
    void shouldRejectCsvWithMissingHeader() {
        String csv = "name,sku\nMouse,MOUSE-001\n";

        assertThatThrownBy(() -> parser.parse(input(csv)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid CSV Header");
    }

    private ByteArrayInputStream input(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
