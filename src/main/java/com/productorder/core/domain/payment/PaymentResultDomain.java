package com.productorder.core.domain.payment;

public record PaymentResultDomain(boolean approved, String reason) {

    public static PaymentResultDomain success() {
        return new PaymentResultDomain(true, null);
    }

    public static PaymentResultDomain failure(String reason) {
        return new PaymentResultDomain(false, reason);
    }
}
