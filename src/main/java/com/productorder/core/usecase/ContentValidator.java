package com.productorder.core.usecase;

import com.productorder.core.exception.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class ContentValidator {
    private static final Pattern DANGEROUS = Pattern.compile("(?i)(<[^>]+>|&[a-z]+;|--|;\\s*(drop|delete|insert|update|select)\\b|\\bunion\\s+select\\b)");
    private static final Logger log = LoggerFactory.getLogger(ContentValidator.class);

    public void validate(String value) {
        if (value != null && DANGEROUS.matcher(value).find()){
            log.error("DANGEROUS content validation error");
            throw new BusinessRuleException("Text contains dangerous characters");
        }

    }
}
