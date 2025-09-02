package org.ocean.leaveservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.dao.LeaveBalancesDto;
import org.ocean.leaveservice.entity.LeaveBalances;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,uses = LeaveTypeMapper.class)
public interface LeaveBalanceMapper extends  GenericMapper<LeaveBalances, LeaveBalancesDto> {

    @Override
    LeaveBalancesDto toDto(LeaveBalances entity);

    @Override
    LeaveBalances toEntity(LeaveBalancesDto dto);
}
