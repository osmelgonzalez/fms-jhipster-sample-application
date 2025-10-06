package com.myapp.web.rest;

import com.myapp.repository.CampRepository;
import com.myapp.service.CampService;
import com.myapp.service.dto.CampDTO;
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
 * REST controller for managing {@link com.myapp.domain.Camp}.
 */
@RestController
@RequestMapping("/api/camps")
public class CampResource {

    private static final Logger LOG = LoggerFactory.getLogger(CampResource.class);

    private static final String ENTITY_NAME = "camp";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CampService campService;

    private final CampRepository campRepository;

    public CampResource(CampService campService, CampRepository campRepository) {
        this.campService = campService;
        this.campRepository = campRepository;
    }

    /**
     * {@code POST  /camps} : Create a new camp.
     *
     * @param campDTO the campDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new campDTO, or with status {@code 400 (Bad Request)} if the camp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CampDTO> createCamp(@Valid @RequestBody CampDTO campDTO) throws URISyntaxException {
        LOG.debug("REST request to save Camp : {}", campDTO);
        if (campDTO.getId() != null) {
            throw new BadRequestAlertException("A new camp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        campDTO = campService.save(campDTO);
        return ResponseEntity.created(new URI("/api/camps/" + campDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, campDTO.getId().toString()))
            .body(campDTO);
    }

    /**
     * {@code PUT  /camps/:id} : Updates an existing camp.
     *
     * @param id the id of the campDTO to save.
     * @param campDTO the campDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated campDTO,
     * or with status {@code 400 (Bad Request)} if the campDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the campDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CampDTO> updateCamp(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CampDTO campDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Camp : {}, {}", id, campDTO);
        if (campDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, campDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!campRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        campDTO = campService.update(campDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, campDTO.getId().toString()))
            .body(campDTO);
    }

    /**
     * {@code PATCH  /camps/:id} : Partial updates given fields of an existing camp, field will ignore if it is null
     *
     * @param id the id of the campDTO to save.
     * @param campDTO the campDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated campDTO,
     * or with status {@code 400 (Bad Request)} if the campDTO is not valid,
     * or with status {@code 404 (Not Found)} if the campDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the campDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CampDTO> partialUpdateCamp(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CampDTO campDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Camp partially : {}, {}", id, campDTO);
        if (campDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, campDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!campRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CampDTO> result = campService.partialUpdate(campDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, campDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /camps} : get all the camps.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of camps in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CampDTO>> getAllCamps(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Camps");
        Page<CampDTO> page = campService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /camps/:id} : get the "id" camp.
     *
     * @param id the id of the campDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the campDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampDTO> getCamp(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Camp : {}", id);
        Optional<CampDTO> campDTO = campService.findOne(id);
        return ResponseUtil.wrapOrNotFound(campDTO);
    }

    /**
     * {@code DELETE  /camps/:id} : delete the "id" camp.
     *
     * @param id the id of the campDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCamp(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Camp : {}", id);
        campService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
