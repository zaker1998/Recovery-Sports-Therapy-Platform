package com.recovery.platform.appointments.controller;

import com.recovery.platform.appointments.dto.AppointmentDto;
import com.recovery.platform.appointments.dto.CreateAppointmentRequest;
import com.recovery.platform.appointments.dto.UpdateAppointmentStatusRequest;
import com.recovery.platform.appointments.entity.AppointmentStatus;
import com.recovery.platform.appointments.service.AppointmentService;
import com.recovery.platform.common.web.PageResponse;
import com.recovery.platform.security.annotation.CurrentUser;
import com.recovery.platform.security.userdetails.AppUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Scheduling and status tracking for therapy bookings")
@PreAuthorize("isAuthenticated()")
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ATHLETE','THERAPIST','COACH','ADMIN')")
    @Operation(summary = "Create an appointment")
    public ResponseEntity<AppointmentDto> create(@CurrentUser AppUserDetails me,
                                                 @Valid @RequestBody CreateAppointmentRequest req) {
        AppointmentDto created = service.create(me.getId(), me.getRole(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List appointments with role-aware scoping")
    public PageResponse<AppointmentDto> list(
            @CurrentUser AppUserDetails me,
            @RequestParam(required = false) UUID athleteId,
            @RequestParam(required = false) UUID therapistId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.list(me.getId(), me.getRole(), athleteId, therapistId, status, from, to, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one appointment")
    public AppointmentDto getById(@CurrentUser AppUserDetails me, @PathVariable UUID id) {
        return service.getById(me.getId(), me.getRole(), id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status")
    public AppointmentDto updateStatus(@CurrentUser AppUserDetails me,
                                       @PathVariable UUID id,
                                       @Valid @RequestBody UpdateAppointmentStatusRequest req) {
        return service.updateStatus(me.getId(), me.getRole(), id, req);
    }
}
