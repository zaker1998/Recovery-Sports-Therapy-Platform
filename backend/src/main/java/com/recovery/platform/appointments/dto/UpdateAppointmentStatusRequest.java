package com.recovery.platform.appointments.dto;

import com.recovery.platform.appointments.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAppointmentStatusRequest(
        @NotNull AppointmentStatus status,
        @Size(max = 200) String cancelledReason
) {}
