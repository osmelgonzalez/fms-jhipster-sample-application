package com.myapp.service;

import com.myapp.domain.Player;
import com.myapp.repository.PlayerRepository;
import com.myapp.service.dto.PlayerDTO;
import com.myapp.service.mapper.PlayerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.myapp.domain.Player}.
 */
@Service
@Transactional
public class PlayerService {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    public PlayerService(PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    /**
     * Save a player.
     *
     * @param playerDTO the entity to save.
     * @return the persisted entity.
     */
    public PlayerDTO save(PlayerDTO playerDTO) {
        LOG.debug("Request to save Player : {}", playerDTO);
        Player player = playerMapper.toEntity(playerDTO);
        player = playerRepository.save(player);
        return playerMapper.toDto(player);
    }

    /**
     * Update a player.
     *
     * @param playerDTO the entity to save.
     * @return the persisted entity.
     */
    public PlayerDTO update(PlayerDTO playerDTO) {
        LOG.debug("Request to update Player : {}", playerDTO);
        Player player = playerMapper.toEntity(playerDTO);
        player = playerRepository.save(player);
        return playerMapper.toDto(player);
    }

    /**
     * Partially update a player.
     *
     * @param playerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PlayerDTO> partialUpdate(PlayerDTO playerDTO) {
        LOG.debug("Request to partially update Player : {}", playerDTO);

        return playerRepository
            .findById(playerDTO.getId())
            .map(existingPlayer -> {
                playerMapper.partialUpdate(existingPlayer, playerDTO);

                return existingPlayer;
            })
            .map(playerRepository::save)
            .map(playerMapper::toDto);
    }

    /**
     * Get all the players.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PlayerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Players");
        return playerRepository.findAll(pageable).map(playerMapper::toDto);
    }

    /**
     * Get all the players with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PlayerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return playerRepository.findAllWithEagerRelationships(pageable).map(playerMapper::toDto);
    }

    /**
     * Get one player by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findOne(Long id) {
        LOG.debug("Request to get Player : {}", id);
        return playerRepository.findOneWithEagerRelationships(id).map(playerMapper::toDto);
    }

    /**
     * Delete the player by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Player : {}", id);
        playerRepository.deleteById(id);
    }
}
