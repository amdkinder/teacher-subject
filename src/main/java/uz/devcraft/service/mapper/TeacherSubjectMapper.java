package uz.devcraft.service.mapper;

import org.mapstruct.*;
import uz.devcraft.domain.Subject;
import uz.devcraft.domain.Teacher;
import uz.devcraft.domain.TeacherSubject;
import uz.devcraft.service.dto.SubjectDTO;
import uz.devcraft.service.dto.TeacherDTO;
import uz.devcraft.service.dto.TeacherSubjectDTO;

/**
 * Mapper for the entity {@link TeacherSubject} and its DTO {@link TeacherSubjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeacherSubjectMapper extends EntityMapper<TeacherSubjectDTO, TeacherSubject> {
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "teacherId")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectId")
    TeacherSubjectDTO toDto(TeacherSubject s);

    @Named("teacherId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    TeacherDTO toDtoTeacherId(Teacher teacher);

    @Named("subjectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "hours", source = "hours")
    SubjectDTO toDtoSubjectId(Subject subject);
}
