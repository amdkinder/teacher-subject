package uz.devcraft.service;

import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import uz.devcraft.domain.*; // for static metamodels
import uz.devcraft.domain.Spec;
import uz.devcraft.repository.SpecRepository;
import uz.devcraft.service.criteria.SpecCriteria;
import uz.devcraft.service.dto.SpecDTO;
import uz.devcraft.service.mapper.SpecMapper;

/**
 * Service for executing complex queries for {@link Spec} entities in the database.
 * The main input is a {@link SpecCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SpecDTO} or a {@link Page} of {@link SpecDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SpecQueryService extends QueryService<Spec> {

    private final Logger log = LoggerFactory.getLogger(SpecQueryService.class);

    private final SpecRepository specRepository;

    private final SpecMapper specMapper;

    public SpecQueryService(SpecRepository specRepository, SpecMapper specMapper) {
        this.specRepository = specRepository;
        this.specMapper = specMapper;
    }

    /**
     * Return a {@link List} of {@link SpecDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SpecDTO> findByCriteria(SpecCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Spec> specification = createSpecification(criteria);
        return specMapper.toDto(specRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SpecDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SpecDTO> findByCriteria(SpecCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Spec> specification = createSpecification(criteria);
        return specRepository.findAll(specification, page).map(specMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SpecCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Spec> specification = createSpecification(criteria);
        return specRepository.count(specification);
    }

    /**
     * Function to convert {@link SpecCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Spec> createSpecification(SpecCriteria criteria) {
        Specification<Spec> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Spec_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Spec_.name));
            }
        }
        return specification;
    }
}
