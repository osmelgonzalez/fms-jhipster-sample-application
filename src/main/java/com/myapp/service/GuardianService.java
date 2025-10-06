package com.myapp.service;

import com.myapp.domain.Guardian;
import com.myapp.repository.GuardianRepository;
import com.myapp.service.dto.GuardianDTO;
import com.myapp.service.mapper.GuardianMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.myapp.domain.Guardian}.
 */
@Service
@Transactional
public class GuardianService {

    private static final Logger LOG = LoggerFactory.getLogger(GuardianService.class);

    private final GuardianRepository guardianRepository;

    private final GuardianMapper guardianMapper;

    public GuardianService(GuardianRepository guardianRepository, GuardianMapper guardianMapper) {
        this.guardianRepository = guardianRepository;
        this.guardianMapper = guardianMapper;
    }

    /**
     * Save a guardian.
     *
     * @param guardianDTO the entity to save.
     * @return the persisted entity.
     */
    public GuardianDTO save(GuardianDTO guardianDTO) {
        LOG.debug("Request to save Guardian : {}", guardianDTO);
        Guardian guardian = guardianMapper.toEntity(guardianDTO);
        guardian = guardianRepository.save(guardian);
        return guardianMapper.toDto(guardian);
    }

    /**
     * Update a guardian.
     *
     * @param guardianDTO the entity to save.
     * @return the persisted entity.
     */
    public GuardianDTO update(GuardianDTO guardianDTO) {
        LOG.debug("Request to update Guardian : {}", guardianDTO);
        Guardian guardian = guardianMapper.toEntity(guardianDTO);
        guardian = guardianRepository.save(guardian);
        return guardianMapper.toDto(guardian);
    }

    /**
     * Partially update a guardian.
     *
     * @param guardianDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GuardianDTO> partialUpdate(GuardianDTO guardianDTO) {
        LOG.debug("Request to partially update Guardian : {}", guardianDTO);

        return guardianRepository
            .findById(guardianDTO.getId())
            .map(existingGuardian -> {
                guardianMapper.partialUpdate(existingGuardian, guardianDTO);

                return existingGuardian;
            })
            .map(guardianRepository::save)
            .map(guardianMapper::toDto);
    }

    /**
     * Get all the guardians.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<GuardianDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Guardians");
        return guardianRepository.findAll(pageable).map(guardianMapper::toDto);
    }

    /**
     * Get one guardian by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GuardianDTO> findOne(Long id) {
        LOG.debug("Request to get Guardian : {}", id);
        return guardianRepository.findById(id).map(guardianMapper::toDto);
    }

    /**
     * Delete the guardian by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Guardian : {}", id);
        guardianRepository.deleteById(id);
    }
}
