package uz.devcraft.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devcraft.domain.RestResult;
import uz.devcraft.repository.TeacherSubjectRepository;
import uz.devcraft.service.TeacherSubjectQueryService;
import uz.devcraft.service.TeacherSubjectService;
import uz.devcraft.service.criteria.TeacherSubjectCriteria;
import uz.devcraft.service.dto.TeacherSubjectDTO;
import uz.devcraft.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devcraft.domain.TeacherSubject}.
 */
@RestController
@RequestMapping("/api")
public class TeacherSubjectResource {

    private final Logger log = LoggerFactory.getLogger(TeacherSubjectResource.class);

    private static final String ENTITY_NAME = "teacherSubject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeacherSubjectService teacherSubjectService;

    private final TeacherSubjectRepository teacherSubjectRepository;

    private final TeacherSubjectQueryService teacherSubjectQueryService;

    public TeacherSubjectResource(
        TeacherSubjectService teacherSubjectService,
        TeacherSubjectRepository teacherSubjectRepository,
        TeacherSubjectQueryService teacherSubjectQueryService
    ) {
        this.teacherSubjectService = teacherSubjectService;
        this.teacherSubjectRepository = teacherSubjectRepository;
        this.teacherSubjectQueryService = teacherSubjectQueryService;
    }

    /**
     * {@code POST  /teacher-subjects} : Create a new teacherSubject.
     *
     * @param teacherSubjectDTO the teacherSubjectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teacherSubjectDTO, or with status {@code 400 (Bad Request)} if the teacherSubject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/teacher-subjects")
    public ResponseEntity<TeacherSubjectDTO> createTeacherSubject(@RequestBody TeacherSubjectDTO teacherSubjectDTO)
        throws URISyntaxException {
        log.debug("REST request to save TeacherSubject : {}", teacherSubjectDTO);
        if (teacherSubjectDTO.getId() != null) {
            throw new BadRequestAlertException("A new teacherSubject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeacherSubjectDTO result = teacherSubjectService.save(teacherSubjectDTO);
        return ResponseEntity
            .created(new URI("/api/teacher-subjects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /teacher-subjects/:id} : Updates an existing teacherSubject.
     *
     * @param id the id of the teacherSubjectDTO to save.
     * @param teacherSubjectDTO the teacherSubjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherSubjectDTO,
     * or with status {@code 400 (Bad Request)} if the teacherSubjectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teacherSubjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/teacher-subjects/{id}")
    public ResponseEntity<TeacherSubjectDTO> updateTeacherSubject(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeacherSubjectDTO teacherSubjectDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TeacherSubject : {}, {}", id, teacherSubjectDTO);
        if (teacherSubjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherSubjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherSubjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TeacherSubjectDTO result = teacherSubjectService.update(teacherSubjectDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherSubjectDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /teacher-subjects/:id} : Partial updates given fields of an existing teacherSubject, field will ignore if it is null
     *
     * @param id the id of the teacherSubjectDTO to save.
     * @param teacherSubjectDTO the teacherSubjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherSubjectDTO,
     * or with status {@code 400 (Bad Request)} if the teacherSubjectDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teacherSubjectDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teacherSubjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/teacher-subjects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeacherSubjectDTO> partialUpdateTeacherSubject(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeacherSubjectDTO teacherSubjectDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TeacherSubject partially : {}, {}", id, teacherSubjectDTO);
        if (teacherSubjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherSubjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherSubjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeacherSubjectDTO> result = teacherSubjectService.partialUpdate(teacherSubjectDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherSubjectDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /teacher-subjects} : get all the teacherSubjects.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teacherSubjects in body.
     */
    @GetMapping("/teacher-subjects")
    public ResponseEntity<List<TeacherSubjectDTO>> getAllTeacherSubjects(
        TeacherSubjectCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TeacherSubjects by criteria: {}", criteria);

        Page<TeacherSubjectDTO> page = teacherSubjectQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /teacher-subjects/count} : count all the teacherSubjects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/teacher-subjects/count")
    public ResponseEntity<Long> countTeacherSubjects(TeacherSubjectCriteria criteria) {
        log.debug("REST request to count TeacherSubjects by criteria: {}", criteria);
        return ResponseEntity.ok().body(teacherSubjectQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /teacher-subjects/:id} : get the "id" teacherSubject.
     *
     * @param id the id of the teacherSubjectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacherSubjectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/teacher-subjects/{id}")
    public ResponseEntity<TeacherSubjectDTO> getTeacherSubject(@PathVariable Long id) {
        log.debug("REST request to get TeacherSubject : {}", id);
        Optional<TeacherSubjectDTO> teacherSubjectDTO = teacherSubjectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teacherSubjectDTO);
    }

    /**
     * {@code DELETE  /teacher-subjects/:id} : delete the "id" teacherSubject.
     *
     * @param id the id of the teacherSubjectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/teacher-subjects/{id}")
    public ResponseEntity<Void> deleteTeacherSubject(@PathVariable Long id) {
        log.debug("REST request to delete TeacherSubject : {}", id);
        teacherSubjectService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }



}
