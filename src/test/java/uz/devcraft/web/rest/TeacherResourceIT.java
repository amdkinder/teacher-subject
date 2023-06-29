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
import uz.devcraft.domain.Spec;
import uz.devcraft.domain.Staff;
import uz.devcraft.domain.Teacher;
import uz.devcraft.domain.enumeration.AcademicRank;
import uz.devcraft.repository.TeacherRepository;
import uz.devcraft.service.criteria.TeacherCriteria;
import uz.devcraft.service.dto.TeacherDTO;
import uz.devcraft.service.mapper.TeacherMapper;

/**
 * Integration tests for the {@link TeacherResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TeacherResourceIT {

    private static final String DEFAULT_FULLN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULLN_NAME = "BBBBBBBBBB";

    private static final AcademicRank DEFAULT_RANK = AcademicRank.INTERN;
    private static final AcademicRank UPDATED_RANK = AcademicRank.ASSISTANT;

    private static final String ENTITY_API_URL = "/api/teachers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeacherMockMvc;

    private Teacher teacher;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teacher createEntity(EntityManager em) {
        Teacher teacher = new Teacher().fullnName(DEFAULT_FULLN_NAME).rank(DEFAULT_RANK);
        return teacher;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teacher createUpdatedEntity(EntityManager em) {
        Teacher teacher = new Teacher().fullnName(UPDATED_FULLN_NAME).rank(UPDATED_RANK);
        return teacher;
    }

    @BeforeEach
    public void initTest() {
        teacher = createEntity(em);
    }

    @Test
    @Transactional
    void createTeacher() throws Exception {
        int databaseSizeBeforeCreate = teacherRepository.findAll().size();
        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);
        restTeacherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isCreated());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeCreate + 1);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFullnName()).isEqualTo(DEFAULT_FULLN_NAME);
        assertThat(testTeacher.getRank()).isEqualTo(DEFAULT_RANK);
    }

    @Test
    @Transactional
    void createTeacherWithExistingId() throws Exception {
        // Create the Teacher with an existing ID
        teacher.setId(1L);
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        int databaseSizeBeforeCreate = teacherRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeacherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTeachers() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacher.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullnName").value(hasItem(DEFAULT_FULLN_NAME)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK.toString())));
    }

    @Test
    @Transactional
    void getTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get the teacher
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL_ID, teacher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teacher.getId().intValue()))
            .andExpect(jsonPath("$.fullnName").value(DEFAULT_FULLN_NAME))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK.toString()));
    }

    @Test
    @Transactional
    void getTeachersByIdFiltering() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        Long id = teacher.getId();

        defaultTeacherShouldBeFound("id.equals=" + id);
        defaultTeacherShouldNotBeFound("id.notEquals=" + id);

        defaultTeacherShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTeacherShouldNotBeFound("id.greaterThan=" + id);

        defaultTeacherShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTeacherShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeachersByFullnNameIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where fullnName equals to DEFAULT_FULLN_NAME
        defaultTeacherShouldBeFound("fullnName.equals=" + DEFAULT_FULLN_NAME);

        // Get all the teacherList where fullnName equals to UPDATED_FULLN_NAME
        defaultTeacherShouldNotBeFound("fullnName.equals=" + UPDATED_FULLN_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByFullnNameIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where fullnName in DEFAULT_FULLN_NAME or UPDATED_FULLN_NAME
        defaultTeacherShouldBeFound("fullnName.in=" + DEFAULT_FULLN_NAME + "," + UPDATED_FULLN_NAME);

        // Get all the teacherList where fullnName equals to UPDATED_FULLN_NAME
        defaultTeacherShouldNotBeFound("fullnName.in=" + UPDATED_FULLN_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByFullnNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where fullnName is not null
        defaultTeacherShouldBeFound("fullnName.specified=true");

        // Get all the teacherList where fullnName is null
        defaultTeacherShouldNotBeFound("fullnName.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByFullnNameContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where fullnName contains DEFAULT_FULLN_NAME
        defaultTeacherShouldBeFound("fullnName.contains=" + DEFAULT_FULLN_NAME);

        // Get all the teacherList where fullnName contains UPDATED_FULLN_NAME
        defaultTeacherShouldNotBeFound("fullnName.contains=" + UPDATED_FULLN_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByFullnNameNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where fullnName does not contain DEFAULT_FULLN_NAME
        defaultTeacherShouldNotBeFound("fullnName.doesNotContain=" + DEFAULT_FULLN_NAME);

        // Get all the teacherList where fullnName does not contain UPDATED_FULLN_NAME
        defaultTeacherShouldBeFound("fullnName.doesNotContain=" + UPDATED_FULLN_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByRankIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where rank equals to DEFAULT_RANK
        defaultTeacherShouldBeFound("rank.equals=" + DEFAULT_RANK);

        // Get all the teacherList where rank equals to UPDATED_RANK
        defaultTeacherShouldNotBeFound("rank.equals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    void getAllTeachersByRankIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where rank in DEFAULT_RANK or UPDATED_RANK
        defaultTeacherShouldBeFound("rank.in=" + DEFAULT_RANK + "," + UPDATED_RANK);

        // Get all the teacherList where rank equals to UPDATED_RANK
        defaultTeacherShouldNotBeFound("rank.in=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    void getAllTeachersByRankIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where rank is not null
        defaultTeacherShouldBeFound("rank.specified=true");

        // Get all the teacherList where rank is null
        defaultTeacherShouldNotBeFound("rank.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByStaffIsEqualToSomething() throws Exception {
        Staff staff;
        if (TestUtil.findAll(em, Staff.class).isEmpty()) {
            teacherRepository.saveAndFlush(teacher);
            staff = StaffResourceIT.createEntity(em);
        } else {
            staff = TestUtil.findAll(em, Staff.class).get(0);
        }
        em.persist(staff);
        em.flush();
        teacher.setStaff(staff);
        teacherRepository.saveAndFlush(teacher);
        Long staffId = staff.getId();
        // Get all the teacherList where staff equals to staffId
        defaultTeacherShouldBeFound("staffId.equals=" + staffId);

        // Get all the teacherList where staff equals to (staffId + 1)
        defaultTeacherShouldNotBeFound("staffId.equals=" + (staffId + 1));
    }

    @Test
    @Transactional
    void getAllTeachersBySpecIsEqualToSomething() throws Exception {
        Spec spec;
        if (TestUtil.findAll(em, Spec.class).isEmpty()) {
            teacherRepository.saveAndFlush(teacher);
            spec = SpecResourceIT.createEntity(em);
        } else {
            spec = TestUtil.findAll(em, Spec.class).get(0);
        }
        em.persist(spec);
        em.flush();
        teacher.setSpec(spec);
        teacherRepository.saveAndFlush(teacher);
        Long specId = spec.getId();
        // Get all the teacherList where spec equals to specId
        defaultTeacherShouldBeFound("specId.equals=" + specId);

        // Get all the teacherList where spec equals to (specId + 1)
        defaultTeacherShouldNotBeFound("specId.equals=" + (specId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeacherShouldBeFound(String filter) throws Exception {
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacher.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullnName").value(hasItem(DEFAULT_FULLN_NAME)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK.toString())));

        // Check, that the count call also returns 1
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeacherShouldNotBeFound(String filter) throws Exception {
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeacher() throws Exception {
        // Get the teacher
        restTeacherMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher
        Teacher updatedTeacher = teacherRepository.findById(teacher.getId()).get();
        // Disconnect from session so that the updates on updatedTeacher are not directly saved in db
        em.detach(updatedTeacher);
        updatedTeacher.fullnName(UPDATED_FULLN_NAME).rank(UPDATED_RANK);
        TeacherDTO teacherDTO = teacherMapper.toDto(updatedTeacher);

        restTeacherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isOk());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFullnName()).isEqualTo(UPDATED_FULLN_NAME);
        assertThat(testTeacher.getRank()).isEqualTo(UPDATED_RANK);
    }

    @Test
    @Transactional
    void putNonExistingTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeacherWithPatch() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher using partial update
        Teacher partialUpdatedTeacher = new Teacher();
        partialUpdatedTeacher.setId(teacher.getId());

        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacher.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeacher))
            )
            .andExpect(status().isOk());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFullnName()).isEqualTo(DEFAULT_FULLN_NAME);
        assertThat(testTeacher.getRank()).isEqualTo(DEFAULT_RANK);
    }

    @Test
    @Transactional
    void fullUpdateTeacherWithPatch() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher using partial update
        Teacher partialUpdatedTeacher = new Teacher();
        partialUpdatedTeacher.setId(teacher.getId());

        partialUpdatedTeacher.fullnName(UPDATED_FULLN_NAME).rank(UPDATED_RANK);

        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacher.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeacher))
            )
            .andExpect(status().isOk());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFullnName()).isEqualTo(UPDATED_FULLN_NAME);
        assertThat(testTeacher.getRank()).isEqualTo(UPDATED_RANK);
    }

    @Test
    @Transactional
    void patchNonExistingTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teacherDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeDelete = teacherRepository.findAll().size();

        // Delete the teacher
        restTeacherMockMvc
            .perform(delete(ENTITY_API_URL_ID, teacher.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
