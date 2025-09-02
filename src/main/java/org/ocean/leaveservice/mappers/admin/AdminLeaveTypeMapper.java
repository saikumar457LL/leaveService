package org.ocean.leaveservice.mappers.admin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.dao.admin.AdminLeaveTypeDto;
import org.ocean.leaveservice.entity.LeaveType;
import org.ocean.leaveservice.mappers.GenericMapper;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdminLeaveTypeMapper extends GenericMapper<LeaveType, AdminLeaveTypeDto> {
    @Mapping(source = "code",target = "name")
    @Override
    AdminLeaveTypeDto toDto(LeaveType entity);

    @Mapping(source = "name",target = "code")
    @Mapping(target = "id",ignore = true)
    @Override
    LeaveType toEntity(AdminLeaveTypeDto dto);
}
