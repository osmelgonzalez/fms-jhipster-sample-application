package com.myapp.web.rest;

import static com.myapp.domain.TeamAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.Team;
import com.myapp.repository.TeamRepository;
import com.myapp.service.TeamService;
import com.myapp.service.dto.TeamDTO;
import com.myapp.service.mapper.TeamMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link TeamResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TeamResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamRepository teamRepository;

    @Mock
    private TeamRepository teamRepositoryMock;

    @Autowired
    private TeamMapper teamMapper;

    @Mock
    private TeamService teamServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamMockMvc;

    private Team team;

    private Team insertedTeam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createEntity() {
        return new Team().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createUpdatedEntity() {
        return new Team().name(UPDATED_NAME);
    }

    @BeforeEach
    void initTest() {
        team = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTeam != null) {
            teamRepository.delete(insertedTeam);
            insertedTeam = null;
        }
    }

    @Test
    @Transactional
    void createTeam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);
        var returnedTeamDTO = om.readValue(
            restTeamMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TeamDTO.class
        );

        // Validate the Team in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeam = teamMapper.toEntity(returnedTeamDTO);
        assertTeamUpdatableFieldsEquals(returnedTeam, getPersistedTeam(returnedTeam));

        insertedTeam = returnedTeam;
    }

    @Test
    @Transactional
    void createTeamWithExistingId() throws Exception {
        // Create the Team with an existing ID
        team.setId(1L);
        TeamDTO teamDTO = teamMapper.toDto(team);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTeams() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.saveAndFlush(team);

        // Get all the teamList
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(team.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamsWithEagerRelationshipsIsEnabled() throws Exception {
        when(teamServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teamServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(teamServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(teamRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTeam() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.saveAndFlush(team);

        // Get the team
        restTeamMockMvc
            .perform(get(ENTITY_API_URL_ID, team.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(team.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingTeam() throws Exception {
        // Get the team
        restTeamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeam() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.saveAndFlush(team);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team
        Team updatedTeam = teamRepository.findById(team.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTeam are not directly saved in db
        em.detach(updatedTeam);
        updatedTeam.name(UPDATED_NAME);
        TeamDTO teamDTO = teamMapper.toDto(updatedTeam);

        restTeamMockMvc
            .perform(put(ENTITY_API_URL_ID, teamDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamDTO)))
            .andExpect(status().isOk());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamToMatchAllProperties(updatedTeam);
    }

    @Test
    @Transactional
    void putNonExistingTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(put(ENTITY_API_URL_ID, teamDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.saveAndFlush(team);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam.name(UPDATED_NAME);

        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeam))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTeam, team), getPersistedTeam(team));
    }

    @Test
    @Transactional
    void fullUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.saveAndFlush(team);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam.name(UPDATED_NAME);

        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeam))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamUpdatableFieldsEquals(partialUpdatedTeam, getPersistedTeam(partialUpdatedTeam));
    }

    @Test
    @Transactional
    void patchNonExistingTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(teamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(teamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeam() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.saveAndFlush(team);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the team
        restTeamMockMvc
            .perform(delete(ENTITY_API_URL_ID, team.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teamRepository.count();
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

    protected Team getPersistedTeam(Team team) {
        return teamRepository.findById(team.getId()).orElseThrow();
    }

    protected void assertPersistedTeamToMatchAllProperties(Team expectedTeam) {
        assertTeamAllPropertiesEquals(expectedTeam, getPersistedTeam(expectedTeam));
    }

    protected void assertPersistedTeamToMatchUpdatableProperties(Team expectedTeam) {
        assertTeamAllUpdatablePropertiesEquals(expectedTeam, getPersistedTeam(expectedTeam));
    }
}
