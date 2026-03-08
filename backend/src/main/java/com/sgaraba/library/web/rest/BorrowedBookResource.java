package com.sgaraba.library.web.rest;

import com.sgaraba.library.domain.BorrowedBook;
import com.sgaraba.library.repository.BorrowedBookRepository;
import com.sgaraba.library.service.BorrowedBookQueryService;
import com.sgaraba.library.service.BorrowedBookService;
import com.sgaraba.library.service.criteria.BorrowedBookCriteria;
import com.sgaraba.library.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.sgaraba.library.domain.BorrowedBook}.
 */
@RestController
@RequestMapping("/api/borrowed-books")
public class BorrowedBookResource {

    private static final Logger LOG = LoggerFactory.getLogger(BorrowedBookResource.class);

    private static final String ENTITY_NAME = "borrowedBook";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BorrowedBookService borrowedBookService;

    private final BorrowedBookRepository borrowedBookRepository;

    private final BorrowedBookQueryService borrowedBookQueryService;

    public BorrowedBookResource(
        BorrowedBookService borrowedBookService,
        BorrowedBookRepository borrowedBookRepository,
        BorrowedBookQueryService borrowedBookQueryService
    ) {
        this.borrowedBookService = borrowedBookService;
        this.borrowedBookRepository = borrowedBookRepository;
        this.borrowedBookQueryService = borrowedBookQueryService;
    }

    /**
     * {@code POST  /borrowed-books} : Create a new borrowedBook.
     *
     * @param borrowedBook the borrowedBook to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new borrowedBook, or with status {@code 400 (Bad Request)} if the borrowedBook has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BorrowedBook> createBorrowedBook(@RequestBody BorrowedBook borrowedBook) throws URISyntaxException {
        LOG.debug("REST request to save BorrowedBook : {}", borrowedBook);
        if (borrowedBook.getId() != null) {
            throw new BadRequestAlertException("A new borrowedBook cannot already have an ID", ENTITY_NAME, "idexists");
        }
        borrowedBook = borrowedBookService.save(borrowedBook);
        return ResponseEntity.created(new URI("/api/borrowed-books/" + borrowedBook.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, borrowedBook.getId().toString()))
            .body(borrowedBook);
    }

    /**
     * {@code PUT  /borrowed-books/:id} : Updates an existing borrowedBook.
     *
     * @param id the id of the borrowedBook to save.
     * @param borrowedBook the borrowedBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrowedBook,
     * or with status {@code 400 (Bad Request)} if the borrowedBook is not valid,
     * or with status {@code 500 (Internal Server Error)} if the borrowedBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BorrowedBook> updateBorrowedBook(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BorrowedBook borrowedBook
    ) throws URISyntaxException {
        LOG.debug("REST request to update BorrowedBook : {}, {}", id, borrowedBook);
        if (borrowedBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowedBook.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowedBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        borrowedBook = borrowedBookService.update(borrowedBook);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, borrowedBook.getId().toString()))
            .body(borrowedBook);
    }

    /**
     * {@code PATCH  /borrowed-books/:id} : Partial updates given fields of an existing borrowedBook, field will ignore if it is null
     *
     * @param id the id of the borrowedBook to save.
     * @param borrowedBook the borrowedBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrowedBook,
     * or with status {@code 400 (Bad Request)} if the borrowedBook is not valid,
     * or with status {@code 404 (Not Found)} if the borrowedBook is not found,
     * or with status {@code 500 (Internal Server Error)} if the borrowedBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BorrowedBook> partialUpdateBorrowedBook(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BorrowedBook borrowedBook
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BorrowedBook partially : {}, {}", id, borrowedBook);
        if (borrowedBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowedBook.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowedBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BorrowedBook> result = borrowedBookService.partialUpdate(borrowedBook);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, borrowedBook.getId().toString())
        );
    }

    /**
     * {@code GET  /borrowed-books} : get all the borrowedBooks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @param eagerload flag to eager load entities from relationships (This is applicable only if no criteria is specified).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of borrowedBooks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BorrowedBook>> getAllBorrowedBooks(
        BorrowedBookCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get BorrowedBooks by criteria: {}, eagerload: {}", criteria, eagerload);

        Page<BorrowedBook> page;
        boolean criteriaIsEmpty = criteria == null ||
            (criteria.getId() == null &&
             criteria.getBorrowDate() == null &&
             criteria.getBookId() == null &&
             criteria.getClientId() == null);

        if (eagerload && criteriaIsEmpty) {
            page = borrowedBookService.findAllWithEagerRelationships(pageable);
        } else {
            page = borrowedBookQueryService.findByCriteria(criteria, pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /borrowed-books/count} : count all the borrowedBooks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBorrowedBooks(BorrowedBookCriteria criteria) {
        LOG.debug("REST request to count BorrowedBooks by criteria: {}", criteria);
        return ResponseEntity.ok().body(borrowedBookQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /borrowed-books/:id} : get the "id" borrowedBook.
     *
     * @param id the id of the borrowedBook to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the borrowedBook, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BorrowedBook> getBorrowedBook(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BorrowedBook : {}", id);
        Optional<BorrowedBook> borrowedBook = borrowedBookService.findOne(id);
        return ResponseUtil.wrapOrNotFound(borrowedBook);
    }

    /**
     * {@code DELETE  /borrowed-books/:id} : delete the "id" borrowedBook.
     *
     * @param id the id of the borrowedBook to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowedBook(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BorrowedBook : {}", id);
        borrowedBookService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
