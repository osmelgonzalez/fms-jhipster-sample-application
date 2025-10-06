package com.myapp.web.rest;

import com.myapp.repository.PlayerRepository;
import com.myapp.service.PlayerService;
import com.myapp.service.dto.PlayerDTO;
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
 * REST controller for managing {@link com.myapp.domain.Player}.
 */
@RestController
@RequestMapping("/api/players")
public class PlayerResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerResource.class);

    private static final String ENTITY_NAME = "player";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlayerService playerService;

    private final PlayerRepository playerRepository;

    public PlayerResource(PlayerService playerService, PlayerRepository playerRepository) {
        this.playerService = playerService;
        this.playerRepository = playerRepository;
    }

    /**
     * {@code POST  /players} : Create a new player.
     *
     * @param playerDTO the playerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playerDTO, or with status {@code 400 (Bad Request)} if the player has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Player : {}", playerDTO);
        if (playerDTO.getId() != null) {
            throw new BadRequestAlertException("A new player cannot already have an ID", ENTITY_NAME, "idexists");
        }
        playerDTO = playerService.save(playerDTO);
        return ResponseEntity.created(new URI("/api/players/" + playerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, playerDTO.getId().toString()))
            .body(playerDTO);
    }

    /**
     * {@code PUT  /players/:id} : Updates an existing player.
     *
     * @param id the id of the playerDTO to save.
     * @param playerDTO the playerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerDTO,
     * or with status {@code 400 (Bad Request)} if the playerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlayerDTO playerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Player : {}, {}", id, playerDTO);
        if (playerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        playerDTO = playerService.update(playerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, playerDTO.getId().toString()))
            .body(playerDTO);
    }

    /**
     * {@code PATCH  /players/:id} : Partial updates given fields of an existing player, field will ignore if it is null
     *
     * @param id the id of the playerDTO to save.
     * @param playerDTO the playerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerDTO,
     * or with status {@code 400 (Bad Request)} if the playerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the playerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the playerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlayerDTO> partialUpdatePlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlayerDTO playerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Player partially : {}, {}", id, playerDTO);
        if (playerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlayerDTO> result = playerService.partialUpdate(playerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, playerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /players} : get all the players.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of players in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Players");
        Page<PlayerDTO> page;
        if (eagerload) {
            page = playerService.findAllWithEagerRelationships(pageable);
        } else {
            page = playerService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /players/:id} : get the "id" player.
     *
     * @param id the id of the playerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Player : {}", id);
        Optional<PlayerDTO> playerDTO = playerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(playerDTO);
    }

    /**
     * {@code DELETE  /players/:id} : delete the "id" player.
     *
     * @param id the id of the playerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Player : {}", id);
        playerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
