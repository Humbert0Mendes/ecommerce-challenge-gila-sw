package com.productorder.core.gateway;

public record PageQuery(int page, int size, String sort, String direction) {
}
