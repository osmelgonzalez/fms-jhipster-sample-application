package com.myapp.web.rest;

import static com.myapp.domain.PlayerAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.Player;
import com.myapp.domain.enumeration.Gender;
import com.myapp.repository.PlayerRepository;
import com.myapp.service.PlayerService;
import com.myapp.service.dto.PlayerDTO;
import com.myapp.service.mapper.PlayerMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PlayerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlayerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MIDDLE_INITIAL = "A";
    private static final String UPDATED_MIDDLE_INITIAL = "B";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/players";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlayerRepository playerRepository;

    @Mock
    private PlayerRepository playerRepositoryMock;

    @Autowired
    private PlayerMapper playerMapper;

    @Mock
    private PlayerService playerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlayerMockMvc;

    private Player player;

    private Player insertedPlayer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createEntity() {
        return new Player()
            .firstName(DEFAULT_FIRST_NAME)
            .middleInitial(DEFAULT_MIDDLE_INITIAL)
            .lastName(DEFAULT_LAST_NAME)
            .gender(DEFAULT_GENDER)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createUpdatedEntity() {
        return new Player()
            .firstName(UPDATED_FIRST_NAME)
            .middleInitial(UPDATED_MIDDLE_INITIAL)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);
    }

    @BeforeEach
    void initTest() {
        player = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPlayer != null) {
            playerRepository.delete(insertedPlayer);
            insertedPlayer = null;
        }
    }

    @Test
    @Transactional
    void createPlayer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);
        var returnedPlayerDTO = om.readValue(
            restPlayerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PlayerDTO.class
        );

        // Validate the Player in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlayer = playerMapper.toEntity(returnedPlayerDTO);
        assertPlayerUpdatableFieldsEquals(returnedPlayer, getPersistedPlayer(returnedPlayer));

        insertedPlayer = returnedPlayer;
    }

    @Test
    @Transactional
    void createPlayerWithExistingId() throws Exception {
        // Create the Player with an existing ID
        player.setId(1L);
        PlayerDTO playerDTO = playerMapper.toDto(player);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        player.setFirstName(null);

        // Create the Player, which fails.
        PlayerDTO playerDTO = playerMapper.toDto(player);

        restPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        player.setLastName(null);

        // Create the Player, which fails.
        PlayerDTO playerDTO = playerMapper.toDto(player);

        restPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlayers() throws Exception {
        // Initialize the database
        insertedPlayer = playerRepository.saveAndFlush(player);

        // Get all the playerList
        restPlayerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(player.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].middleInitial").value(hasItem(DEFAULT_MIDDLE_INITIAL)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlayersWithEagerRelationshipsIsEnabled() throws Exception {
        when(playerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlayerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(playerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlayersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(playerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlayerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(playerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPlayer() throws Exception {
        // Initialize the database
        insertedPlayer = playerRepository.saveAndFlush(player);

        // Get the player
        restPlayerMockMvc
            .perform(get(ENTITY_API_URL_ID, player.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(player.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.middleInitial").value(DEFAULT_MIDDLE_INITIAL))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPlayer() throws Exception {
        // Get the player
        restPlayerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlayer() throws Exception {
        // Initialize the database
        insertedPlayer = playerRepository.saveAndFlush(player);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the player
        Player updatedPlayer = playerRepository.findById(player.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPlayer are not directly saved in db
        em.detach(updatedPlayer);
        updatedPlayer
            .firstName(UPDATED_FIRST_NAME)
            .middleInitial(UPDATED_MIDDLE_INITIAL)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);
        PlayerDTO playerDTO = playerMapper.toDto(updatedPlayer);

        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlayerToMatchAllProperties(updatedPlayer);
    }

    @Test
    @Transactional
    void putNonExistingPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        insertedPlayer = playerRepository.saveAndFlush(player);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer.middleInitial(UPDATED_MIDDLE_INITIAL).lastName(UPDATED_LAST_NAME).dateOfBirth(UPDATED_DATE_OF_BIRTH);

        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlayer))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPlayer, player), getPersistedPlayer(player));
    }

    @Test
    @Transactional
    void fullUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        insertedPlayer = playerRepository.saveAndFlush(player);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer
            .firstName(UPDATED_FIRST_NAME)
            .middleInitial(UPDATED_MIDDLE_INITIAL)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);

        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlayer))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerUpdatableFieldsEquals(partialUpdatedPlayer, getPersistedPlayer(partialUpdatedPlayer));
    }

    @Test
    @Transactional
    void patchNonExistingPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, playerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(playerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlayer() throws Exception {
        // Initialize the database
        insertedPlayer = playerRepository.saveAndFlush(player);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the player
        restPlayerMockMvc
            .perform(delete(ENTITY_API_URL_ID, player.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return playerRepository.count();
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

    protected Player getPersistedPlayer(Player player) {
        return playerRepository.findById(player.getId()).orElseThrow();
    }

    protected void assertPersistedPlayerToMatchAllProperties(Player expectedPlayer) {
        assertPlayerAllPropertiesEquals(expectedPlayer, getPersistedPlayer(expectedPlayer));
    }

    protected void assertPersistedPlayerToMatchUpdatableProperties(Player expectedPlayer) {
        assertPlayerAllUpdatablePropertiesEquals(expectedPlayer, getPersistedPlayer(expectedPlayer));
    }
}
