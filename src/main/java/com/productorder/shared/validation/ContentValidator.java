package com.productorder.shared.validation;

import com.productorder.shared.error.BusinessRuleException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ContentValidator {
    private static final Pattern DANGEROUS = Pattern.compile("(?i)(<[^>]+>|&[a-z]+;|--|;\\s*(drop|delete|insert|update|select)\\b|\\bunion\\s+select\\b)");
    public void validate(String value) {
        if (value != null && DANGEROUS.matcher(value).find()) throw new BusinessRuleException("Conteúdo textual não permitido");
    }
}
