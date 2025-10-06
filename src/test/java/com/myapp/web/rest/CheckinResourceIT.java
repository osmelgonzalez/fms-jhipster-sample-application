package com.myapp.web.rest;

import static com.myapp.domain.CheckinAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.Checkin;
import com.myapp.domain.Player;
import com.myapp.repository.CheckinRepository;
import com.myapp.service.dto.CheckinDTO;
import com.myapp.service.mapper.CheckinMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link CheckinResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CheckinResourceIT {

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/checkins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private CheckinMapper checkinMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCheckinMockMvc;

    private Checkin checkin;

    private Checkin insertedCheckin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkin createEntity(EntityManager em) {
        Checkin checkin = new Checkin().timestamp(DEFAULT_TIMESTAMP);
        // Add required entity
        Player player;
        if (TestUtil.findAll(em, Player.class).isEmpty()) {
            player = PlayerResourceIT.createEntity();
            em.persist(player);
            em.flush();
        } else {
            player = TestUtil.findAll(em, Player.class).get(0);
        }
        checkin.setPlayer(player);
        return checkin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkin createUpdatedEntity(EntityManager em) {
        Checkin updatedCheckin = new Checkin().timestamp(UPDATED_TIMESTAMP);
        // Add required entity
        Player player;
        if (TestUtil.findAll(em, Player.class).isEmpty()) {
            player = PlayerResourceIT.createUpdatedEntity();
            em.persist(player);
            em.flush();
        } else {
            player = TestUtil.findAll(em, Player.class).get(0);
        }
        updatedCheckin.setPlayer(player);
        return updatedCheckin;
    }

    @BeforeEach
    void initTest() {
        checkin = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCheckin != null) {
            checkinRepository.delete(insertedCheckin);
            insertedCheckin = null;
        }
    }

    @Test
    @Transactional
    void createCheckin() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Checkin
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);
        var returnedCheckinDTO = om.readValue(
            restCheckinMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkinDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CheckinDTO.class
        );

        // Validate the Checkin in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCheckin = checkinMapper.toEntity(returnedCheckinDTO);
        assertCheckinUpdatableFieldsEquals(returnedCheckin, getPersistedCheckin(returnedCheckin));

        insertedCheckin = returnedCheckin;
    }

    @Test
    @Transactional
    void createCheckinWithExistingId() throws Exception {
        // Create the Checkin with an existing ID
        checkin.setId(1L);
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCheckinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkinDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        checkin.setTimestamp(null);

        // Create the Checkin, which fails.
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        restCheckinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkinDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCheckins() throws Exception {
        // Initialize the database
        insertedCheckin = checkinRepository.saveAndFlush(checkin);

        // Get all the checkinList
        restCheckinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkin.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }

    @Test
    @Transactional
    void getCheckin() throws Exception {
        // Initialize the database
        insertedCheckin = checkinRepository.saveAndFlush(checkin);

        // Get the checkin
        restCheckinMockMvc
            .perform(get(ENTITY_API_URL_ID, checkin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(checkin.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCheckin() throws Exception {
        // Get the checkin
        restCheckinMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCheckin() throws Exception {
        // Initialize the database
        insertedCheckin = checkinRepository.saveAndFlush(checkin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkin
        Checkin updatedCheckin = checkinRepository.findById(checkin.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCheckin are not directly saved in db
        em.detach(updatedCheckin);
        updatedCheckin.timestamp(UPDATED_TIMESTAMP);
        CheckinDTO checkinDTO = checkinMapper.toDto(updatedCheckin);

        restCheckinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkinDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkinDTO))
            )
            .andExpect(status().isOk());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCheckinToMatchAllProperties(updatedCheckin);
    }

    @Test
    @Transactional
    void putNonExistingCheckin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkin.setId(longCount.incrementAndGet());

        // Create the Checkin
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkinDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCheckin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkin.setId(longCount.incrementAndGet());

        // Create the Checkin
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(checkinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCheckin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkin.setId(longCount.incrementAndGet());

        // Create the Checkin
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckinMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkinDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCheckinWithPatch() throws Exception {
        // Initialize the database
        insertedCheckin = checkinRepository.saveAndFlush(checkin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkin using partial update
        Checkin partialUpdatedCheckin = new Checkin();
        partialUpdatedCheckin.setId(checkin.getId());

        partialUpdatedCheckin.timestamp(UPDATED_TIMESTAMP);

        restCheckinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckin))
            )
            .andExpect(status().isOk());

        // Validate the Checkin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckinUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCheckin, checkin), getPersistedCheckin(checkin));
    }

    @Test
    @Transactional
    void fullUpdateCheckinWithPatch() throws Exception {
        // Initialize the database
        insertedCheckin = checkinRepository.saveAndFlush(checkin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkin using partial update
        Checkin partialUpdatedCheckin = new Checkin();
        partialUpdatedCheckin.setId(checkin.getId());

        partialUpdatedCheckin.timestamp(UPDATED_TIMESTAMP);

        restCheckinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckin))
            )
            .andExpect(status().isOk());

        // Validate the Checkin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckinUpdatableFieldsEquals(partialUpdatedCheckin, getPersistedCheckin(partialUpdatedCheckin));
    }

    @Test
    @Transactional
    void patchNonExistingCheckin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkin.setId(longCount.incrementAndGet());

        // Create the Checkin
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, checkinDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCheckin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkin.setId(longCount.incrementAndGet());

        // Create the Checkin
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCheckin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkin.setId(longCount.incrementAndGet());

        // Create the Checkin
        CheckinDTO checkinDTO = checkinMapper.toDto(checkin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckinMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(checkinDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Checkin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCheckin() throws Exception {
        // Initialize the database
        insertedCheckin = checkinRepository.saveAndFlush(checkin);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the checkin
        restCheckinMockMvc
            .perform(delete(ENTITY_API_URL_ID, checkin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return checkinRepository.count();
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

    protected Checkin getPersistedCheckin(Checkin checkin) {
        return checkinRepository.findById(checkin.getId()).orElseThrow();
    }

    protected void assertPersistedCheckinToMatchAllProperties(Checkin expectedCheckin) {
        assertCheckinAllPropertiesEquals(expectedCheckin, getPersistedCheckin(expectedCheckin));
    }

    protected void assertPersistedCheckinToMatchUpdatableProperties(Checkin expectedCheckin) {
        assertCheckinAllUpdatablePropertiesEquals(expectedCheckin, getPersistedCheckin(expectedCheckin));
    }
}
