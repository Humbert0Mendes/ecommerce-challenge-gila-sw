package com.productorder.shared.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resource) { super(resource + " não encontrado"); }
}
