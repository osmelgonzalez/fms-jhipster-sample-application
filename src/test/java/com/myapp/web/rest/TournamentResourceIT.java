package com.myapp.web.rest;

import static com.myapp.domain.TournamentAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.Tournament;
import com.myapp.domain.enumeration.CompetitionStatus;
import com.myapp.repository.TournamentRepository;
import com.myapp.service.dto.TournamentDTO;
import com.myapp.service.mapper.TournamentMapper;
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
 * Integration tests for the {@link TournamentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TournamentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDITIONAL_INFO = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_INFO = "BBBBBBBBBB";

    private static final CompetitionStatus DEFAULT_STATUS = CompetitionStatus.ACTIVE;
    private static final CompetitionStatus UPDATED_STATUS = CompetitionStatus.INACTIVE;

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ENDS = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENDS = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/tournaments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTournamentMockMvc;

    private Tournament tournament;

    private Tournament insertedTournament;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tournament createEntity() {
        return new Tournament()
            .name(DEFAULT_NAME)
            .additionalInfo(DEFAULT_ADDITIONAL_INFO)
            .status(DEFAULT_STATUS)
            .start(DEFAULT_START)
            .ends(DEFAULT_ENDS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tournament createUpdatedEntity() {
        return new Tournament()
            .name(UPDATED_NAME)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .status(UPDATED_STATUS)
            .start(UPDATED_START)
            .ends(UPDATED_ENDS);
    }

    @BeforeEach
    void initTest() {
        tournament = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTournament != null) {
            tournamentRepository.delete(insertedTournament);
            insertedTournament = null;
        }
    }

    @Test
    @Transactional
    void createTournament() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);
        var returnedTournamentDTO = om.readValue(
            restTournamentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tournamentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TournamentDTO.class
        );

        // Validate the Tournament in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTournament = tournamentMapper.toEntity(returnedTournamentDTO);
        assertTournamentUpdatableFieldsEquals(returnedTournament, getPersistedTournament(returnedTournament));

        insertedTournament = returnedTournament;
    }

    @Test
    @Transactional
    void createTournamentWithExistingId() throws Exception {
        // Create the Tournament with an existing ID
        tournament.setId(1L);
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tournament.setName(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tournament.setStatus(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTournaments() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.saveAndFlush(tournament);

        // Get all the tournamentList
        restTournamentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tournament.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].additionalInfo").value(hasItem(DEFAULT_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].ends").value(hasItem(DEFAULT_ENDS.toString())));
    }

    @Test
    @Transactional
    void getTournament() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.saveAndFlush(tournament);

        // Get the tournament
        restTournamentMockMvc
            .perform(get(ENTITY_API_URL_ID, tournament.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tournament.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.additionalInfo").value(DEFAULT_ADDITIONAL_INFO))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.ends").value(DEFAULT_ENDS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTournament() throws Exception {
        // Get the tournament
        restTournamentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTournament() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.saveAndFlush(tournament);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tournament
        Tournament updatedTournament = tournamentRepository.findById(tournament.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTournament are not directly saved in db
        em.detach(updatedTournament);
        updatedTournament
            .name(UPDATED_NAME)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .status(UPDATED_STATUS)
            .start(UPDATED_START)
            .ends(UPDATED_ENDS);
        TournamentDTO tournamentDTO = tournamentMapper.toDto(updatedTournament);

        restTournamentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tournamentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tournamentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTournamentToMatchAllProperties(updatedTournament);
    }

    @Test
    @Transactional
    void putNonExistingTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tournament.setId(longCount.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tournamentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tournament.setId(longCount.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tournament.setId(longCount.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tournamentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTournamentWithPatch() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.saveAndFlush(tournament);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tournament using partial update
        Tournament partialUpdatedTournament = new Tournament();
        partialUpdatedTournament.setId(tournament.getId());

        partialUpdatedTournament.start(UPDATED_START).ends(UPDATED_ENDS);

        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTournament.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTournament))
            )
            .andExpect(status().isOk());

        // Validate the Tournament in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTournamentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTournament, tournament),
            getPersistedTournament(tournament)
        );
    }

    @Test
    @Transactional
    void fullUpdateTournamentWithPatch() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.saveAndFlush(tournament);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tournament using partial update
        Tournament partialUpdatedTournament = new Tournament();
        partialUpdatedTournament.setId(tournament.getId());

        partialUpdatedTournament
            .name(UPDATED_NAME)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .status(UPDATED_STATUS)
            .start(UPDATED_START)
            .ends(UPDATED_ENDS);

        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTournament.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTournament))
            )
            .andExpect(status().isOk());

        // Validate the Tournament in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTournamentUpdatableFieldsEquals(partialUpdatedTournament, getPersistedTournament(partialUpdatedTournament));
    }

    @Test
    @Transactional
    void patchNonExistingTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tournament.setId(longCount.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tournamentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tournament.setId(longCount.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tournament.setId(longCount.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tournamentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTournament() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.saveAndFlush(tournament);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tournament
        restTournamentMockMvc
            .perform(delete(ENTITY_API_URL_ID, tournament.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tournamentRepository.count();
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

    protected Tournament getPersistedTournament(Tournament tournament) {
        return tournamentRepository.findById(tournament.getId()).orElseThrow();
    }

    protected void assertPersistedTournamentToMatchAllProperties(Tournament expectedTournament) {
        assertTournamentAllPropertiesEquals(expectedTournament, getPersistedTournament(expectedTournament));
    }

    protected void assertPersistedTournamentToMatchUpdatableProperties(Tournament expectedTournament) {
        assertTournamentAllUpdatablePropertiesEquals(expectedTournament, getPersistedTournament(expectedTournament));
    }
}
