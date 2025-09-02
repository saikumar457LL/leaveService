package org.ocean.leaveservice.mappers.admin;

import org.mapstruct.*;
import org.ocean.leaveservice.dao.admin.AdminLeaveBalanceDto;
import org.ocean.leaveservice.entity.LeaveBalances;
import org.ocean.leaveservice.mappers.GenericMapper;
import org.ocean.leaveservice.utils.UserUtils;

import java.util.UUID;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,uses = AdminLeaveTypeMapper.class)
public interface AdminLeaveBalanceMapper extends GenericMapper<LeaveBalances, AdminLeaveBalanceDto> {

    @Mapping(source = "user", target = "username", qualifiedByName = "get_username")
    AdminLeaveBalanceDto toDto(LeaveBalances entity, @Context UserUtils userUtils);

    @Override
    @Mapping(target = "id", ignore = true)
    LeaveBalances toEntity(AdminLeaveBalanceDto dto);

    @Named("get_username")
    default String getUsername(UUID user, @Context UserUtils userUtils) {
        return userUtils.getUserName();
    }
}
