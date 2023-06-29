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
import uz.devcraft.repository.SpecRepository;
import uz.devcraft.service.criteria.SpecCriteria;
import uz.devcraft.service.dto.SpecDTO;
import uz.devcraft.service.mapper.SpecMapper;

/**
 * Integration tests for the {@link SpecResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SpecResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/specs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpecRepository specRepository;

    @Autowired
    private SpecMapper specMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpecMockMvc;

    private Spec spec;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Spec createEntity(EntityManager em) {
        Spec spec = new Spec().name(DEFAULT_NAME);
        return spec;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Spec createUpdatedEntity(EntityManager em) {
        Spec spec = new Spec().name(UPDATED_NAME);
        return spec;
    }

    @BeforeEach
    public void initTest() {
        spec = createEntity(em);
    }

    @Test
    @Transactional
    void createSpec() throws Exception {
        int databaseSizeBeforeCreate = specRepository.findAll().size();
        // Create the Spec
        SpecDTO specDTO = specMapper.toDto(spec);
        restSpecMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(specDTO)))
            .andExpect(status().isCreated());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeCreate + 1);
        Spec testSpec = specList.get(specList.size() - 1);
        assertThat(testSpec.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createSpecWithExistingId() throws Exception {
        // Create the Spec with an existing ID
        spec.setId(1L);
        SpecDTO specDTO = specMapper.toDto(spec);

        int databaseSizeBeforeCreate = specRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpecMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(specDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSpecs() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        // Get all the specList
        restSpecMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spec.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getSpec() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        // Get the spec
        restSpecMockMvc
            .perform(get(ENTITY_API_URL_ID, spec.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spec.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getSpecsByIdFiltering() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        Long id = spec.getId();

        defaultSpecShouldBeFound("id.equals=" + id);
        defaultSpecShouldNotBeFound("id.notEquals=" + id);

        defaultSpecShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSpecShouldNotBeFound("id.greaterThan=" + id);

        defaultSpecShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSpecShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSpecsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        // Get all the specList where name equals to DEFAULT_NAME
        defaultSpecShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the specList where name equals to UPDATED_NAME
        defaultSpecShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpecsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        // Get all the specList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSpecShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the specList where name equals to UPDATED_NAME
        defaultSpecShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpecsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        // Get all the specList where name is not null
        defaultSpecShouldBeFound("name.specified=true");

        // Get all the specList where name is null
        defaultSpecShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSpecsByNameContainsSomething() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        // Get all the specList where name contains DEFAULT_NAME
        defaultSpecShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the specList where name contains UPDATED_NAME
        defaultSpecShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpecsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        // Get all the specList where name does not contain DEFAULT_NAME
        defaultSpecShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the specList where name does not contain UPDATED_NAME
        defaultSpecShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSpecShouldBeFound(String filter) throws Exception {
        restSpecMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spec.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restSpecMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSpecShouldNotBeFound(String filter) throws Exception {
        restSpecMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSpecMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSpec() throws Exception {
        // Get the spec
        restSpecMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSpec() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        int databaseSizeBeforeUpdate = specRepository.findAll().size();

        // Update the spec
        Spec updatedSpec = specRepository.findById(spec.getId()).get();
        // Disconnect from session so that the updates on updatedSpec are not directly saved in db
        em.detach(updatedSpec);
        updatedSpec.name(UPDATED_NAME);
        SpecDTO specDTO = specMapper.toDto(updatedSpec);

        restSpecMockMvc
            .perform(
                put(ENTITY_API_URL_ID, specDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(specDTO))
            )
            .andExpect(status().isOk());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
        Spec testSpec = specList.get(specList.size() - 1);
        assertThat(testSpec.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingSpec() throws Exception {
        int databaseSizeBeforeUpdate = specRepository.findAll().size();
        spec.setId(count.incrementAndGet());

        // Create the Spec
        SpecDTO specDTO = specMapper.toDto(spec);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpecMockMvc
            .perform(
                put(ENTITY_API_URL_ID, specDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(specDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpec() throws Exception {
        int databaseSizeBeforeUpdate = specRepository.findAll().size();
        spec.setId(count.incrementAndGet());

        // Create the Spec
        SpecDTO specDTO = specMapper.toDto(spec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(specDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpec() throws Exception {
        int databaseSizeBeforeUpdate = specRepository.findAll().size();
        spec.setId(count.incrementAndGet());

        // Create the Spec
        SpecDTO specDTO = specMapper.toDto(spec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(specDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpecWithPatch() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        int databaseSizeBeforeUpdate = specRepository.findAll().size();

        // Update the spec using partial update
        Spec partialUpdatedSpec = new Spec();
        partialUpdatedSpec.setId(spec.getId());

        partialUpdatedSpec.name(UPDATED_NAME);

        restSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpec.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpec))
            )
            .andExpect(status().isOk());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
        Spec testSpec = specList.get(specList.size() - 1);
        assertThat(testSpec.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSpecWithPatch() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        int databaseSizeBeforeUpdate = specRepository.findAll().size();

        // Update the spec using partial update
        Spec partialUpdatedSpec = new Spec();
        partialUpdatedSpec.setId(spec.getId());

        partialUpdatedSpec.name(UPDATED_NAME);

        restSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpec.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpec))
            )
            .andExpect(status().isOk());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
        Spec testSpec = specList.get(specList.size() - 1);
        assertThat(testSpec.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSpec() throws Exception {
        int databaseSizeBeforeUpdate = specRepository.findAll().size();
        spec.setId(count.incrementAndGet());

        // Create the Spec
        SpecDTO specDTO = specMapper.toDto(spec);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, specDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(specDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpec() throws Exception {
        int databaseSizeBeforeUpdate = specRepository.findAll().size();
        spec.setId(count.incrementAndGet());

        // Create the Spec
        SpecDTO specDTO = specMapper.toDto(spec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(specDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpec() throws Exception {
        int databaseSizeBeforeUpdate = specRepository.findAll().size();
        spec.setId(count.incrementAndGet());

        // Create the Spec
        SpecDTO specDTO = specMapper.toDto(spec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(specDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Spec in the database
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpec() throws Exception {
        // Initialize the database
        specRepository.saveAndFlush(spec);

        int databaseSizeBeforeDelete = specRepository.findAll().size();

        // Delete the spec
        restSpecMockMvc
            .perform(delete(ENTITY_API_URL_ID, spec.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Spec> specList = specRepository.findAll();
        assertThat(specList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
