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
import uz.devcraft.domain.Subject;
import uz.devcraft.repository.SubjectRepository;
import uz.devcraft.service.criteria.SubjectCriteria;
import uz.devcraft.service.dto.SubjectDTO;
import uz.devcraft.service.mapper.SubjectMapper;

/**
 * Integration tests for the {@link SubjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_HOURS = 1;
    private static final Integer UPDATED_HOURS = 2;
    private static final Integer SMALLER_HOURS = 1 - 1;

    private static final Boolean DEFAULT_IN_USE = false;
    private static final Boolean UPDATED_IN_USE = true;

    private static final String ENTITY_API_URL = "/api/subjects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubjectMockMvc;

    private Subject subject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createEntity(EntityManager em) {
        Subject subject = new Subject().name(DEFAULT_NAME).hours(DEFAULT_HOURS).inUse(DEFAULT_IN_USE);
        return subject;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createUpdatedEntity(EntityManager em) {
        Subject subject = new Subject().name(UPDATED_NAME).hours(UPDATED_HOURS).inUse(UPDATED_IN_USE);
        return subject;
    }

    @BeforeEach
    public void initTest() {
        subject = createEntity(em);
    }

    @Test
    @Transactional
    void createSubject() throws Exception {
        int databaseSizeBeforeCreate = subjectRepository.findAll().size();
        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);
        restSubjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjectDTO)))
            .andExpect(status().isCreated());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeCreate + 1);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSubject.getHours()).isEqualTo(DEFAULT_HOURS);
        assertThat(testSubject.getInUse()).isEqualTo(DEFAULT_IN_USE);
    }

    @Test
    @Transactional
    void createSubjectWithExistingId() throws Exception {
        // Create the Subject with an existing ID
        subject.setId(1L);
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        int databaseSizeBeforeCreate = subjectRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSubjects() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS)))
            .andExpect(jsonPath("$.[*].inUse").value(hasItem(DEFAULT_IN_USE.booleanValue())));
    }

    @Test
    @Transactional
    void getSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get the subject
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL_ID, subject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subject.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS))
            .andExpect(jsonPath("$.inUse").value(DEFAULT_IN_USE.booleanValue()));
    }

    @Test
    @Transactional
    void getSubjectsByIdFiltering() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        Long id = subject.getId();

        defaultSubjectShouldBeFound("id.equals=" + id);
        defaultSubjectShouldNotBeFound("id.notEquals=" + id);

        defaultSubjectShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSubjectShouldNotBeFound("id.greaterThan=" + id);

        defaultSubjectShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSubjectShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name equals to DEFAULT_NAME
        defaultSubjectShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the subjectList where name equals to UPDATED_NAME
        defaultSubjectShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSubjectShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the subjectList where name equals to UPDATED_NAME
        defaultSubjectShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name is not null
        defaultSubjectShouldBeFound("name.specified=true");

        // Get all the subjectList where name is null
        defaultSubjectShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByNameContainsSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name contains DEFAULT_NAME
        defaultSubjectShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the subjectList where name contains UPDATED_NAME
        defaultSubjectShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where name does not contain DEFAULT_NAME
        defaultSubjectShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the subjectList where name does not contain UPDATED_NAME
        defaultSubjectShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByHoursIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where hours equals to DEFAULT_HOURS
        defaultSubjectShouldBeFound("hours.equals=" + DEFAULT_HOURS);

        // Get all the subjectList where hours equals to UPDATED_HOURS
        defaultSubjectShouldNotBeFound("hours.equals=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    void getAllSubjectsByHoursIsInShouldWork() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where hours in DEFAULT_HOURS or UPDATED_HOURS
        defaultSubjectShouldBeFound("hours.in=" + DEFAULT_HOURS + "," + UPDATED_HOURS);

        // Get all the subjectList where hours equals to UPDATED_HOURS
        defaultSubjectShouldNotBeFound("hours.in=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    void getAllSubjectsByHoursIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where hours is not null
        defaultSubjectShouldBeFound("hours.specified=true");

        // Get all the subjectList where hours is null
        defaultSubjectShouldNotBeFound("hours.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByHoursIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where hours is greater than or equal to DEFAULT_HOURS
        defaultSubjectShouldBeFound("hours.greaterThanOrEqual=" + DEFAULT_HOURS);

        // Get all the subjectList where hours is greater than or equal to UPDATED_HOURS
        defaultSubjectShouldNotBeFound("hours.greaterThanOrEqual=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    void getAllSubjectsByHoursIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where hours is less than or equal to DEFAULT_HOURS
        defaultSubjectShouldBeFound("hours.lessThanOrEqual=" + DEFAULT_HOURS);

        // Get all the subjectList where hours is less than or equal to SMALLER_HOURS
        defaultSubjectShouldNotBeFound("hours.lessThanOrEqual=" + SMALLER_HOURS);
    }

    @Test
    @Transactional
    void getAllSubjectsByHoursIsLessThanSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where hours is less than DEFAULT_HOURS
        defaultSubjectShouldNotBeFound("hours.lessThan=" + DEFAULT_HOURS);

        // Get all the subjectList where hours is less than UPDATED_HOURS
        defaultSubjectShouldBeFound("hours.lessThan=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    void getAllSubjectsByHoursIsGreaterThanSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where hours is greater than DEFAULT_HOURS
        defaultSubjectShouldNotBeFound("hours.greaterThan=" + DEFAULT_HOURS);

        // Get all the subjectList where hours is greater than SMALLER_HOURS
        defaultSubjectShouldBeFound("hours.greaterThan=" + SMALLER_HOURS);
    }

    @Test
    @Transactional
    void getAllSubjectsByInUseIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where inUse equals to DEFAULT_IN_USE
        defaultSubjectShouldBeFound("inUse.equals=" + DEFAULT_IN_USE);

        // Get all the subjectList where inUse equals to UPDATED_IN_USE
        defaultSubjectShouldNotBeFound("inUse.equals=" + UPDATED_IN_USE);
    }

    @Test
    @Transactional
    void getAllSubjectsByInUseIsInShouldWork() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where inUse in DEFAULT_IN_USE or UPDATED_IN_USE
        defaultSubjectShouldBeFound("inUse.in=" + DEFAULT_IN_USE + "," + UPDATED_IN_USE);

        // Get all the subjectList where inUse equals to UPDATED_IN_USE
        defaultSubjectShouldNotBeFound("inUse.in=" + UPDATED_IN_USE);
    }

    @Test
    @Transactional
    void getAllSubjectsByInUseIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where inUse is not null
        defaultSubjectShouldBeFound("inUse.specified=true");

        // Get all the subjectList where inUse is null
        defaultSubjectShouldNotBeFound("inUse.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsBySpecIsEqualToSomething() throws Exception {
        Spec spec;
        if (TestUtil.findAll(em, Spec.class).isEmpty()) {
            subjectRepository.saveAndFlush(subject);
            spec = SpecResourceIT.createEntity(em);
        } else {
            spec = TestUtil.findAll(em, Spec.class).get(0);
        }
        em.persist(spec);
        em.flush();
        subject.setSpec(spec);
        subjectRepository.saveAndFlush(subject);
        Long specId = spec.getId();
        // Get all the subjectList where spec equals to specId
        defaultSubjectShouldBeFound("specId.equals=" + specId);

        // Get all the subjectList where spec equals to (specId + 1)
        defaultSubjectShouldNotBeFound("specId.equals=" + (specId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSubjectShouldBeFound(String filter) throws Exception {
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS)))
            .andExpect(jsonPath("$.[*].inUse").value(hasItem(DEFAULT_IN_USE.booleanValue())));

        // Check, that the count call also returns 1
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSubjectShouldNotBeFound(String filter) throws Exception {
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSubject() throws Exception {
        // Get the subject
        restSubjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();

        // Update the subject
        Subject updatedSubject = subjectRepository.findById(subject.getId()).get();
        // Disconnect from session so that the updates on updatedSubject are not directly saved in db
        em.detach(updatedSubject);
        updatedSubject.name(UPDATED_NAME).hours(UPDATED_HOURS).inUse(UPDATED_IN_USE);
        SubjectDTO subjectDTO = subjectMapper.toDto(updatedSubject);

        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subjectDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubject.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testSubject.getInUse()).isEqualTo(UPDATED_IN_USE);
    }

    @Test
    @Transactional
    void putNonExistingSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subjectDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        partialUpdatedSubject.name(UPDATED_NAME).hours(UPDATED_HOURS);

        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubject))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubject.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testSubject.getInUse()).isEqualTo(DEFAULT_IN_USE);
    }

    @Test
    @Transactional
    void fullUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        partialUpdatedSubject.name(UPDATED_NAME).hours(UPDATED_HOURS).inUse(UPDATED_IN_USE);

        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubject))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubject.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testSubject.getInUse()).isEqualTo(UPDATED_IN_USE);
    }

    @Test
    @Transactional
    void patchNonExistingSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subjectDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeDelete = subjectRepository.findAll().size();

        // Delete the subject
        restSubjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, subject.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
