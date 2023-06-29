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
import uz.devcraft.domain.Staff;
import uz.devcraft.repository.StaffRepository;
import uz.devcraft.service.criteria.StaffCriteria;
import uz.devcraft.service.dto.StaffDTO;
import uz.devcraft.service.mapper.StaffMapper;

/**
 * Integration tests for the {@link StaffResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StaffResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_FROM_HOURS = 1;
    private static final Integer UPDATED_FROM_HOURS = 2;
    private static final Integer SMALLER_FROM_HOURS = 1 - 1;

    private static final Integer DEFAULT_TO_HOURS = 1;
    private static final Integer UPDATED_TO_HOURS = 2;
    private static final Integer SMALLER_TO_HOURS = 1 - 1;

    private static final String ENTITY_API_URL = "/api/staff";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStaffMockMvc;

    private Staff staff;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createEntity(EntityManager em) {
        Staff staff = new Staff().name(DEFAULT_NAME).fromHours(DEFAULT_FROM_HOURS).toHours(DEFAULT_TO_HOURS);
        return staff;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createUpdatedEntity(EntityManager em) {
        Staff staff = new Staff().name(UPDATED_NAME).fromHours(UPDATED_FROM_HOURS).toHours(UPDATED_TO_HOURS);
        return staff;
    }

    @BeforeEach
    public void initTest() {
        staff = createEntity(em);
    }

    @Test
    @Transactional
    void createStaff() throws Exception {
        int databaseSizeBeforeCreate = staffRepository.findAll().size();
        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);
        restStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isCreated());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeCreate + 1);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStaff.getFromHours()).isEqualTo(DEFAULT_FROM_HOURS);
        assertThat(testStaff.getToHours()).isEqualTo(DEFAULT_TO_HOURS);
    }

    @Test
    @Transactional
    void createStaffWithExistingId() throws Exception {
        // Create the Staff with an existing ID
        staff.setId(1L);
        StaffDTO staffDTO = staffMapper.toDto(staff);

        int databaseSizeBeforeCreate = staffRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fromHours").value(hasItem(DEFAULT_FROM_HOURS)))
            .andExpect(jsonPath("$.[*].toHours").value(hasItem(DEFAULT_TO_HOURS)));
    }

    @Test
    @Transactional
    void getStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get the staff
        restStaffMockMvc
            .perform(get(ENTITY_API_URL_ID, staff.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(staff.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fromHours").value(DEFAULT_FROM_HOURS))
            .andExpect(jsonPath("$.toHours").value(DEFAULT_TO_HOURS));
    }

    @Test
    @Transactional
    void getStaffByIdFiltering() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        Long id = staff.getId();

        defaultStaffShouldBeFound("id.equals=" + id);
        defaultStaffShouldNotBeFound("id.notEquals=" + id);

        defaultStaffShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStaffShouldNotBeFound("id.greaterThan=" + id);

        defaultStaffShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStaffShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStaffByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where name equals to DEFAULT_NAME
        defaultStaffShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the staffList where name equals to UPDATED_NAME
        defaultStaffShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByNameIsInShouldWork() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where name in DEFAULT_NAME or UPDATED_NAME
        defaultStaffShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the staffList where name equals to UPDATED_NAME
        defaultStaffShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where name is not null
        defaultStaffShouldBeFound("name.specified=true");

        // Get all the staffList where name is null
        defaultStaffShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByNameContainsSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where name contains DEFAULT_NAME
        defaultStaffShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the staffList where name contains UPDATED_NAME
        defaultStaffShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByNameNotContainsSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where name does not contain DEFAULT_NAME
        defaultStaffShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the staffList where name does not contain UPDATED_NAME
        defaultStaffShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByFromHoursIsEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where fromHours equals to DEFAULT_FROM_HOURS
        defaultStaffShouldBeFound("fromHours.equals=" + DEFAULT_FROM_HOURS);

        // Get all the staffList where fromHours equals to UPDATED_FROM_HOURS
        defaultStaffShouldNotBeFound("fromHours.equals=" + UPDATED_FROM_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByFromHoursIsInShouldWork() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where fromHours in DEFAULT_FROM_HOURS or UPDATED_FROM_HOURS
        defaultStaffShouldBeFound("fromHours.in=" + DEFAULT_FROM_HOURS + "," + UPDATED_FROM_HOURS);

        // Get all the staffList where fromHours equals to UPDATED_FROM_HOURS
        defaultStaffShouldNotBeFound("fromHours.in=" + UPDATED_FROM_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByFromHoursIsNullOrNotNull() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where fromHours is not null
        defaultStaffShouldBeFound("fromHours.specified=true");

        // Get all the staffList where fromHours is null
        defaultStaffShouldNotBeFound("fromHours.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByFromHoursIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where fromHours is greater than or equal to DEFAULT_FROM_HOURS
        defaultStaffShouldBeFound("fromHours.greaterThanOrEqual=" + DEFAULT_FROM_HOURS);

        // Get all the staffList where fromHours is greater than or equal to UPDATED_FROM_HOURS
        defaultStaffShouldNotBeFound("fromHours.greaterThanOrEqual=" + UPDATED_FROM_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByFromHoursIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where fromHours is less than or equal to DEFAULT_FROM_HOURS
        defaultStaffShouldBeFound("fromHours.lessThanOrEqual=" + DEFAULT_FROM_HOURS);

        // Get all the staffList where fromHours is less than or equal to SMALLER_FROM_HOURS
        defaultStaffShouldNotBeFound("fromHours.lessThanOrEqual=" + SMALLER_FROM_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByFromHoursIsLessThanSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where fromHours is less than DEFAULT_FROM_HOURS
        defaultStaffShouldNotBeFound("fromHours.lessThan=" + DEFAULT_FROM_HOURS);

        // Get all the staffList where fromHours is less than UPDATED_FROM_HOURS
        defaultStaffShouldBeFound("fromHours.lessThan=" + UPDATED_FROM_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByFromHoursIsGreaterThanSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where fromHours is greater than DEFAULT_FROM_HOURS
        defaultStaffShouldNotBeFound("fromHours.greaterThan=" + DEFAULT_FROM_HOURS);

        // Get all the staffList where fromHours is greater than SMALLER_FROM_HOURS
        defaultStaffShouldBeFound("fromHours.greaterThan=" + SMALLER_FROM_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByToHoursIsEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where toHours equals to DEFAULT_TO_HOURS
        defaultStaffShouldBeFound("toHours.equals=" + DEFAULT_TO_HOURS);

        // Get all the staffList where toHours equals to UPDATED_TO_HOURS
        defaultStaffShouldNotBeFound("toHours.equals=" + UPDATED_TO_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByToHoursIsInShouldWork() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where toHours in DEFAULT_TO_HOURS or UPDATED_TO_HOURS
        defaultStaffShouldBeFound("toHours.in=" + DEFAULT_TO_HOURS + "," + UPDATED_TO_HOURS);

        // Get all the staffList where toHours equals to UPDATED_TO_HOURS
        defaultStaffShouldNotBeFound("toHours.in=" + UPDATED_TO_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByToHoursIsNullOrNotNull() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where toHours is not null
        defaultStaffShouldBeFound("toHours.specified=true");

        // Get all the staffList where toHours is null
        defaultStaffShouldNotBeFound("toHours.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByToHoursIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where toHours is greater than or equal to DEFAULT_TO_HOURS
        defaultStaffShouldBeFound("toHours.greaterThanOrEqual=" + DEFAULT_TO_HOURS);

        // Get all the staffList where toHours is greater than or equal to UPDATED_TO_HOURS
        defaultStaffShouldNotBeFound("toHours.greaterThanOrEqual=" + UPDATED_TO_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByToHoursIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where toHours is less than or equal to DEFAULT_TO_HOURS
        defaultStaffShouldBeFound("toHours.lessThanOrEqual=" + DEFAULT_TO_HOURS);

        // Get all the staffList where toHours is less than or equal to SMALLER_TO_HOURS
        defaultStaffShouldNotBeFound("toHours.lessThanOrEqual=" + SMALLER_TO_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByToHoursIsLessThanSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where toHours is less than DEFAULT_TO_HOURS
        defaultStaffShouldNotBeFound("toHours.lessThan=" + DEFAULT_TO_HOURS);

        // Get all the staffList where toHours is less than UPDATED_TO_HOURS
        defaultStaffShouldBeFound("toHours.lessThan=" + UPDATED_TO_HOURS);
    }

    @Test
    @Transactional
    void getAllStaffByToHoursIsGreaterThanSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where toHours is greater than DEFAULT_TO_HOURS
        defaultStaffShouldNotBeFound("toHours.greaterThan=" + DEFAULT_TO_HOURS);

        // Get all the staffList where toHours is greater than SMALLER_TO_HOURS
        defaultStaffShouldBeFound("toHours.greaterThan=" + SMALLER_TO_HOURS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStaffShouldBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fromHours").value(hasItem(DEFAULT_FROM_HOURS)))
            .andExpect(jsonPath("$.[*].toHours").value(hasItem(DEFAULT_TO_HOURS)));

        // Check, that the count call also returns 1
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStaffShouldNotBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStaff() throws Exception {
        // Get the staff
        restStaffMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeUpdate = staffRepository.findAll().size();

        // Update the staff
        Staff updatedStaff = staffRepository.findById(staff.getId()).get();
        // Disconnect from session so that the updates on updatedStaff are not directly saved in db
        em.detach(updatedStaff);
        updatedStaff.name(UPDATED_NAME).fromHours(UPDATED_FROM_HOURS).toHours(UPDATED_TO_HOURS);
        StaffDTO staffDTO = staffMapper.toDto(updatedStaff);

        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStaff.getFromHours()).isEqualTo(UPDATED_FROM_HOURS);
        assertThat(testStaff.getToHours()).isEqualTo(UPDATED_TO_HOURS);
    }

    @Test
    @Transactional
    void putNonExistingStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeUpdate = staffRepository.findAll().size();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStaff.getFromHours()).isEqualTo(DEFAULT_FROM_HOURS);
        assertThat(testStaff.getToHours()).isEqualTo(DEFAULT_TO_HOURS);
    }

    @Test
    @Transactional
    void fullUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeUpdate = staffRepository.findAll().size();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff.name(UPDATED_NAME).fromHours(UPDATED_FROM_HOURS).toHours(UPDATED_TO_HOURS);

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStaff.getFromHours()).isEqualTo(UPDATED_FROM_HOURS);
        assertThat(testStaff.getToHours()).isEqualTo(UPDATED_TO_HOURS);
    }

    @Test
    @Transactional
    void patchNonExistingStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, staffDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeDelete = staffRepository.findAll().size();

        // Delete the staff
        restStaffMockMvc
            .perform(delete(ENTITY_API_URL_ID, staff.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
