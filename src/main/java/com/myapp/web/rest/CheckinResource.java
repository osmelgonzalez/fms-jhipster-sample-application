package com.myapp.web.rest;

import com.myapp.repository.CheckinRepository;
import com.myapp.service.CheckinService;
import com.myapp.service.dto.CheckinDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.myapp.domain.Checkin}.
 */
@RestController
@RequestMapping("/api/checkins")
public class CheckinResource {

    private static final Logger LOG = LoggerFactory.getLogger(CheckinResource.class);

    private static final String ENTITY_NAME = "checkin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CheckinService checkinService;

    private final CheckinRepository checkinRepository;

    public CheckinResource(CheckinService checkinService, CheckinRepository checkinRepository) {
        this.checkinService = checkinService;
        this.checkinRepository = checkinRepository;
    }

    /**
     * {@code POST  /checkins} : Create a new checkin.
     *
     * @param checkinDTO the checkinDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new checkinDTO, or with status {@code 400 (Bad Request)} if the checkin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CheckinDTO> createCheckin(@Valid @RequestBody CheckinDTO checkinDTO) throws URISyntaxException {
        LOG.debug("REST request to save Checkin : {}", checkinDTO);
        if (checkinDTO.getId() != null) {
            throw new BadRequestAlertException("A new checkin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        checkinDTO = checkinService.save(checkinDTO);
        return ResponseEntity.created(new URI("/api/checkins/" + checkinDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, checkinDTO.getId().toString()))
            .body(checkinDTO);
    }

    /**
     * {@code PUT  /checkins/:id} : Updates an existing checkin.
     *
     * @param id the id of the checkinDTO to save.
     * @param checkinDTO the checkinDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkinDTO,
     * or with status {@code 400 (Bad Request)} if the checkinDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the checkinDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CheckinDTO> updateCheckin(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CheckinDTO checkinDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Checkin : {}, {}", id, checkinDTO);
        if (checkinDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkinDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        checkinDTO = checkinService.update(checkinDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, checkinDTO.getId().toString()))
            .body(checkinDTO);
    }

    /**
     * {@code PATCH  /checkins/:id} : Partial updates given fields of an existing checkin, field will ignore if it is null
     *
     * @param id the id of the checkinDTO to save.
     * @param checkinDTO the checkinDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkinDTO,
     * or with status {@code 400 (Bad Request)} if the checkinDTO is not valid,
     * or with status {@code 404 (Not Found)} if the checkinDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the checkinDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CheckinDTO> partialUpdateCheckin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CheckinDTO checkinDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Checkin partially : {}, {}", id, checkinDTO);
        if (checkinDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkinDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CheckinDTO> result = checkinService.partialUpdate(checkinDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, checkinDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /checkins} : get all the checkins.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of checkins in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CheckinDTO>> getAllCheckins(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Checkins");
        Page<CheckinDTO> page = checkinService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /checkins/:id} : get the "id" checkin.
     *
     * @param id the id of the checkinDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the checkinDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CheckinDTO> getCheckin(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Checkin : {}", id);
        Optional<CheckinDTO> checkinDTO = checkinService.findOne(id);
        return ResponseUtil.wrapOrNotFound(checkinDTO);
    }

    /**
     * {@code DELETE  /checkins/:id} : delete the "id" checkin.
     *
     * @param id the id of the checkinDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheckin(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Checkin : {}", id);
        checkinService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
