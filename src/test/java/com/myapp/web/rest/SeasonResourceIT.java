package com.myapp.web.rest;

import static com.myapp.domain.SeasonAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.Season;
import com.myapp.domain.enumeration.CompetitionStatus;
import com.myapp.repository.SeasonRepository;
import com.myapp.service.SeasonService;
import com.myapp.service.dto.SeasonDTO;
import com.myapp.service.mapper.SeasonMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SeasonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SeasonResourceIT {

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

    private static final String ENTITY_API_URL = "/api/seasons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SeasonRepository seasonRepository;

    @Mock
    private SeasonRepository seasonRepositoryMock;

    @Autowired
    private SeasonMapper seasonMapper;

    @Mock
    private SeasonService seasonServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSeasonMockMvc;

    private Season season;

    private Season insertedSeason;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Season createEntity() {
        return new Season()
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
    public static Season createUpdatedEntity() {
        return new Season()
            .name(UPDATED_NAME)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .status(UPDATED_STATUS)
            .start(UPDATED_START)
            .ends(UPDATED_ENDS);
    }

    @BeforeEach
    void initTest() {
        season = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSeason != null) {
            seasonRepository.delete(insertedSeason);
            insertedSeason = null;
        }
    }

    @Test
    @Transactional
    void createSeason() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);
        var returnedSeasonDTO = om.readValue(
            restSeasonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SeasonDTO.class
        );

        // Validate the Season in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSeason = seasonMapper.toEntity(returnedSeasonDTO);
        assertSeasonUpdatableFieldsEquals(returnedSeason, getPersistedSeason(returnedSeason));

        insertedSeason = returnedSeason;
    }

    @Test
    @Transactional
    void createSeasonWithExistingId() throws Exception {
        // Create the Season with an existing ID
        season.setId(1L);
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSeasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        season.setName(null);

        // Create the Season, which fails.
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        restSeasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        season.setStatus(null);

        // Create the Season, which fails.
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        restSeasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        season.setStart(null);

        // Create the Season, which fails.
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        restSeasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        season.setEnds(null);

        // Create the Season, which fails.
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        restSeasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSeasons() throws Exception {
        // Initialize the database
        insertedSeason = seasonRepository.saveAndFlush(season);

        // Get all the seasonList
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(season.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].additionalInfo").value(hasItem(DEFAULT_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].ends").value(hasItem(DEFAULT_ENDS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSeasonsWithEagerRelationshipsIsEnabled() throws Exception {
        when(seasonServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSeasonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(seasonServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSeasonsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(seasonServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSeasonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(seasonRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSeason() throws Exception {
        // Initialize the database
        insertedSeason = seasonRepository.saveAndFlush(season);

        // Get the season
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL_ID, season.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(season.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.additionalInfo").value(DEFAULT_ADDITIONAL_INFO))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.ends").value(DEFAULT_ENDS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSeason() throws Exception {
        // Get the season
        restSeasonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSeason() throws Exception {
        // Initialize the database
        insertedSeason = seasonRepository.saveAndFlush(season);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the season
        Season updatedSeason = seasonRepository.findById(season.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSeason are not directly saved in db
        em.detach(updatedSeason);
        updatedSeason
            .name(UPDATED_NAME)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .status(UPDATED_STATUS)
            .start(UPDATED_START)
            .ends(UPDATED_ENDS);
        SeasonDTO seasonDTO = seasonMapper.toDto(updatedSeason);

        restSeasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seasonDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSeasonToMatchAllProperties(updatedSeason);
    }

    @Test
    @Transactional
    void putNonExistingSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seasonDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seasonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSeasonWithPatch() throws Exception {
        // Initialize the database
        insertedSeason = seasonRepository.saveAndFlush(season);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the season using partial update
        Season partialUpdatedSeason = new Season();
        partialUpdatedSeason.setId(season.getId());

        partialUpdatedSeason.start(UPDATED_START);

        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeason.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeason))
            )
            .andExpect(status().isOk());

        // Validate the Season in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeasonUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSeason, season), getPersistedSeason(season));
    }

    @Test
    @Transactional
    void fullUpdateSeasonWithPatch() throws Exception {
        // Initialize the database
        insertedSeason = seasonRepository.saveAndFlush(season);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the season using partial update
        Season partialUpdatedSeason = new Season();
        partialUpdatedSeason.setId(season.getId());

        partialUpdatedSeason
            .name(UPDATED_NAME)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .status(UPDATED_STATUS)
            .start(UPDATED_START)
            .ends(UPDATED_ENDS);

        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeason.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeason))
            )
            .andExpect(status().isOk());

        // Validate the Season in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeasonUpdatableFieldsEquals(partialUpdatedSeason, getPersistedSeason(partialUpdatedSeason));
    }

    @Test
    @Transactional
    void patchNonExistingSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, seasonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(seasonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSeason() throws Exception {
        // Initialize the database
        insertedSeason = seasonRepository.saveAndFlush(season);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the season
        restSeasonMockMvc
            .perform(delete(ENTITY_API_URL_ID, season.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return seasonRepository.count();
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

    protected Season getPersistedSeason(Season season) {
        return seasonRepository.findById(season.getId()).orElseThrow();
    }

    protected void assertPersistedSeasonToMatchAllProperties(Season expectedSeason) {
        assertSeasonAllPropertiesEquals(expectedSeason, getPersistedSeason(expectedSeason));
    }

    protected void assertPersistedSeasonToMatchUpdatableProperties(Season expectedSeason) {
        assertSeasonAllUpdatablePropertiesEquals(expectedSeason, getPersistedSeason(expectedSeason));
    }
}
