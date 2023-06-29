package uz.devcraft.service.mapper;

import org.mapstruct.*;
import uz.devcraft.domain.Spec;
import uz.devcraft.domain.Subject;
import uz.devcraft.service.dto.SpecDTO;
import uz.devcraft.service.dto.SubjectDTO;

/**
 * Mapper for the entity {@link Subject} and its DTO {@link SubjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper extends EntityMapper<SubjectDTO, Subject> {
    @Mapping(target = "spec", source = "spec", qualifiedByName = "specId")
    SubjectDTO toDto(Subject s);

    @Named("specId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SpecDTO toDtoSpecId(Spec spec);
}
