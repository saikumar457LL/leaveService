package org.ocean.leaveservice.mappers.admin;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.entity.LeaveBalances;
import org.ocean.leaveservice.mappers.GenericMapper;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,uses = AdminLeaveTypeMapper.class)
public interface AdminLeaveBalanceMapper extends GenericMapper<LeaveBalances, AdminLeaveBalanceResponseDto> {

    AdminLeaveBalanceResponseDto toDto(LeaveBalances entity);
}
