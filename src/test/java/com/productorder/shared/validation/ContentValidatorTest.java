package com.productorder.shared.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.productorder.shared.error.BusinessRuleException;
import org.junit.jupiter.api.Test;

class ContentValidatorTest {
    private final ContentValidator validator = new ContentValidator();
    @Test void acceptsOrdinaryText() { assertThatCode(() -> validator.validate("Caderno universitário")).doesNotThrowAnyException(); }
    @Test void rejectsHtml() { assertThatThrownBy(() -> validator.validate("<script>alert(1)</script>")).isInstanceOf(BusinessRuleException.class); }
    @Test void rejectsSqlMarker() { assertThatThrownBy(() -> validator.validate("x; DROP TABLE products")).isInstanceOf(BusinessRuleException.class); }
}
