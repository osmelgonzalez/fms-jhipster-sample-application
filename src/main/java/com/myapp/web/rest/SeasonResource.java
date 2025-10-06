package com.myapp.web.rest;

import com.myapp.repository.SeasonRepository;
import com.myapp.service.SeasonService;
import com.myapp.service.dto.SeasonDTO;
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
 * REST controller for managing {@link com.myapp.domain.Season}.
 */
@RestController
@RequestMapping("/api/seasons")
public class SeasonResource {

    private static final Logger LOG = LoggerFactory.getLogger(SeasonResource.class);

    private static final String ENTITY_NAME = "season";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SeasonService seasonService;

    private final SeasonRepository seasonRepository;

    public SeasonResource(SeasonService seasonService, SeasonRepository seasonRepository) {
        this.seasonService = seasonService;
        this.seasonRepository = seasonRepository;
    }

    /**
     * {@code POST  /seasons} : Create a new season.
     *
     * @param seasonDTO the seasonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new seasonDTO, or with status {@code 400 (Bad Request)} if the season has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SeasonDTO> createSeason(@Valid @RequestBody SeasonDTO seasonDTO) throws URISyntaxException {
        LOG.debug("REST request to save Season : {}", seasonDTO);
        if (seasonDTO.getId() != null) {
            throw new BadRequestAlertException("A new season cannot already have an ID", ENTITY_NAME, "idexists");
        }
        seasonDTO = seasonService.save(seasonDTO);
        return ResponseEntity.created(new URI("/api/seasons/" + seasonDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, seasonDTO.getId().toString()))
            .body(seasonDTO);
    }

    /**
     * {@code PUT  /seasons/:id} : Updates an existing season.
     *
     * @param id the id of the seasonDTO to save.
     * @param seasonDTO the seasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seasonDTO,
     * or with status {@code 400 (Bad Request)} if the seasonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the seasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SeasonDTO> updateSeason(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SeasonDTO seasonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Season : {}, {}", id, seasonDTO);
        if (seasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        seasonDTO = seasonService.update(seasonDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, seasonDTO.getId().toString()))
            .body(seasonDTO);
    }

    /**
     * {@code PATCH  /seasons/:id} : Partial updates given fields of an existing season, field will ignore if it is null
     *
     * @param id the id of the seasonDTO to save.
     * @param seasonDTO the seasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seasonDTO,
     * or with status {@code 400 (Bad Request)} if the seasonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the seasonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the seasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SeasonDTO> partialUpdateSeason(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SeasonDTO seasonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Season partially : {}, {}", id, seasonDTO);
        if (seasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SeasonDTO> result = seasonService.partialUpdate(seasonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, seasonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /seasons} : get all the seasons.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of seasons in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SeasonDTO>> getAllSeasons(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Seasons");
        Page<SeasonDTO> page;
        if (eagerload) {
            page = seasonService.findAllWithEagerRelationships(pageable);
        } else {
            page = seasonService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /seasons/:id} : get the "id" season.
     *
     * @param id the id of the seasonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the seasonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SeasonDTO> getSeason(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Season : {}", id);
        Optional<SeasonDTO> seasonDTO = seasonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(seasonDTO);
    }

    /**
     * {@code DELETE  /seasons/:id} : delete the "id" season.
     *
     * @param id the id of the seasonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeason(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Season : {}", id);
        seasonService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
