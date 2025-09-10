package org.ocean.leaveservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.entity.LeaveRequest;
import org.ocean.leaveservice.entity.LeaveType;
import org.ocean.leaveservice.responses.LeaveStatus;

import java.util.UUID;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LeaveStatusMapper extends GenericMapper<LeaveRequest,LeaveStatus> {
    @Mapping(source = "leaveType",target = "leaveType",qualifiedByName = "format_leaveType")
    @Mapping(source = "status",target = "status",qualifiedByName = "format_leaveStatus")
    @Mapping(source = "uuid",target = "leaveId",qualifiedByName = "format_leaveUuid")
    @Override
    LeaveStatus toDto(LeaveRequest entity);

    @Mapping(target = "leaveType",ignore = true)
    @Mapping(target = "status",ignore = true)
    @Override
    LeaveRequest toEntity(LeaveStatus dto);

    @Named("format_leaveType")
    default String formatLeaveType(LeaveType leaveType) {
        return leaveType.getCode();
    }

    @Named("format_leaveStatus")
    default String formatLeaveType(org.ocean.leaveservice.constants.LeaveStatus leaveStatus) {
        return leaveStatus.toString();
    }

    @Named("format_leaveUuid")
    default String formatLeaveUuid(UUID uuid) {
        return uuid.toString();
    }
}
