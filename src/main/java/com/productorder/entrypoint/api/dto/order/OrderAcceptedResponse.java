package com.productorder.entrypoint.api.dto.order;

import com.productorder.core.domain.order.OrderStatusEnum;

public record OrderAcceptedResponse(Long orderId, OrderStatusEnum status) {}
