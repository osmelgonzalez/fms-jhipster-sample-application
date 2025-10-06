package com.myapp.service;

import com.myapp.domain.FileData;
import com.myapp.repository.FileDataRepository;
import com.myapp.service.dto.FileDataDTO;
import com.myapp.service.mapper.FileDataMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.myapp.domain.FileData}.
 */
@Service
@Transactional
public class FileDataService {

    private static final Logger LOG = LoggerFactory.getLogger(FileDataService.class);

    private final FileDataRepository fileDataRepository;

    private final FileDataMapper fileDataMapper;

    public FileDataService(FileDataRepository fileDataRepository, FileDataMapper fileDataMapper) {
        this.fileDataRepository = fileDataRepository;
        this.fileDataMapper = fileDataMapper;
    }

    /**
     * Save a fileData.
     *
     * @param fileDataDTO the entity to save.
     * @return the persisted entity.
     */
    public FileDataDTO save(FileDataDTO fileDataDTO) {
        LOG.debug("Request to save FileData : {}", fileDataDTO);
        FileData fileData = fileDataMapper.toEntity(fileDataDTO);
        fileData = fileDataRepository.save(fileData);
        return fileDataMapper.toDto(fileData);
    }

    /**
     * Update a fileData.
     *
     * @param fileDataDTO the entity to save.
     * @return the persisted entity.
     */
    public FileDataDTO update(FileDataDTO fileDataDTO) {
        LOG.debug("Request to update FileData : {}", fileDataDTO);
        FileData fileData = fileDataMapper.toEntity(fileDataDTO);
        fileData = fileDataRepository.save(fileData);
        return fileDataMapper.toDto(fileData);
    }

    /**
     * Partially update a fileData.
     *
     * @param fileDataDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FileDataDTO> partialUpdate(FileDataDTO fileDataDTO) {
        LOG.debug("Request to partially update FileData : {}", fileDataDTO);

        return fileDataRepository
            .findById(fileDataDTO.getId())
            .map(existingFileData -> {
                fileDataMapper.partialUpdate(existingFileData, fileDataDTO);

                return existingFileData;
            })
            .map(fileDataRepository::save)
            .map(fileDataMapper::toDto);
    }

    /**
     * Get all the fileData.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FileDataDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FileData");
        return fileDataRepository.findAll(pageable).map(fileDataMapper::toDto);
    }

    /**
     *  Get all the fileData where Tournament is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FileDataDTO> findAllWhereTournamentIsNull() {
        LOG.debug("Request to get all fileData where Tournament is null");
        return StreamSupport.stream(fileDataRepository.findAll().spliterator(), false)
            .filter(fileData -> fileData.getTournament() == null)
            .map(fileDataMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the fileData where Season is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FileDataDTO> findAllWhereSeasonIsNull() {
        LOG.debug("Request to get all fileData where Season is null");
        return StreamSupport.stream(fileDataRepository.findAll().spliterator(), false)
            .filter(fileData -> fileData.getSeason() == null)
            .map(fileDataMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the fileData where Camp is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FileDataDTO> findAllWhereCampIsNull() {
        LOG.debug("Request to get all fileData where Camp is null");
        return StreamSupport.stream(fileDataRepository.findAll().spliterator(), false)
            .filter(fileData -> fileData.getCamp() == null)
            .map(fileDataMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one fileData by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FileDataDTO> findOne(Long id) {
        LOG.debug("Request to get FileData : {}", id);
        return fileDataRepository.findById(id).map(fileDataMapper::toDto);
    }

    /**
     * Delete the fileData by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete FileData : {}", id);
        fileDataRepository.deleteById(id);
    }
}
