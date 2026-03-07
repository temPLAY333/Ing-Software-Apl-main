package com.sgaraba.library.web.rest;

import static com.sgaraba.library.domain.BorrowedBookAsserts.*;
import static com.sgaraba.library.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgaraba.library.IntegrationTest;
import com.sgaraba.library.domain.Book;
import com.sgaraba.library.domain.BorrowedBook;
import com.sgaraba.library.domain.Client;
import com.sgaraba.library.repository.BorrowedBookRepository;
import com.sgaraba.library.service.BorrowedBookService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BorrowedBookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BorrowedBookResourceIT {

    private static final LocalDate DEFAULT_BORROW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROW_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BORROW_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/borrowed-books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepositoryMock;

    @Mock
    private BorrowedBookService borrowedBookServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBorrowedBookMockMvc;

    private BorrowedBook borrowedBook;

    private BorrowedBook insertedBorrowedBook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BorrowedBook createEntity() {
        return new BorrowedBook().borrowDate(DEFAULT_BORROW_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BorrowedBook createUpdatedEntity() {
        return new BorrowedBook().borrowDate(UPDATED_BORROW_DATE);
    }

    @BeforeEach
    void initTest() {
        borrowedBook = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBorrowedBook != null) {
            borrowedBookRepository.delete(insertedBorrowedBook);
            insertedBorrowedBook = null;
        }
    }

    @Test
    @Transactional
    void createBorrowedBook() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BorrowedBook
        var returnedBorrowedBook = om.readValue(
            restBorrowedBookMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(borrowedBook)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BorrowedBook.class
        );

        // Validate the BorrowedBook in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBorrowedBookUpdatableFieldsEquals(returnedBorrowedBook, getPersistedBorrowedBook(returnedBorrowedBook));

        insertedBorrowedBook = returnedBorrowedBook;
    }

    @Test
    @Transactional
    void createBorrowedBookWithExistingId() throws Exception {
        // Create the BorrowedBook with an existing ID
        borrowedBook.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowedBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(borrowedBook)))
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBorrowedBooks() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowedBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())));
    }

    void getAllBorrowedBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(borrowedBookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        restBorrowedBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(borrowedBookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    void getAllBorrowedBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(borrowedBookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        restBorrowedBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(borrowedBookRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBorrowedBook() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get the borrowedBook
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL_ID, borrowedBook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(borrowedBook.getId().intValue()))
            .andExpect(jsonPath("$.borrowDate").value(DEFAULT_BORROW_DATE.toString()));
    }

    @Test
    @Transactional
    void getBorrowedBooksByIdFiltering() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        Long id = borrowedBook.getId();

        defaultBorrowedBookFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBorrowedBookFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBorrowedBookFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBorrowDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate equals to
        defaultBorrowedBookFiltering("borrowDate.equals=" + DEFAULT_BORROW_DATE, "borrowDate.equals=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBorrowDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate in
        defaultBorrowedBookFiltering(
            "borrowDate.in=" + DEFAULT_BORROW_DATE + "," + UPDATED_BORROW_DATE,
            "borrowDate.in=" + UPDATED_BORROW_DATE
        );
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBorrowDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is not null
        defaultBorrowedBookFiltering("borrowDate.specified=true", "borrowDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBorrowDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is greater than or equal to
        defaultBorrowedBookFiltering(
            "borrowDate.greaterThanOrEqual=" + DEFAULT_BORROW_DATE,
            "borrowDate.greaterThanOrEqual=" + UPDATED_BORROW_DATE
        );
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBorrowDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is less than or equal to
        defaultBorrowedBookFiltering(
            "borrowDate.lessThanOrEqual=" + DEFAULT_BORROW_DATE,
            "borrowDate.lessThanOrEqual=" + SMALLER_BORROW_DATE
        );
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBorrowDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is less than
        defaultBorrowedBookFiltering("borrowDate.lessThan=" + UPDATED_BORROW_DATE, "borrowDate.lessThan=" + DEFAULT_BORROW_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBorrowDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        // Get all the borrowedBookList where borrowDate is greater than
        defaultBorrowedBookFiltering("borrowDate.greaterThan=" + SMALLER_BORROW_DATE, "borrowDate.greaterThan=" + DEFAULT_BORROW_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            borrowedBookRepository.saveAndFlush(borrowedBook);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        borrowedBook.setBook(book);
        borrowedBookRepository.saveAndFlush(borrowedBook);
        Long bookId = book.getId();
        // Get all the borrowedBookList where book equals to bookId
        defaultBorrowedBookShouldBeFound("bookId.equals=" + bookId);

        // Get all the borrowedBookList where book equals to (bookId + 1)
        defaultBorrowedBookShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    @Test
    @Transactional
    void getAllBorrowedBooksByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            borrowedBookRepository.saveAndFlush(borrowedBook);
            client = ClientResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        borrowedBook.setClient(client);
        borrowedBookRepository.saveAndFlush(borrowedBook);
        Long clientId = client.getId();
        // Get all the borrowedBookList where client equals to clientId
        defaultBorrowedBookShouldBeFound("clientId.equals=" + clientId);

        // Get all the borrowedBookList where client equals to (clientId + 1)
        defaultBorrowedBookShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    private void defaultBorrowedBookFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBorrowedBookShouldBeFound(shouldBeFound);
        defaultBorrowedBookShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBorrowedBookShouldBeFound(String filter) throws Exception {
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowedBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())));

        // Check, that the count call also returns 1
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBorrowedBookShouldNotBeFound(String filter) throws Exception {
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBorrowedBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBorrowedBook() throws Exception {
        // Get the borrowedBook
        restBorrowedBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBorrowedBook() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the borrowedBook
        BorrowedBook updatedBorrowedBook = borrowedBookRepository.findById(borrowedBook.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBorrowedBook are not directly saved in db
        em.detach(updatedBorrowedBook);
        updatedBorrowedBook.borrowDate(UPDATED_BORROW_DATE);

        restBorrowedBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBorrowedBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBorrowedBook))
            )
            .andExpect(status().isOk());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBorrowedBookToMatchAllProperties(updatedBorrowedBook);
    }

    @Test
    @Transactional
    void putNonExistingBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, borrowedBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(borrowedBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(borrowedBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(borrowedBook)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBorrowedBookWithPatch() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the borrowedBook using partial update
        BorrowedBook partialUpdatedBorrowedBook = new BorrowedBook();
        partialUpdatedBorrowedBook.setId(borrowedBook.getId());

        partialUpdatedBorrowedBook.borrowDate(UPDATED_BORROW_DATE);

        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBorrowedBook))
            )
            .andExpect(status().isOk());

        // Validate the BorrowedBook in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBorrowedBookUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBorrowedBook, borrowedBook),
            getPersistedBorrowedBook(borrowedBook)
        );
    }

    @Test
    @Transactional
    void fullUpdateBorrowedBookWithPatch() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the borrowedBook using partial update
        BorrowedBook partialUpdatedBorrowedBook = new BorrowedBook();
        partialUpdatedBorrowedBook.setId(borrowedBook.getId());

        partialUpdatedBorrowedBook.borrowDate(UPDATED_BORROW_DATE);

        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBorrowedBook))
            )
            .andExpect(status().isOk());

        // Validate the BorrowedBook in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBorrowedBookUpdatableFieldsEquals(partialUpdatedBorrowedBook, getPersistedBorrowedBook(partialUpdatedBorrowedBook));
    }

    @Test
    @Transactional
    void patchNonExistingBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, borrowedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(borrowedBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(borrowedBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBorrowedBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        borrowedBook.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowedBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(borrowedBook)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BorrowedBook in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBorrowedBook() throws Exception {
        // Initialize the database
        insertedBorrowedBook = borrowedBookRepository.saveAndFlush(borrowedBook);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the borrowedBook
        restBorrowedBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, borrowedBook.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return borrowedBookRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BorrowedBook getPersistedBorrowedBook(BorrowedBook borrowedBook) {
        return borrowedBookRepository.findById(borrowedBook.getId()).orElseThrow();
    }

    protected void assertPersistedBorrowedBookToMatchAllProperties(BorrowedBook expectedBorrowedBook) {
        assertBorrowedBookAllPropertiesEquals(expectedBorrowedBook, getPersistedBorrowedBook(expectedBorrowedBook));
    }

    protected void assertPersistedBorrowedBookToMatchUpdatableProperties(BorrowedBook expectedBorrowedBook) {
        assertBorrowedBookAllUpdatablePropertiesEquals(expectedBorrowedBook, getPersistedBorrowedBook(expectedBorrowedBook));
    }
}
