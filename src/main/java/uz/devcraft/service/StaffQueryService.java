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
import uz.devcraft.domain.Staff;
import uz.devcraft.repository.StaffRepository;
import uz.devcraft.service.criteria.StaffCriteria;
import uz.devcraft.service.dto.StaffDTO;
import uz.devcraft.service.mapper.StaffMapper;

/**
 * Service for executing complex queries for {@link Staff} entities in the database.
 * The main input is a {@link StaffCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StaffDTO} or a {@link Page} of {@link StaffDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StaffQueryService extends QueryService<Staff> {

    private final Logger log = LoggerFactory.getLogger(StaffQueryService.class);

    private final StaffRepository staffRepository;

    private final StaffMapper staffMapper;

    public StaffQueryService(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    /**
     * Return a {@link List} of {@link StaffDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StaffDTO> findByCriteria(StaffCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffMapper.toDto(staffRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StaffDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StaffDTO> findByCriteria(StaffCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffRepository.findAll(specification, page).map(staffMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StaffCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffRepository.count(specification);
    }

    /**
     * Function to convert {@link StaffCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Staff> createSpecification(StaffCriteria criteria) {
        Specification<Staff> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Staff_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Staff_.name));
            }
            if (criteria.getFromHours() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFromHours(), Staff_.fromHours));
            }
            if (criteria.getToHours() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getToHours(), Staff_.toHours));
            }
        }
        return specification;
    }
}
