package com.myapp.web.rest;

import com.myapp.repository.TournamentRepository;
import com.myapp.service.TournamentService;
import com.myapp.service.dto.TournamentDTO;
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
 * REST controller for managing {@link com.myapp.domain.Tournament}.
 */
@RestController
@RequestMapping("/api/tournaments")
public class TournamentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TournamentResource.class);

    private static final String ENTITY_NAME = "tournament";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TournamentService tournamentService;

    private final TournamentRepository tournamentRepository;

    public TournamentResource(TournamentService tournamentService, TournamentRepository tournamentRepository) {
        this.tournamentService = tournamentService;
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * {@code POST  /tournaments} : Create a new tournament.
     *
     * @param tournamentDTO the tournamentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tournamentDTO, or with status {@code 400 (Bad Request)} if the tournament has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TournamentDTO> createTournament(@Valid @RequestBody TournamentDTO tournamentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Tournament : {}", tournamentDTO);
        if (tournamentDTO.getId() != null) {
            throw new BadRequestAlertException("A new tournament cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tournamentDTO = tournamentService.save(tournamentDTO);
        return ResponseEntity.created(new URI("/api/tournaments/" + tournamentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, tournamentDTO.getId().toString()))
            .body(tournamentDTO);
    }

    /**
     * {@code PUT  /tournaments/:id} : Updates an existing tournament.
     *
     * @param id the id of the tournamentDTO to save.
     * @param tournamentDTO the tournamentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournamentDTO,
     * or with status {@code 400 (Bad Request)} if the tournamentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tournamentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TournamentDTO> updateTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TournamentDTO tournamentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Tournament : {}, {}", id, tournamentDTO);
        if (tournamentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournamentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tournamentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tournamentDTO = tournamentService.update(tournamentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tournamentDTO.getId().toString()))
            .body(tournamentDTO);
    }

    /**
     * {@code PATCH  /tournaments/:id} : Partial updates given fields of an existing tournament, field will ignore if it is null
     *
     * @param id the id of the tournamentDTO to save.
     * @param tournamentDTO the tournamentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournamentDTO,
     * or with status {@code 400 (Bad Request)} if the tournamentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tournamentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tournamentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TournamentDTO> partialUpdateTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TournamentDTO tournamentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Tournament partially : {}, {}", id, tournamentDTO);
        if (tournamentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournamentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tournamentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TournamentDTO> result = tournamentService.partialUpdate(tournamentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tournamentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tournaments} : get all the tournaments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tournaments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TournamentDTO>> getAllTournaments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Tournaments");
        Page<TournamentDTO> page = tournamentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tournaments/:id} : get the "id" tournament.
     *
     * @param id the id of the tournamentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tournamentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTO> getTournament(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Tournament : {}", id);
        Optional<TournamentDTO> tournamentDTO = tournamentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tournamentDTO);
    }

    /**
     * {@code DELETE  /tournaments/:id} : delete the "id" tournament.
     *
     * @param id the id of the tournamentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Tournament : {}", id);
        tournamentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
