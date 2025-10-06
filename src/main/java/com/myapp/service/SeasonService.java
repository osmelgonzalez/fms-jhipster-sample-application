package com.myapp.service;

import com.myapp.domain.Season;
import com.myapp.repository.SeasonRepository;
import com.myapp.service.dto.SeasonDTO;
import com.myapp.service.mapper.SeasonMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.myapp.domain.Season}.
 */
@Service
@Transactional
public class SeasonService {

    private static final Logger LOG = LoggerFactory.getLogger(SeasonService.class);

    private final SeasonRepository seasonRepository;

    private final SeasonMapper seasonMapper;

    public SeasonService(SeasonRepository seasonRepository, SeasonMapper seasonMapper) {
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
    }

    /**
     * Save a season.
     *
     * @param seasonDTO the entity to save.
     * @return the persisted entity.
     */
    public SeasonDTO save(SeasonDTO seasonDTO) {
        LOG.debug("Request to save Season : {}", seasonDTO);
        Season season = seasonMapper.toEntity(seasonDTO);
        season = seasonRepository.save(season);
        return seasonMapper.toDto(season);
    }

    /**
     * Update a season.
     *
     * @param seasonDTO the entity to save.
     * @return the persisted entity.
     */
    public SeasonDTO update(SeasonDTO seasonDTO) {
        LOG.debug("Request to update Season : {}", seasonDTO);
        Season season = seasonMapper.toEntity(seasonDTO);
        season = seasonRepository.save(season);
        return seasonMapper.toDto(season);
    }

    /**
     * Partially update a season.
     *
     * @param seasonDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SeasonDTO> partialUpdate(SeasonDTO seasonDTO) {
        LOG.debug("Request to partially update Season : {}", seasonDTO);

        return seasonRepository
            .findById(seasonDTO.getId())
            .map(existingSeason -> {
                seasonMapper.partialUpdate(existingSeason, seasonDTO);

                return existingSeason;
            })
            .map(seasonRepository::save)
            .map(seasonMapper::toDto);
    }

    /**
     * Get all the seasons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SeasonDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Seasons");
        return seasonRepository.findAll(pageable).map(seasonMapper::toDto);
    }

    /**
     * Get all the seasons with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SeasonDTO> findAllWithEagerRelationships(Pageable pageable) {
        return seasonRepository.findAllWithEagerRelationships(pageable).map(seasonMapper::toDto);
    }

    /**
     * Get one season by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SeasonDTO> findOne(Long id) {
        LOG.debug("Request to get Season : {}", id);
        return seasonRepository.findOneWithEagerRelationships(id).map(seasonMapper::toDto);
    }

    /**
     * Delete the season by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Season : {}", id);
        seasonRepository.deleteById(id);
    }
}
