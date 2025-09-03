package org.ocean.leaveservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.constants.Leave;
import org.ocean.leaveservice.constants.LeaveStatus;
import org.ocean.leaveservice.entity.LeaveRequest;
import org.ocean.leaveservice.entity.LeaveType;
import org.ocean.leaveservice.responses.UserLeaveApplyResponseDto;

import java.util.UUID;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserLeaveResponseMapper extends GenericMapper<LeaveRequest, UserLeaveApplyResponseDto> {

    @Mapping(source = "leaveType",target = "leaveType",qualifiedByName = "format_leave_type")
    @Mapping(source = "user",target = "requestId",qualifiedByName = "format_user_id")
    @Mapping(source = "status",target = "status",qualifiedByName = "format_leave_status")
    @Override
    UserLeaveApplyResponseDto toDto(LeaveRequest entity);

    @Mapping(target = "leaveType",ignore = true)
    @Override
    LeaveRequest toEntity(UserLeaveApplyResponseDto dto);

    @Named("format_leave_type")
    default String formatLeaveType(LeaveType leaveType) {
        return switch (leaveType.getCode()) {
            case "SL" -> Leave.SL.toString();
            case "CL" -> Leave.CL.toString();
            case "CML" -> Leave.CML.toString();
            case "ML"  -> Leave.ML.toString();
            default -> Leave.EL.toString();
        };
    }

    @Named("format_user_id")
    default String formatUserId(UUID user) {
        return user.toString();
    }

    @Named("format_leave_status")
    default String formatLeaveStatus(LeaveStatus leaveStatus) {
        return leaveStatus.toString();
    }
}
