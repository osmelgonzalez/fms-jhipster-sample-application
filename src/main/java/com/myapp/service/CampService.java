package com.myapp.service;

import com.myapp.domain.Camp;
import com.myapp.repository.CampRepository;
import com.myapp.service.dto.CampDTO;
import com.myapp.service.mapper.CampMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.myapp.domain.Camp}.
 */
@Service
@Transactional
public class CampService {

    private static final Logger LOG = LoggerFactory.getLogger(CampService.class);

    private final CampRepository campRepository;

    private final CampMapper campMapper;

    public CampService(CampRepository campRepository, CampMapper campMapper) {
        this.campRepository = campRepository;
        this.campMapper = campMapper;
    }

    /**
     * Save a camp.
     *
     * @param campDTO the entity to save.
     * @return the persisted entity.
     */
    public CampDTO save(CampDTO campDTO) {
        LOG.debug("Request to save Camp : {}", campDTO);
        Camp camp = campMapper.toEntity(campDTO);
        camp = campRepository.save(camp);
        return campMapper.toDto(camp);
    }

    /**
     * Update a camp.
     *
     * @param campDTO the entity to save.
     * @return the persisted entity.
     */
    public CampDTO update(CampDTO campDTO) {
        LOG.debug("Request to update Camp : {}", campDTO);
        Camp camp = campMapper.toEntity(campDTO);
        camp = campRepository.save(camp);
        return campMapper.toDto(camp);
    }

    /**
     * Partially update a camp.
     *
     * @param campDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CampDTO> partialUpdate(CampDTO campDTO) {
        LOG.debug("Request to partially update Camp : {}", campDTO);

        return campRepository
            .findById(campDTO.getId())
            .map(existingCamp -> {
                campMapper.partialUpdate(existingCamp, campDTO);

                return existingCamp;
            })
            .map(campRepository::save)
            .map(campMapper::toDto);
    }

    /**
     * Get all the camps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CampDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Camps");
        return campRepository.findAll(pageable).map(campMapper::toDto);
    }

    /**
     * Get one camp by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CampDTO> findOne(Long id) {
        LOG.debug("Request to get Camp : {}", id);
        return campRepository.findById(id).map(campMapper::toDto);
    }

    /**
     * Delete the camp by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Camp : {}", id);
        campRepository.deleteById(id);
    }
}
