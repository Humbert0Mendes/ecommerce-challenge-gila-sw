package com.productorder.core.gateway;

import java.util.UUID;

public interface ProcessedEventGateway {
    boolean register(UUID eventId, String eventType);
}
