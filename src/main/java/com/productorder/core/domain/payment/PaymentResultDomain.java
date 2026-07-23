package com.productorder.core.domain.payment;

public record PaymentResultDomain(boolean approved, String transactionId, String failureReason) {

    public static PaymentResultDomain success() {
        return new PaymentResultDomain(true, "fake-approved", null);
    }

    public static PaymentResultDomain failure(String reason) {
        return new PaymentResultDomain(false, null, reason);
    }

    public String reason() {
        return failureReason;
    }
}
