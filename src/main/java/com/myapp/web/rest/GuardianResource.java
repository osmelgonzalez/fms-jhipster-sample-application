package com.myapp.web.rest;

import com.myapp.repository.GuardianRepository;
import com.myapp.service.GuardianService;
import com.myapp.service.dto.GuardianDTO;
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
 * REST controller for managing {@link com.myapp.domain.Guardian}.
 */
@RestController
@RequestMapping("/api/guardians")
public class GuardianResource {

    private static final Logger LOG = LoggerFactory.getLogger(GuardianResource.class);

    private static final String ENTITY_NAME = "guardian";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GuardianService guardianService;

    private final GuardianRepository guardianRepository;

    public GuardianResource(GuardianService guardianService, GuardianRepository guardianRepository) {
        this.guardianService = guardianService;
        this.guardianRepository = guardianRepository;
    }

    /**
     * {@code POST  /guardians} : Create a new guardian.
     *
     * @param guardianDTO the guardianDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new guardianDTO, or with status {@code 400 (Bad Request)} if the guardian has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GuardianDTO> createGuardian(@Valid @RequestBody GuardianDTO guardianDTO) throws URISyntaxException {
        LOG.debug("REST request to save Guardian : {}", guardianDTO);
        if (guardianDTO.getId() != null) {
            throw new BadRequestAlertException("A new guardian cannot already have an ID", ENTITY_NAME, "idexists");
        }
        guardianDTO = guardianService.save(guardianDTO);
        return ResponseEntity.created(new URI("/api/guardians/" + guardianDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, guardianDTO.getId().toString()))
            .body(guardianDTO);
    }

    /**
     * {@code PUT  /guardians/:id} : Updates an existing guardian.
     *
     * @param id the id of the guardianDTO to save.
     * @param guardianDTO the guardianDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guardianDTO,
     * or with status {@code 400 (Bad Request)} if the guardianDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the guardianDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GuardianDTO> updateGuardian(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GuardianDTO guardianDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Guardian : {}, {}", id, guardianDTO);
        if (guardianDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, guardianDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!guardianRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        guardianDTO = guardianService.update(guardianDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, guardianDTO.getId().toString()))
            .body(guardianDTO);
    }

    /**
     * {@code PATCH  /guardians/:id} : Partial updates given fields of an existing guardian, field will ignore if it is null
     *
     * @param id the id of the guardianDTO to save.
     * @param guardianDTO the guardianDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guardianDTO,
     * or with status {@code 400 (Bad Request)} if the guardianDTO is not valid,
     * or with status {@code 404 (Not Found)} if the guardianDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the guardianDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GuardianDTO> partialUpdateGuardian(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GuardianDTO guardianDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Guardian partially : {}, {}", id, guardianDTO);
        if (guardianDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, guardianDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!guardianRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GuardianDTO> result = guardianService.partialUpdate(guardianDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, guardianDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /guardians} : get all the guardians.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of guardians in body.
     */
    @GetMapping("")
    public ResponseEntity<List<GuardianDTO>> getAllGuardians(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Guardians");
        Page<GuardianDTO> page = guardianService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /guardians/:id} : get the "id" guardian.
     *
     * @param id the id of the guardianDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the guardianDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GuardianDTO> getGuardian(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Guardian : {}", id);
        Optional<GuardianDTO> guardianDTO = guardianService.findOne(id);
        return ResponseUtil.wrapOrNotFound(guardianDTO);
    }

    /**
     * {@code DELETE  /guardians/:id} : delete the "id" guardian.
     *
     * @param id the id of the guardianDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuardian(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Guardian : {}", id);
        guardianService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
