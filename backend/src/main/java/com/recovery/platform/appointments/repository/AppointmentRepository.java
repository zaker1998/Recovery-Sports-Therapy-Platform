package com.recovery.platform.appointments.repository;

import com.recovery.platform.appointments.entity.Appointment;
import com.recovery.platform.appointments.entity.AppointmentStatus;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("""
           SELECT a
             FROM Appointment a
            WHERE (:athleteId IS NULL OR a.athlete.id = :athleteId)
              AND (:therapistId IS NULL OR a.therapist.id = :therapistId)
              AND (:status IS NULL OR a.status = :status)
              AND (:fromAt IS NULL OR a.scheduledAt >= :fromAt)
              AND (:toAt IS NULL OR a.scheduledAt <= :toAt)
            ORDER BY a.scheduledAt ASC
           """)
    Page<Appointment> search(
            @Param("athleteId") UUID athleteId,
            @Param("therapistId") UUID therapistId,
            @Param("status") AppointmentStatus status,
            @Param("fromAt") Instant fromAt,
            @Param("toAt") Instant toAt,
            Pageable pageable
    );
}
