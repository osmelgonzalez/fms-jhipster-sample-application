package com.myapp.web.rest;

import com.myapp.repository.FileDataRepository;
import com.myapp.service.FileDataService;
import com.myapp.service.dto.FileDataDTO;
import com.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.myapp.domain.FileData}.
 */
@RestController
@RequestMapping("/api/file-data")
public class FileDataResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileDataResource.class);

    private static final String ENTITY_NAME = "fileData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileDataService fileDataService;

    private final FileDataRepository fileDataRepository;

    public FileDataResource(FileDataService fileDataService, FileDataRepository fileDataRepository) {
        this.fileDataService = fileDataService;
        this.fileDataRepository = fileDataRepository;
    }

    /**
     * {@code POST  /file-data} : Create a new fileData.
     *
     * @param fileDataDTO the fileDataDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileDataDTO, or with status {@code 400 (Bad Request)} if the fileData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FileDataDTO> createFileData(@Valid @RequestBody FileDataDTO fileDataDTO) throws URISyntaxException {
        LOG.debug("REST request to save FileData : {}", fileDataDTO);
        if (fileDataDTO.getId() != null) {
            throw new BadRequestAlertException("A new fileData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        fileDataDTO = fileDataService.save(fileDataDTO);
        return ResponseEntity.created(new URI("/api/file-data/" + fileDataDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, fileDataDTO.getId().toString()))
            .body(fileDataDTO);
    }

    /**
     * {@code PUT  /file-data/:id} : Updates an existing fileData.
     *
     * @param id the id of the fileDataDTO to save.
     * @param fileDataDTO the fileDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileDataDTO,
     * or with status {@code 400 (Bad Request)} if the fileDataDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FileDataDTO> updateFileData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FileDataDTO fileDataDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FileData : {}, {}", id, fileDataDTO);
        if (fileDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fileDataDTO = fileDataService.update(fileDataDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileDataDTO.getId().toString()))
            .body(fileDataDTO);
    }

    /**
     * {@code PATCH  /file-data/:id} : Partial updates given fields of an existing fileData, field will ignore if it is null
     *
     * @param id the id of the fileDataDTO to save.
     * @param fileDataDTO the fileDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileDataDTO,
     * or with status {@code 400 (Bad Request)} if the fileDataDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fileDataDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fileDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FileDataDTO> partialUpdateFileData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FileDataDTO fileDataDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FileData partially : {}, {}", id, fileDataDTO);
        if (fileDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FileDataDTO> result = fileDataService.partialUpdate(fileDataDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileDataDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /file-data} : get all the fileData.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileData in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FileDataDTO>> getAllFileData(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("tournament-is-null".equals(filter)) {
            LOG.debug("REST request to get all FileDatas where tournament is null");
            return new ResponseEntity<>(fileDataService.findAllWhereTournamentIsNull(), HttpStatus.OK);
        }

        if ("season-is-null".equals(filter)) {
            LOG.debug("REST request to get all FileDatas where season is null");
            return new ResponseEntity<>(fileDataService.findAllWhereSeasonIsNull(), HttpStatus.OK);
        }

        if ("camp-is-null".equals(filter)) {
            LOG.debug("REST request to get all FileDatas where camp is null");
            return new ResponseEntity<>(fileDataService.findAllWhereCampIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of FileData");
        Page<FileDataDTO> page = fileDataService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /file-data/:id} : get the "id" fileData.
     *
     * @param id the id of the fileDataDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileDataDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileDataDTO> getFileData(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FileData : {}", id);
        Optional<FileDataDTO> fileDataDTO = fileDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileDataDTO);
    }

    /**
     * {@code DELETE  /file-data/:id} : delete the "id" fileData.
     *
     * @param id the id of the fileDataDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFileData(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FileData : {}", id);
        fileDataService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
