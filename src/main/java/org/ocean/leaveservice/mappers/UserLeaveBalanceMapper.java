package org.ocean.leaveservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.entity.LeaveType;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;
import org.ocean.leaveservice.entity.LeaveBalances;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserLeaveBalanceMapper extends  GenericMapper<LeaveBalances, UserLeaveBalancesResponseDto> {

    @Mapping(source = "leaveType",target = "leaveType",qualifiedByName = "format_leave_type")
    @Override
    UserLeaveBalancesResponseDto toDto(LeaveBalances entity);

    @Mapping(target = "leaveType",ignore = true)
    @Override
    LeaveBalances toEntity(UserLeaveBalancesResponseDto dto);

    @Named("format_leave_type")
    default String formatLeaveType(LeaveType leaveType) {
        return leaveType.getCode();
    }
}
