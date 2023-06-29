package uz.devcraft.service.mapper;

import org.mapstruct.*;
import uz.devcraft.domain.Spec;
import uz.devcraft.service.dto.SpecDTO;

/**
 * Mapper for the entity {@link Spec} and its DTO {@link SpecDTO}.
 */
@Mapper(componentModel = "spring")
public interface SpecMapper extends EntityMapper<SpecDTO, Spec> {}
