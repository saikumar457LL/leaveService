package org.ocean.leaveservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ocean.leaveservice.dao.LeaveTypeDto;
import org.ocean.leaveservice.entity.LeaveType;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LeaveTypeMapper extends GenericMapper<LeaveType,LeaveTypeDto> {

    @Mapping(source = "code",target = "name")
    @Override
    LeaveTypeDto toDto(LeaveType entity);

    @Mapping(source = "name",target = "code")
    @Override
    LeaveType toEntity(LeaveTypeDto dto);
}
