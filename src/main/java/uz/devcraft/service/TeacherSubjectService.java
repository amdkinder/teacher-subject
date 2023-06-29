package uz.devcraft.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devcraft.domain.TeacherSubject;
import uz.devcraft.repository.TeacherSubjectRepository;
import uz.devcraft.service.dto.TeacherSubjectDTO;
import uz.devcraft.service.mapper.TeacherSubjectMapper;

/**
 * Service Implementation for managing {@link TeacherSubject}.
 */
@Service
@Transactional
public class TeacherSubjectService {

    private final Logger log = LoggerFactory.getLogger(TeacherSubjectService.class);

    private final TeacherSubjectRepository teacherSubjectRepository;

    private final TeacherSubjectMapper teacherSubjectMapper;

    public TeacherSubjectService(TeacherSubjectRepository teacherSubjectRepository, TeacherSubjectMapper teacherSubjectMapper) {
        this.teacherSubjectRepository = teacherSubjectRepository;
        this.teacherSubjectMapper = teacherSubjectMapper;
    }

    /**
     * Save a teacherSubject.
     *
     * @param teacherSubjectDTO the entity to save.
     * @return the persisted entity.
     */
    public TeacherSubjectDTO save(TeacherSubjectDTO teacherSubjectDTO) {
        log.debug("Request to save TeacherSubject : {}", teacherSubjectDTO);
        TeacherSubject teacherSubject = teacherSubjectMapper.toEntity(teacherSubjectDTO);
        teacherSubject = teacherSubjectRepository.save(teacherSubject);
        return teacherSubjectMapper.toDto(teacherSubject);
    }

    /**
     * Update a teacherSubject.
     *
     * @param teacherSubjectDTO the entity to save.
     * @return the persisted entity.
     */
    public TeacherSubjectDTO update(TeacherSubjectDTO teacherSubjectDTO) {
        log.debug("Request to update TeacherSubject : {}", teacherSubjectDTO);
        TeacherSubject teacherSubject = teacherSubjectMapper.toEntity(teacherSubjectDTO);
        teacherSubject = teacherSubjectRepository.save(teacherSubject);
        return teacherSubjectMapper.toDto(teacherSubject);
    }

    /**
     * Partially update a teacherSubject.
     *
     * @param teacherSubjectDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TeacherSubjectDTO> partialUpdate(TeacherSubjectDTO teacherSubjectDTO) {
        log.debug("Request to partially update TeacherSubject : {}", teacherSubjectDTO);

        return teacherSubjectRepository
            .findById(teacherSubjectDTO.getId())
            .map(existingTeacherSubject -> {
                teacherSubjectMapper.partialUpdate(existingTeacherSubject, teacherSubjectDTO);

                return existingTeacherSubject;
            })
            .map(teacherSubjectRepository::save)
            .map(teacherSubjectMapper::toDto);
    }

    /**
     * Get all the teacherSubjects.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TeacherSubjectDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TeacherSubjects");
        return teacherSubjectRepository.findAll(pageable).map(teacherSubjectMapper::toDto);
    }

    /**
     * Get one teacherSubject by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeacherSubjectDTO> findOne(Long id) {
        log.debug("Request to get TeacherSubject : {}", id);
        return teacherSubjectRepository.findById(id).map(teacherSubjectMapper::toDto);
    }

    /**
     * Delete the teacherSubject by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TeacherSubject : {}", id);
        teacherSubjectRepository.deleteById(id);
    }

    public Integer calculateTeacherHours(Long teacherId) {
        var result = teacherSubjectRepository.sumTeacherSubjectHours(teacherId);
        return result;
    }
}
