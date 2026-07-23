package com.productorder.entrypoint.api.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.product.ProductImportIssue;
import com.productorder.core.domain.product.ProductImportResult;
import com.productorder.core.domain.product.ProductImportRow;
import com.productorder.core.usecase.product.ProductUseCase;
import com.productorder.dataprovider.csv.ProductCsvParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ProductImportFacadeTest {

    @Mock
    private ProductCsvParser parser;

    @Mock
    private ProductUseCase useCase;

    @Mock
    private MultipartFile file;

    private ProductImportFacade facade;

    @BeforeEach
    void setUp() {
        facade = new ProductImportFacade(parser, useCase);
    }

    @Test
    void shouldParseFileImportRowsAndMapImportReport() throws IOException {
        List<ProductImportRow> rows = List.of(new ProductImportRow(2, "Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", "99.90", "10", "0.120"));
        var result = new ProductImportResult(1, 1, List.of(new ProductImportIssue(3, "price", "invalid price")));

        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(parser.parse(org.mockito.ArgumentMatchers.any())).thenReturn(rows);
        when(useCase.importProducts(rows)).thenReturn(result);

        var response = facade.importFile(file);

        verify(useCase).importProducts(rows);
        assertThat(response.imported()).isEqualTo(1);
        assertThat(response.skipped()).isEqualTo(1);
        assertThat(response.errors()).singleElement()
                .satisfies(error -> {
                    assertThat(error.line()).isEqualTo(3);
                    assertThat(error.field()).isEqualTo("price");
                });
    }

    @Test
    void shouldRejectEmptyFile() {
        when(file.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> facade.importFile(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CSV file is empty");
    }
}
