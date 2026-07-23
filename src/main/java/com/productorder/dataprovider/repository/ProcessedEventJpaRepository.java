package com.productorder.dataprovider.repository;

import com.productorder.dataprovider.entity.ProcessedEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    @Modifying
    @Query(value = "insert into processed_events(event_id, event_type, processed_at) values (:eventId, :eventType, current_timestamp) on conflict (event_id) do nothing", nativeQuery = true)
    int register(@Param("eventId") UUID eventId, @Param("eventType") String eventType);
}
