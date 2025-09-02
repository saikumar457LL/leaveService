package org.ocean.leaveservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.dao.UserLeaveBalancesDto;
import org.ocean.leaveservice.entity.LeaveBalances;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,uses = LeaveTypeMapper.class)
public interface UserLeaveBalanceMapper extends  GenericMapper<LeaveBalances, UserLeaveBalancesDto> {

    @Override
    UserLeaveBalancesDto toDto(LeaveBalances entity);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "user",ignore = true)
    @Override
    LeaveBalances toEntity(UserLeaveBalancesDto dto);
}
