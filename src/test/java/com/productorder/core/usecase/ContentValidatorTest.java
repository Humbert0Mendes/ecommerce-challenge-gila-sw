package com.productorder.core.usecase;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.productorder.core.exception.BusinessRuleException;

import org.junit.jupiter.api.Test;

class ContentValidatorTest {

    private final ContentValidator validator = new ContentValidator();

    @Test
    void shouldAcceptSafeText() {
        assertThatCode(() -> validator.validate("Mouse sem fio para escritorio")).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectHtmlTags() {
        assertThatThrownBy(() -> validator.validate("<script>alert('xss')</script>"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Text contains dangerous characters");
    }

    @Test
    void shouldRejectSqlInjectionMarkers() {
        assertThatThrownBy(() -> validator.validate("Mouse; drop table products"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Text contains dangerous characters");
    }
}
