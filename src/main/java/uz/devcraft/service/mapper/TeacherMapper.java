package uz.devcraft.service.mapper;

import org.mapstruct.*;
import uz.devcraft.domain.Spec;
import uz.devcraft.domain.Staff;
import uz.devcraft.domain.Teacher;
import uz.devcraft.service.dto.SpecDTO;
import uz.devcraft.service.dto.StaffDTO;
import uz.devcraft.service.dto.TeacherDTO;

/**
 * Mapper for the entity {@link Teacher} and its DTO {@link TeacherDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeacherMapper extends EntityMapper<TeacherDTO, Teacher> {
    @Mapping(target = "staff", source = "staff", qualifiedByName = "staffId")
    @Mapping(target = "spec", source = "spec", qualifiedByName = "specId")
    TeacherDTO toDto(Teacher s);

    @Named("staffId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    StaffDTO toDtoStaffId(Staff staff);

    @Named("specId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SpecDTO toDtoSpecId(Spec spec);
}
