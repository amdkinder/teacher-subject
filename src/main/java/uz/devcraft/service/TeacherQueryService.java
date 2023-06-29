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
import uz.devcraft.domain.Teacher;
import uz.devcraft.repository.TeacherRepository;
import uz.devcraft.service.criteria.TeacherCriteria;
import uz.devcraft.service.dto.TeacherDTO;
import uz.devcraft.service.mapper.TeacherMapper;

/**
 * Service for executing complex queries for {@link Teacher} entities in the database.
 * The main input is a {@link TeacherCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TeacherDTO} or a {@link Page} of {@link TeacherDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeacherQueryService extends QueryService<Teacher> {

    private final Logger log = LoggerFactory.getLogger(TeacherQueryService.class);

    private final TeacherRepository teacherRepository;

    private final TeacherMapper teacherMapper;

    public TeacherQueryService(TeacherRepository teacherRepository, TeacherMapper teacherMapper) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    /**
     * Return a {@link List} of {@link TeacherDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TeacherDTO> findByCriteria(TeacherCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Teacher> specification = createSpecification(criteria);
        return teacherMapper.toDto(teacherRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TeacherDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TeacherDTO> findByCriteria(TeacherCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Teacher> specification = createSpecification(criteria);
        return teacherRepository.findAll(specification, page).map(teacherMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeacherCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Teacher> specification = createSpecification(criteria);
        return teacherRepository.count(specification);
    }

    /**
     * Function to convert {@link TeacherCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Teacher> createSpecification(TeacherCriteria criteria) {
        Specification<Teacher> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Teacher_.id));
            }
            if (criteria.getFullnName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullnName(), Teacher_.fullnName));
            }
            if (criteria.getRank() != null) {
                specification = specification.and(buildSpecification(criteria.getRank(), Teacher_.rank));
            }
            if (criteria.getStaffId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStaffId(), root -> root.join(Teacher_.staff, JoinType.LEFT).get(Staff_.id))
                    );
            }
            if (criteria.getSpecId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSpecId(), root -> root.join(Teacher_.spec, JoinType.LEFT).get(Spec_.id))
                    );
            }
        }
        return specification;
    }
}
