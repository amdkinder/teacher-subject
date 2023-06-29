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
import uz.devcraft.domain.TeacherSubject;
import uz.devcraft.repository.TeacherSubjectRepository;
import uz.devcraft.service.criteria.TeacherSubjectCriteria;
import uz.devcraft.service.dto.TeacherSubjectDTO;
import uz.devcraft.service.mapper.TeacherSubjectMapper;

/**
 * Service for executing complex queries for {@link TeacherSubject} entities in the database.
 * The main input is a {@link TeacherSubjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TeacherSubjectDTO} or a {@link Page} of {@link TeacherSubjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeacherSubjectQueryService extends QueryService<TeacherSubject> {

    private final Logger log = LoggerFactory.getLogger(TeacherSubjectQueryService.class);

    private final TeacherSubjectRepository teacherSubjectRepository;

    private final TeacherSubjectMapper teacherSubjectMapper;

    public TeacherSubjectQueryService(TeacherSubjectRepository teacherSubjectRepository, TeacherSubjectMapper teacherSubjectMapper) {
        this.teacherSubjectRepository = teacherSubjectRepository;
        this.teacherSubjectMapper = teacherSubjectMapper;
    }

    /**
     * Return a {@link List} of {@link TeacherSubjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TeacherSubjectDTO> findByCriteria(TeacherSubjectCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TeacherSubject> specification = createSpecification(criteria);
        return teacherSubjectMapper.toDto(teacherSubjectRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TeacherSubjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TeacherSubjectDTO> findByCriteria(TeacherSubjectCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TeacherSubject> specification = createSpecification(criteria);
        return teacherSubjectRepository.findAll(specification, page).map(teacherSubjectMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeacherSubjectCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TeacherSubject> specification = createSpecification(criteria);
        return teacherSubjectRepository.count(specification);
    }

    /**
     * Function to convert {@link TeacherSubjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TeacherSubject> createSpecification(TeacherSubjectCriteria criteria) {
        Specification<TeacherSubject> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TeacherSubject_.id));
            }
            if (criteria.getTeacherId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTeacherId(),
                            root -> root.join(TeacherSubject_.teacher, JoinType.LEFT).get(Teacher_.id)
                        )
                    );
            }
            if (criteria.getSubjectId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSubjectId(),
                            root -> root.join(TeacherSubject_.subject, JoinType.LEFT).get(Subject_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
