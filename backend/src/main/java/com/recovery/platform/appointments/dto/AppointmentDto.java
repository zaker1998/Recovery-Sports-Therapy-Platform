package com.recovery.platform.appointments.dto;

import com.recovery.platform.appointments.entity.AppointmentSessionType;
import com.recovery.platform.appointments.entity.AppointmentStatus;
import java.time.Instant;
import java.util.UUID;

public record AppointmentDto(
        UUID id,
        UUID athleteId,
        UUID therapistId,
        Instant scheduledAt,
        Integer durationMinutes,
        AppointmentStatus status,
        AppointmentSessionType sessionType,
        String location,
        String notes,
        String recurrenceRule,
        UUID recurrenceParentId,
        Instant cancelledAt,
        String cancelledReason,
        Instant createdAt,
        Instant updatedAt
) {}
