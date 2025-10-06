package com.myapp.service;

import com.myapp.domain.Checkin;
import com.myapp.repository.CheckinRepository;
import com.myapp.service.dto.CheckinDTO;
import com.myapp.service.mapper.CheckinMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.myapp.domain.Checkin}.
 */
@Service
@Transactional
public class CheckinService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckinService.class);

    private final CheckinRepository checkinRepository;

    private final CheckinMapper checkinMapper;

    public CheckinService(CheckinRepository checkinRepository, CheckinMapper checkinMapper) {
        this.checkinRepository = checkinRepository;
        this.checkinMapper = checkinMapper;
    }

    /**
     * Save a checkin.
     *
     * @param checkinDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckinDTO save(CheckinDTO checkinDTO) {
        LOG.debug("Request to save Checkin : {}", checkinDTO);
        Checkin checkin = checkinMapper.toEntity(checkinDTO);
        checkin = checkinRepository.save(checkin);
        return checkinMapper.toDto(checkin);
    }

    /**
     * Update a checkin.
     *
     * @param checkinDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckinDTO update(CheckinDTO checkinDTO) {
        LOG.debug("Request to update Checkin : {}", checkinDTO);
        Checkin checkin = checkinMapper.toEntity(checkinDTO);
        checkin = checkinRepository.save(checkin);
        return checkinMapper.toDto(checkin);
    }

    /**
     * Partially update a checkin.
     *
     * @param checkinDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CheckinDTO> partialUpdate(CheckinDTO checkinDTO) {
        LOG.debug("Request to partially update Checkin : {}", checkinDTO);

        return checkinRepository
            .findById(checkinDTO.getId())
            .map(existingCheckin -> {
                checkinMapper.partialUpdate(existingCheckin, checkinDTO);

                return existingCheckin;
            })
            .map(checkinRepository::save)
            .map(checkinMapper::toDto);
    }

    /**
     * Get all the checkins.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CheckinDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Checkins");
        return checkinRepository.findAll(pageable).map(checkinMapper::toDto);
    }

    /**
     * Get one checkin by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CheckinDTO> findOne(Long id) {
        LOG.debug("Request to get Checkin : {}", id);
        return checkinRepository.findById(id).map(checkinMapper::toDto);
    }

    /**
     * Delete the checkin by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Checkin : {}", id);
        checkinRepository.deleteById(id);
    }
}
