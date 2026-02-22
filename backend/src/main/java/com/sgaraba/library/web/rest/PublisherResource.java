package com.sgaraba.library.web.rest;

import com.sgaraba.library.domain.Publisher;
import com.sgaraba.library.repository.PublisherRepository;
import com.sgaraba.library.service.PublisherQueryService;
import com.sgaraba.library.service.PublisherService;
import com.sgaraba.library.service.criteria.PublisherCriteria;
import com.sgaraba.library.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.sgaraba.library.domain.Publisher}.
 */
@RestController
@RequestMapping("/api/publishers")
public class PublisherResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherResource.class);

    private static final String ENTITY_NAME = "publisher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublisherService publisherService;

    private final PublisherRepository publisherRepository;

    private final PublisherQueryService publisherQueryService;

    public PublisherResource(
        PublisherService publisherService,
        PublisherRepository publisherRepository,
        PublisherQueryService publisherQueryService
    ) {
        this.publisherService = publisherService;
        this.publisherRepository = publisherRepository;
        this.publisherQueryService = publisherQueryService;
    }

    /**
     * {@code POST  /publishers} : Create a new publisher.
     *
     * @param publisher the publisher to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new publisher, or with status {@code 400 (Bad Request)} if the publisher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Publisher> createPublisher(@Valid @RequestBody Publisher publisher) throws URISyntaxException {
        LOG.debug("REST request to save Publisher : {}", publisher);
        if (publisher.getId() != null) {
            throw new BadRequestAlertException("A new publisher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        publisher = publisherService.save(publisher);
        return ResponseEntity.created(new URI("/api/publishers/" + publisher.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, publisher.getId().toString()))
            .body(publisher);
    }

    /**
     * {@code PUT  /publishers/:id} : Updates an existing publisher.
     *
     * @param id the id of the publisher to save.
     * @param publisher the publisher to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publisher,
     * or with status {@code 400 (Bad Request)} if the publisher is not valid,
     * or with status {@code 500 (Internal Server Error)} if the publisher couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Publisher publisher
    ) throws URISyntaxException {
        LOG.debug("REST request to update Publisher : {}, {}", id, publisher);
        if (publisher.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisher.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        publisher = publisherService.update(publisher);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisher.getId().toString()))
            .body(publisher);
    }

    /**
     * {@code PATCH  /publishers/:id} : Partial updates given fields of an existing publisher, field will ignore if it is null
     *
     * @param id the id of the publisher to save.
     * @param publisher the publisher to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publisher,
     * or with status {@code 400 (Bad Request)} if the publisher is not valid,
     * or with status {@code 404 (Not Found)} if the publisher is not found,
     * or with status {@code 500 (Internal Server Error)} if the publisher couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Publisher> partialUpdatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Publisher publisher
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Publisher partially : {}, {}", id, publisher);
        if (publisher.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisher.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Publisher> result = publisherService.partialUpdate(publisher);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisher.getId().toString())
        );
    }

    /**
     * {@code GET  /publishers} : get all the publishers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of publishers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Publisher>> getAllPublishers(
        PublisherCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Publishers by criteria: {}", criteria);

        Page<Publisher> page = publisherQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /publishers/count} : count all the publishers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPublishers(PublisherCriteria criteria) {
        LOG.debug("REST request to count Publishers by criteria: {}", criteria);
        return ResponseEntity.ok().body(publisherQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /publishers/:id} : get the "id" publisher.
     *
     * @param id the id of the publisher to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the publisher, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Publisher : {}", id);
        Optional<Publisher> publisher = publisherService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publisher);
    }

    /**
     * {@code DELETE  /publishers/:id} : delete the "id" publisher.
     *
     * @param id the id of the publisher to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Publisher : {}", id);
        publisherService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
