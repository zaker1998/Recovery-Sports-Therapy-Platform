package com.recovery.platform.appointments.mapper;

import com.recovery.platform.appointments.dto.AppointmentDto;
import com.recovery.platform.appointments.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "athleteId", source = "athlete.id")
    @Mapping(target = "therapistId", source = "therapist.id")
    AppointmentDto toDto(Appointment appointment);
}
