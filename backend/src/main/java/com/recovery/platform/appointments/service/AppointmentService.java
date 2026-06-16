package com.recovery.platform.appointments.service;

import com.recovery.platform.appointments.dto.AppointmentDto;
import com.recovery.platform.appointments.dto.CreateAppointmentRequest;
import com.recovery.platform.appointments.dto.UpdateAppointmentStatusRequest;
import com.recovery.platform.appointments.entity.Appointment;
import com.recovery.platform.appointments.entity.AppointmentStatus;
import com.recovery.platform.appointments.mapper.AppointmentMapper;
import com.recovery.platform.appointments.repository.AppointmentRepository;
import com.recovery.platform.common.exception.ForbiddenException;
import com.recovery.platform.common.exception.ResourceNotFoundException;
import com.recovery.platform.common.web.PageResponse;
import com.recovery.platform.user.entity.User;
import com.recovery.platform.user.entity.UserRole;
import com.recovery.platform.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper mapper;

    @Transactional
    public AppointmentDto create(UUID callerId, UserRole callerRole, CreateAppointmentRequest req) {
        UUID athleteId = resolveAthleteId(callerId, callerRole, req.athleteId());
        UUID therapistId = resolveTherapistId(callerId, callerRole, req.therapistId());

        User athlete = userRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete", athleteId));
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist", therapistId));

        Appointment entity = Appointment.builder()
                .athlete(athlete)
                .therapist(therapist)
                .scheduledAt(req.scheduledAt())
                .durationMinutes(req.durationMinutes())
                .status(AppointmentStatus.SCHEDULED)
                .sessionType(req.sessionType())
                .location(req.location())
                .notes(req.notes())
                .build();
        return mapper.toDto(appointmentRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentDto> list(UUID callerId, UserRole callerRole,
                                             UUID athleteId, UUID therapistId,
                                             AppointmentStatus status, Instant fromAt,
                                             Instant toAt, Pageable pageable) {
        UUID scopedAthleteId = athleteId;
        UUID scopedTherapistId = therapistId;

        switch (callerRole) {
            case ATHLETE -> {
                if (athleteId != null && !callerId.equals(athleteId)) {
                    throw new ForbiddenException("Athletes may only list their own appointments");
                }
                scopedAthleteId = callerId;
            }
            case THERAPIST, COACH -> {
                if (therapistId != null && !callerId.equals(therapistId)) {
                    throw new ForbiddenException("Staff may only list appointments assigned to themselves");
                }
                scopedTherapistId = callerId;
            }
            case ADMIN -> {
                // unrestricted
            }
        }

        var page = appointmentRepository.search(
                scopedAthleteId,
                scopedTherapistId,
                status,
                fromAt,
                toAt,
                pageable
        );
        return PageResponse.from(page, mapper::toDto);
    }

    @Transactional(readOnly = true)
    public AppointmentDto getById(UUID callerId, UserRole callerRole, UUID appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        requireAccess(callerId, callerRole, appt);
        return mapper.toDto(appt);
    }

    @Transactional
    public AppointmentDto updateStatus(UUID callerId, UserRole callerRole, UUID appointmentId,
                                       UpdateAppointmentStatusRequest req) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        requireAccess(callerId, callerRole, appt);

        if (callerRole == UserRole.ATHLETE
                && req.status() != AppointmentStatus.CANCELLED
                && req.status() != AppointmentStatus.CONFIRMED) {
            throw new ForbiddenException("Athletes may only confirm or cancel their appointments");
        }

        appt.setStatus(req.status());
        if (req.status() == AppointmentStatus.CANCELLED) {
            appt.setCancelledAt(Instant.now());
            appt.setCancelledReason(req.cancelledReason());
        } else {
            appt.setCancelledAt(null);
            appt.setCancelledReason(null);
        }
        return mapper.toDto(appointmentRepository.save(appt));
    }

    private void requireAccess(UUID callerId, UserRole role, Appointment appt) {
        if (role == UserRole.ADMIN) return;
        if (role == UserRole.ATHLETE && callerId.equals(appt.getAthlete().getId())) return;
        if ((role == UserRole.THERAPIST || role == UserRole.COACH)
                && callerId.equals(appt.getTherapist().getId())) return;
        throw new ForbiddenException("Not allowed to access this appointment");
    }

    private UUID resolveAthleteId(UUID callerId, UserRole role, UUID requestedAthleteId) {
        return switch (role) {
            case ATHLETE -> {
                if (requestedAthleteId != null && !callerId.equals(requestedAthleteId)) {
                    throw new ForbiddenException("Athletes may only create their own appointments");
                }
                yield callerId;
            }
            case THERAPIST, COACH -> requestedAthleteId != null ? requestedAthleteId : callerId;
            case ADMIN -> requestedAthleteId != null ? requestedAthleteId : callerId;
        };
    }

    private UUID resolveTherapistId(UUID callerId, UserRole role, UUID requestedTherapistId) {
        return switch (role) {
            case ATHLETE -> requestedTherapistId != null ? requestedTherapistId : callerId;
            case THERAPIST, COACH -> {
                if (requestedTherapistId != null && !callerId.equals(requestedTherapistId)) {
                    throw new ForbiddenException("Staff may only create appointments for themselves");
                }
                yield callerId;
            }
            case ADMIN -> requestedTherapistId != null ? requestedTherapistId : callerId;
        };
    }
}
