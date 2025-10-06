package com.myapp.web.rest;

import static com.myapp.domain.GuardianAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.Guardian;
import com.myapp.repository.GuardianRepository;
import com.myapp.service.dto.GuardianDTO;
import com.myapp.service.mapper.GuardianMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GuardianResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GuardianResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MIDDLE_INITIAL = "A";
    private static final String UPDATED_MIDDLE_INITIAL = "B";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RELATIONSHIP_TO_PLAYER = "AAAAAAAAAA";
    private static final String UPDATED_RELATIONSHIP_TO_PLAYER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_TEST_FIELD = "AAAAAAAAAA";
    private static final String UPDATED_TEST_FIELD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/guardians";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianMapper guardianMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGuardianMockMvc;

    private Guardian guardian;

    private Guardian insertedGuardian;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Guardian createEntity() {
        return new Guardian()
            .firstName(DEFAULT_FIRST_NAME)
            .middleInitial(DEFAULT_MIDDLE_INITIAL)
            .lastName(DEFAULT_LAST_NAME)
            .relationshipToPlayer(DEFAULT_RELATIONSHIP_TO_PLAYER)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
            .testField(DEFAULT_TEST_FIELD);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Guardian createUpdatedEntity() {
        return new Guardian()
            .firstName(UPDATED_FIRST_NAME)
            .middleInitial(UPDATED_MIDDLE_INITIAL)
            .lastName(UPDATED_LAST_NAME)
            .relationshipToPlayer(UPDATED_RELATIONSHIP_TO_PLAYER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .testField(UPDATED_TEST_FIELD);
    }

    @BeforeEach
    void initTest() {
        guardian = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGuardian != null) {
            guardianRepository.delete(insertedGuardian);
            insertedGuardian = null;
        }
    }

    @Test
    @Transactional
    void createGuardian() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);
        var returnedGuardianDTO = om.readValue(
            restGuardianMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guardianDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GuardianDTO.class
        );

        // Validate the Guardian in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGuardian = guardianMapper.toEntity(returnedGuardianDTO);
        assertGuardianUpdatableFieldsEquals(returnedGuardian, getPersistedGuardian(returnedGuardian));

        insertedGuardian = returnedGuardian;
    }

    @Test
    @Transactional
    void createGuardianWithExistingId() throws Exception {
        // Create the Guardian with an existing ID
        guardian.setId(1L);
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        guardian.setFirstName(null);

        // Create the Guardian, which fails.
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        guardian.setLastName(null);

        // Create the Guardian, which fails.
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRelationshipToPlayerIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        guardian.setRelationshipToPlayer(null);

        // Create the Guardian, which fails.
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateOfBirthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        guardian.setDateOfBirth(null);

        // Create the Guardian, which fails.
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGuardians() throws Exception {
        // Initialize the database
        insertedGuardian = guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(guardian.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].middleInitial").value(hasItem(DEFAULT_MIDDLE_INITIAL)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].relationshipToPlayer").value(hasItem(DEFAULT_RELATIONSHIP_TO_PLAYER)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].testField").value(hasItem(DEFAULT_TEST_FIELD)));
    }

    @Test
    @Transactional
    void getGuardian() throws Exception {
        // Initialize the database
        insertedGuardian = guardianRepository.saveAndFlush(guardian);

        // Get the guardian
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL_ID, guardian.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(guardian.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.middleInitial").value(DEFAULT_MIDDLE_INITIAL))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.relationshipToPlayer").value(DEFAULT_RELATIONSHIP_TO_PLAYER))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()))
            .andExpect(jsonPath("$.testField").value(DEFAULT_TEST_FIELD));
    }

    @Test
    @Transactional
    void getNonExistingGuardian() throws Exception {
        // Get the guardian
        restGuardianMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGuardian() throws Exception {
        // Initialize the database
        insertedGuardian = guardianRepository.saveAndFlush(guardian);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the guardian
        Guardian updatedGuardian = guardianRepository.findById(guardian.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGuardian are not directly saved in db
        em.detach(updatedGuardian);
        updatedGuardian
            .firstName(UPDATED_FIRST_NAME)
            .middleInitial(UPDATED_MIDDLE_INITIAL)
            .lastName(UPDATED_LAST_NAME)
            .relationshipToPlayer(UPDATED_RELATIONSHIP_TO_PLAYER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .testField(UPDATED_TEST_FIELD);
        GuardianDTO guardianDTO = guardianMapper.toDto(updatedGuardian);

        restGuardianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, guardianDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(guardianDTO))
            )
            .andExpect(status().isOk());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGuardianToMatchAllProperties(updatedGuardian);
    }

    @Test
    @Transactional
    void putNonExistingGuardian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        guardian.setId(longCount.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, guardianDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGuardian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        guardian.setId(longCount.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGuardian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        guardian.setId(longCount.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guardianDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGuardianWithPatch() throws Exception {
        // Initialize the database
        insertedGuardian = guardianRepository.saveAndFlush(guardian);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the guardian using partial update
        Guardian partialUpdatedGuardian = new Guardian();
        partialUpdatedGuardian.setId(guardian.getId());

        partialUpdatedGuardian.firstName(UPDATED_FIRST_NAME).middleInitial(UPDATED_MIDDLE_INITIAL).testField(UPDATED_TEST_FIELD);

        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGuardian.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGuardian))
            )
            .andExpect(status().isOk());

        // Validate the Guardian in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGuardianUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGuardian, guardian), getPersistedGuardian(guardian));
    }

    @Test
    @Transactional
    void fullUpdateGuardianWithPatch() throws Exception {
        // Initialize the database
        insertedGuardian = guardianRepository.saveAndFlush(guardian);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the guardian using partial update
        Guardian partialUpdatedGuardian = new Guardian();
        partialUpdatedGuardian.setId(guardian.getId());

        partialUpdatedGuardian
            .firstName(UPDATED_FIRST_NAME)
            .middleInitial(UPDATED_MIDDLE_INITIAL)
            .lastName(UPDATED_LAST_NAME)
            .relationshipToPlayer(UPDATED_RELATIONSHIP_TO_PLAYER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .testField(UPDATED_TEST_FIELD);

        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGuardian.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGuardian))
            )
            .andExpect(status().isOk());

        // Validate the Guardian in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGuardianUpdatableFieldsEquals(partialUpdatedGuardian, getPersistedGuardian(partialUpdatedGuardian));
    }

    @Test
    @Transactional
    void patchNonExistingGuardian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        guardian.setId(longCount.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, guardianDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGuardian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        guardian.setId(longCount.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGuardian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        guardian.setId(longCount.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(guardianDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Guardian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGuardian() throws Exception {
        // Initialize the database
        insertedGuardian = guardianRepository.saveAndFlush(guardian);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the guardian
        restGuardianMockMvc
            .perform(delete(ENTITY_API_URL_ID, guardian.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return guardianRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Guardian getPersistedGuardian(Guardian guardian) {
        return guardianRepository.findById(guardian.getId()).orElseThrow();
    }

    protected void assertPersistedGuardianToMatchAllProperties(Guardian expectedGuardian) {
        assertGuardianAllPropertiesEquals(expectedGuardian, getPersistedGuardian(expectedGuardian));
    }

    protected void assertPersistedGuardianToMatchUpdatableProperties(Guardian expectedGuardian) {
        assertGuardianAllUpdatablePropertiesEquals(expectedGuardian, getPersistedGuardian(expectedGuardian));
    }
}
