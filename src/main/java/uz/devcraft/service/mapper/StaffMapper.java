package uz.devcraft.service.mapper;

import org.mapstruct.*;
import uz.devcraft.domain.Staff;
import uz.devcraft.service.dto.StaffDTO;

/**
 * Mapper for the entity {@link Staff} and its DTO {@link StaffDTO}.
 */
@Mapper(componentModel = "spring")
public interface StaffMapper extends EntityMapper<StaffDTO, Staff> {}
