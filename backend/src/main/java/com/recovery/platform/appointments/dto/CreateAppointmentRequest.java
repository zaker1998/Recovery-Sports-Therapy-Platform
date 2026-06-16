package com.recovery.platform.appointments.dto;

import com.recovery.platform.appointments.entity.AppointmentSessionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public record CreateAppointmentRequest(
        UUID athleteId,
        UUID therapistId,
        @NotNull Instant scheduledAt,
        @NotNull @Min(15) Integer durationMinutes,
        @NotNull AppointmentSessionType sessionType,
        @Size(max = 200) String location,
        @Size(max = 4000) String notes
) {}
