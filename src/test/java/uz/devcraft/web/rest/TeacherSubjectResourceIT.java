package uz.devcraft.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devcraft.IntegrationTest;
import uz.devcraft.domain.Subject;
import uz.devcraft.domain.Teacher;
import uz.devcraft.domain.TeacherSubject;
import uz.devcraft.repository.TeacherSubjectRepository;
import uz.devcraft.service.criteria.TeacherSubjectCriteria;
import uz.devcraft.service.dto.TeacherSubjectDTO;
import uz.devcraft.service.mapper.TeacherSubjectMapper;

/**
 * Integration tests for the {@link TeacherSubjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TeacherSubjectResourceIT {

    private static final String ENTITY_API_URL = "/api/teacher-subjects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeacherSubjectRepository teacherSubjectRepository;

    @Autowired
    private TeacherSubjectMapper teacherSubjectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeacherSubjectMockMvc;

    private TeacherSubject teacherSubject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeacherSubject createEntity(EntityManager em) {
        TeacherSubject teacherSubject = new TeacherSubject();
        return teacherSubject;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeacherSubject createUpdatedEntity(EntityManager em) {
        TeacherSubject teacherSubject = new TeacherSubject();
        return teacherSubject;
    }

    @BeforeEach
    public void initTest() {
        teacherSubject = createEntity(em);
    }

    @Test
    @Transactional
    void createTeacherSubject() throws Exception {
        int databaseSizeBeforeCreate = teacherSubjectRepository.findAll().size();
        // Create the TeacherSubject
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);
        restTeacherSubjectMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeCreate + 1);
        TeacherSubject testTeacherSubject = teacherSubjectList.get(teacherSubjectList.size() - 1);
    }

    @Test
    @Transactional
    void createTeacherSubjectWithExistingId() throws Exception {
        // Create the TeacherSubject with an existing ID
        teacherSubject.setId(1L);
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);

        int databaseSizeBeforeCreate = teacherSubjectRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeacherSubjectMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTeacherSubjects() throws Exception {
        // Initialize the database
        teacherSubjectRepository.saveAndFlush(teacherSubject);

        // Get all the teacherSubjectList
        restTeacherSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacherSubject.getId().intValue())));
    }

    @Test
    @Transactional
    void getTeacherSubject() throws Exception {
        // Initialize the database
        teacherSubjectRepository.saveAndFlush(teacherSubject);

        // Get the teacherSubject
        restTeacherSubjectMockMvc
            .perform(get(ENTITY_API_URL_ID, teacherSubject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teacherSubject.getId().intValue()));
    }

    @Test
    @Transactional
    void getTeacherSubjectsByIdFiltering() throws Exception {
        // Initialize the database
        teacherSubjectRepository.saveAndFlush(teacherSubject);

        Long id = teacherSubject.getId();

        defaultTeacherSubjectShouldBeFound("id.equals=" + id);
        defaultTeacherSubjectShouldNotBeFound("id.notEquals=" + id);

        defaultTeacherSubjectShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTeacherSubjectShouldNotBeFound("id.greaterThan=" + id);

        defaultTeacherSubjectShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTeacherSubjectShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeacherSubjectsByTeacherIsEqualToSomething() throws Exception {
        Teacher teacher;
        if (TestUtil.findAll(em, Teacher.class).isEmpty()) {
            teacherSubjectRepository.saveAndFlush(teacherSubject);
            teacher = TeacherResourceIT.createEntity(em);
        } else {
            teacher = TestUtil.findAll(em, Teacher.class).get(0);
        }
        em.persist(teacher);
        em.flush();
        teacherSubject.setTeacher(teacher);
        teacherSubjectRepository.saveAndFlush(teacherSubject);
        Long teacherId = teacher.getId();
        // Get all the teacherSubjectList where teacher equals to teacherId
        defaultTeacherSubjectShouldBeFound("teacherId.equals=" + teacherId);

        // Get all the teacherSubjectList where teacher equals to (teacherId + 1)
        defaultTeacherSubjectShouldNotBeFound("teacherId.equals=" + (teacherId + 1));
    }

    @Test
    @Transactional
    void getAllTeacherSubjectsBySubjectIsEqualToSomething() throws Exception {
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            teacherSubjectRepository.saveAndFlush(teacherSubject);
            subject = SubjectResourceIT.createEntity(em);
        } else {
            subject = TestUtil.findAll(em, Subject.class).get(0);
        }
        em.persist(subject);
        em.flush();
        teacherSubject.setSubject(subject);
        teacherSubjectRepository.saveAndFlush(teacherSubject);
        Long subjectId = subject.getId();
        // Get all the teacherSubjectList where subject equals to subjectId
        defaultTeacherSubjectShouldBeFound("subjectId.equals=" + subjectId);

        // Get all the teacherSubjectList where subject equals to (subjectId + 1)
        defaultTeacherSubjectShouldNotBeFound("subjectId.equals=" + (subjectId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeacherSubjectShouldBeFound(String filter) throws Exception {
        restTeacherSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacherSubject.getId().intValue())));

        // Check, that the count call also returns 1
        restTeacherSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeacherSubjectShouldNotBeFound(String filter) throws Exception {
        restTeacherSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeacherSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeacherSubject() throws Exception {
        // Get the teacherSubject
        restTeacherSubjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeacherSubject() throws Exception {
        // Initialize the database
        teacherSubjectRepository.saveAndFlush(teacherSubject);

        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();

        // Update the teacherSubject
        TeacherSubject updatedTeacherSubject = teacherSubjectRepository.findById(teacherSubject.getId()).get();
        // Disconnect from session so that the updates on updatedTeacherSubject are not directly saved in db
        em.detach(updatedTeacherSubject);
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(updatedTeacherSubject);

        restTeacherSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherSubjectDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isOk());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
        TeacherSubject testTeacherSubject = teacherSubjectList.get(teacherSubjectList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingTeacherSubject() throws Exception {
        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();
        teacherSubject.setId(count.incrementAndGet());

        // Create the TeacherSubject
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherSubjectDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeacherSubject() throws Exception {
        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();
        teacherSubject.setId(count.incrementAndGet());

        // Create the TeacherSubject
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeacherSubject() throws Exception {
        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();
        teacherSubject.setId(count.incrementAndGet());

        // Create the TeacherSubject
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherSubjectMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeacherSubjectWithPatch() throws Exception {
        // Initialize the database
        teacherSubjectRepository.saveAndFlush(teacherSubject);

        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();

        // Update the teacherSubject using partial update
        TeacherSubject partialUpdatedTeacherSubject = new TeacherSubject();
        partialUpdatedTeacherSubject.setId(teacherSubject.getId());

        restTeacherSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacherSubject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeacherSubject))
            )
            .andExpect(status().isOk());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
        TeacherSubject testTeacherSubject = teacherSubjectList.get(teacherSubjectList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateTeacherSubjectWithPatch() throws Exception {
        // Initialize the database
        teacherSubjectRepository.saveAndFlush(teacherSubject);

        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();

        // Update the teacherSubject using partial update
        TeacherSubject partialUpdatedTeacherSubject = new TeacherSubject();
        partialUpdatedTeacherSubject.setId(teacherSubject.getId());

        restTeacherSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacherSubject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeacherSubject))
            )
            .andExpect(status().isOk());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
        TeacherSubject testTeacherSubject = teacherSubjectList.get(teacherSubjectList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingTeacherSubject() throws Exception {
        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();
        teacherSubject.setId(count.incrementAndGet());

        // Create the TeacherSubject
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teacherSubjectDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeacherSubject() throws Exception {
        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();
        teacherSubject.setId(count.incrementAndGet());

        // Create the TeacherSubject
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeacherSubject() throws Exception {
        int databaseSizeBeforeUpdate = teacherSubjectRepository.findAll().size();
        teacherSubject.setId(count.incrementAndGet());

        // Create the TeacherSubject
        TeacherSubjectDTO teacherSubjectDTO = teacherSubjectMapper.toDto(teacherSubject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teacherSubjectDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeacherSubject in the database
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeacherSubject() throws Exception {
        // Initialize the database
        teacherSubjectRepository.saveAndFlush(teacherSubject);

        int databaseSizeBeforeDelete = teacherSubjectRepository.findAll().size();

        // Delete the teacherSubject
        restTeacherSubjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, teacherSubject.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TeacherSubject> teacherSubjectList = teacherSubjectRepository.findAll();
        assertThat(teacherSubjectList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
