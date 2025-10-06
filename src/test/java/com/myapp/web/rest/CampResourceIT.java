package com.myapp.web.rest;

import static com.myapp.domain.CampAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.Camp;
import com.myapp.domain.enumeration.CompetitionStatus;
import com.myapp.repository.CampRepository;
import com.myapp.service.dto.CampDTO;
import com.myapp.service.mapper.CampMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link CampResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CampResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDITIONAL_INFO = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_INFO = "BBBBBBBBBB";

    private static final CompetitionStatus DEFAULT_STATUS = CompetitionStatus.ACTIVE;
    private static final CompetitionStatus UPDATED_STATUS = CompetitionStatus.INACTIVE;

    private static final String ENTITY_API_URL = "/api/camps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CampRepository campRepository;

    @Autowired
    private CampMapper campMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCampMockMvc;

    private Camp camp;

    private Camp insertedCamp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Camp createEntity() {
        return new Camp().name(DEFAULT_NAME).additionalInfo(DEFAULT_ADDITIONAL_INFO).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Camp createUpdatedEntity() {
        return new Camp().name(UPDATED_NAME).additionalInfo(UPDATED_ADDITIONAL_INFO).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        camp = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCamp != null) {
            campRepository.delete(insertedCamp);
            insertedCamp = null;
        }
    }

    @Test
    @Transactional
    void createCamp() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Camp
        CampDTO campDTO = campMapper.toDto(camp);
        var returnedCampDTO = om.readValue(
            restCampMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CampDTO.class
        );

        // Validate the Camp in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCamp = campMapper.toEntity(returnedCampDTO);
        assertCampUpdatableFieldsEquals(returnedCamp, getPersistedCamp(returnedCamp));

        insertedCamp = returnedCamp;
    }

    @Test
    @Transactional
    void createCampWithExistingId() throws Exception {
        // Create the Camp with an existing ID
        camp.setId(1L);
        CampDTO campDTO = campMapper.toDto(camp);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCampMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        camp.setName(null);

        // Create the Camp, which fails.
        CampDTO campDTO = campMapper.toDto(camp);

        restCampMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        camp.setStatus(null);

        // Create the Camp, which fails.
        CampDTO campDTO = campMapper.toDto(camp);

        restCampMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCamps() throws Exception {
        // Initialize the database
        insertedCamp = campRepository.saveAndFlush(camp);

        // Get all the campList
        restCampMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(camp.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].additionalInfo").value(hasItem(DEFAULT_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getCamp() throws Exception {
        // Initialize the database
        insertedCamp = campRepository.saveAndFlush(camp);

        // Get the camp
        restCampMockMvc
            .perform(get(ENTITY_API_URL_ID, camp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(camp.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.additionalInfo").value(DEFAULT_ADDITIONAL_INFO))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCamp() throws Exception {
        // Get the camp
        restCampMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCamp() throws Exception {
        // Initialize the database
        insertedCamp = campRepository.saveAndFlush(camp);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the camp
        Camp updatedCamp = campRepository.findById(camp.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCamp are not directly saved in db
        em.detach(updatedCamp);
        updatedCamp.name(UPDATED_NAME).additionalInfo(UPDATED_ADDITIONAL_INFO).status(UPDATED_STATUS);
        CampDTO campDTO = campMapper.toDto(updatedCamp);

        restCampMockMvc
            .perform(put(ENTITY_API_URL_ID, campDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campDTO)))
            .andExpect(status().isOk());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCampToMatchAllProperties(updatedCamp);
    }

    @Test
    @Transactional
    void putNonExistingCamp() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        camp.setId(longCount.incrementAndGet());

        // Create the Camp
        CampDTO campDTO = campMapper.toDto(camp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCampMockMvc
            .perform(put(ENTITY_API_URL_ID, campDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCamp() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        camp.setId(longCount.incrementAndGet());

        // Create the Camp
        CampDTO campDTO = campMapper.toDto(camp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(campDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCamp() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        camp.setId(longCount.incrementAndGet());

        // Create the Camp
        CampDTO campDTO = campMapper.toDto(camp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCampWithPatch() throws Exception {
        // Initialize the database
        insertedCamp = campRepository.saveAndFlush(camp);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the camp using partial update
        Camp partialUpdatedCamp = new Camp();
        partialUpdatedCamp.setId(camp.getId());

        partialUpdatedCamp.status(UPDATED_STATUS);

        restCampMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCamp.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCamp))
            )
            .andExpect(status().isOk());

        // Validate the Camp in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCampUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCamp, camp), getPersistedCamp(camp));
    }

    @Test
    @Transactional
    void fullUpdateCampWithPatch() throws Exception {
        // Initialize the database
        insertedCamp = campRepository.saveAndFlush(camp);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the camp using partial update
        Camp partialUpdatedCamp = new Camp();
        partialUpdatedCamp.setId(camp.getId());

        partialUpdatedCamp.name(UPDATED_NAME).additionalInfo(UPDATED_ADDITIONAL_INFO).status(UPDATED_STATUS);

        restCampMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCamp.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCamp))
            )
            .andExpect(status().isOk());

        // Validate the Camp in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCampUpdatableFieldsEquals(partialUpdatedCamp, getPersistedCamp(partialUpdatedCamp));
    }

    @Test
    @Transactional
    void patchNonExistingCamp() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        camp.setId(longCount.incrementAndGet());

        // Create the Camp
        CampDTO campDTO = campMapper.toDto(camp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCampMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, campDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(campDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCamp() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        camp.setId(longCount.incrementAndGet());

        // Create the Camp
        CampDTO campDTO = campMapper.toDto(camp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(campDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCamp() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        camp.setId(longCount.incrementAndGet());

        // Create the Camp
        CampDTO campDTO = campMapper.toDto(camp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(campDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Camp in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCamp() throws Exception {
        // Initialize the database
        insertedCamp = campRepository.saveAndFlush(camp);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the camp
        restCampMockMvc
            .perform(delete(ENTITY_API_URL_ID, camp.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return campRepository.count();
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

    protected Camp getPersistedCamp(Camp camp) {
        return campRepository.findById(camp.getId()).orElseThrow();
    }

    protected void assertPersistedCampToMatchAllProperties(Camp expectedCamp) {
        assertCampAllPropertiesEquals(expectedCamp, getPersistedCamp(expectedCamp));
    }

    protected void assertPersistedCampToMatchUpdatableProperties(Camp expectedCamp) {
        assertCampAllUpdatablePropertiesEquals(expectedCamp, getPersistedCamp(expectedCamp));
    }
}
