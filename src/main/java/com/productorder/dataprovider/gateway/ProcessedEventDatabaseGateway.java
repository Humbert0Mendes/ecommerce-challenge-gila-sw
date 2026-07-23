package com.productorder.dataprovider.gateway;

import com.productorder.core.gateway.ProcessedEventGateway;
import com.productorder.dataprovider.repository.ProcessedEventJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProcessedEventDatabaseGateway implements ProcessedEventGateway {
    private final ProcessedEventJpaRepository repository;
    public ProcessedEventDatabaseGateway(ProcessedEventJpaRepository repository) { this.repository = repository; }
    public boolean register(UUID eventId, String eventType) { return repository.register(eventId, eventType) == 1; }
}
