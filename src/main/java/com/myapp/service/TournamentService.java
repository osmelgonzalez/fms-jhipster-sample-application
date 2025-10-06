package com.myapp.service;

import com.myapp.domain.Tournament;
import com.myapp.repository.TournamentRepository;
import com.myapp.service.dto.TournamentDTO;
import com.myapp.service.mapper.TournamentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.myapp.domain.Tournament}.
 */
@Service
@Transactional
public class TournamentService {

    private static final Logger LOG = LoggerFactory.getLogger(TournamentService.class);

    private final TournamentRepository tournamentRepository;

    private final TournamentMapper tournamentMapper;

    public TournamentService(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
    }

    /**
     * Save a tournament.
     *
     * @param tournamentDTO the entity to save.
     * @return the persisted entity.
     */
    public TournamentDTO save(TournamentDTO tournamentDTO) {
        LOG.debug("Request to save Tournament : {}", tournamentDTO);
        Tournament tournament = tournamentMapper.toEntity(tournamentDTO);
        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toDto(tournament);
    }

    /**
     * Update a tournament.
     *
     * @param tournamentDTO the entity to save.
     * @return the persisted entity.
     */
    public TournamentDTO update(TournamentDTO tournamentDTO) {
        LOG.debug("Request to update Tournament : {}", tournamentDTO);
        Tournament tournament = tournamentMapper.toEntity(tournamentDTO);
        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toDto(tournament);
    }

    /**
     * Partially update a tournament.
     *
     * @param tournamentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TournamentDTO> partialUpdate(TournamentDTO tournamentDTO) {
        LOG.debug("Request to partially update Tournament : {}", tournamentDTO);

        return tournamentRepository
            .findById(tournamentDTO.getId())
            .map(existingTournament -> {
                tournamentMapper.partialUpdate(existingTournament, tournamentDTO);

                return existingTournament;
            })
            .map(tournamentRepository::save)
            .map(tournamentMapper::toDto);
    }

    /**
     * Get all the tournaments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TournamentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Tournaments");
        return tournamentRepository.findAll(pageable).map(tournamentMapper::toDto);
    }

    /**
     * Get one tournament by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TournamentDTO> findOne(Long id) {
        LOG.debug("Request to get Tournament : {}", id);
        return tournamentRepository.findById(id).map(tournamentMapper::toDto);
    }

    /**
     * Delete the tournament by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Tournament : {}", id);
        tournamentRepository.deleteById(id);
    }
}
