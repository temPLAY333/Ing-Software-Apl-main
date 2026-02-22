package com.sgaraba.library.web.rest;

import static com.sgaraba.library.domain.BookAsserts.*;
import static com.sgaraba.library.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgaraba.library.IntegrationTest;
import com.sgaraba.library.domain.Author;
import com.sgaraba.library.domain.Book;
import com.sgaraba.library.domain.Publisher;
import com.sgaraba.library.repository.BookRepository;
import com.sgaraba.library.service.BookService;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Base64;
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
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISH_YEAR = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISH_YEAR = "BBBBBBBBBB";

    private static final Integer DEFAULT_COPIES = 1;
    private static final Integer UPDATED_COPIES = 2;
    private static final Integer SMALLER_COPIES = 1 - 1;

    private static final byte[] DEFAULT_COVER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COVER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COVER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COVER_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookRepository bookRepository;

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private BookService bookServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookMockMvc;

    private Book book;

    private Book insertedBook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity() {
        return new Book()
            .isbn(DEFAULT_ISBN)
            .name(DEFAULT_NAME)
            .publishYear(DEFAULT_PUBLISH_YEAR)
            .copies(DEFAULT_COPIES)
            .cover(DEFAULT_COVER)
            .coverContentType(DEFAULT_COVER_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity() {
        return new Book()
            .isbn(UPDATED_ISBN)
            .name(UPDATED_NAME)
            .publishYear(UPDATED_PUBLISH_YEAR)
            .copies(UPDATED_COPIES)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE);
    }

    @BeforeEach
    void initTest() {
        book = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBook != null) {
            bookRepository.delete(insertedBook);
            insertedBook = null;
        }
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Book
        var returnedBook = om.readValue(
            restBookMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Book.class
        );

        // Validate the Book in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBookUpdatableFieldsEquals(returnedBook, getPersistedBook(returnedBook));

        insertedBook = returnedBook;
    }

    @Test
    @Transactional
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIsbnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        book.setIsbn(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        book.setName(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishYearIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        book.setPublishYear(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCopiesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        book.setCopies(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].publishYear").value(hasItem(DEFAULT_PUBLISH_YEAR)))
            .andExpect(jsonPath("$.[*].copies").value(hasItem(DEFAULT_COPIES)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_COVER))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBook() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc
            .perform(get(ENTITY_API_URL_ID, book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId().intValue()))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.publishYear").value(DEFAULT_PUBLISH_YEAR))
            .andExpect(jsonPath("$.copies").value(DEFAULT_COPIES))
            .andExpect(jsonPath("$.coverContentType").value(DEFAULT_COVER_CONTENT_TYPE))
            .andExpect(jsonPath("$.cover").value(Base64.getEncoder().encodeToString(DEFAULT_COVER)));
    }

    @Test
    @Transactional
    void getBooksByIdFiltering() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        Long id = book.getId();

        defaultBookFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn equals to
        defaultBookFiltering("isbn.equals=" + DEFAULT_ISBN, "isbn.equals=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn in
        defaultBookFiltering("isbn.in=" + DEFAULT_ISBN + "," + UPDATED_ISBN, "isbn.in=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn is not null
        defaultBookFiltering("isbn.specified=true", "isbn.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByIsbnContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn contains
        defaultBookFiltering("isbn.contains=" + DEFAULT_ISBN, "isbn.contains=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn does not contain
        defaultBookFiltering("isbn.doesNotContain=" + UPDATED_ISBN, "isbn.doesNotContain=" + DEFAULT_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where name equals to
        defaultBookFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBooksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where name in
        defaultBookFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBooksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where name is not null
        defaultBookFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where name contains
        defaultBookFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBooksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where name does not contain
        defaultBookFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllBooksByPublishYearIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear equals to
        defaultBookFiltering("publishYear.equals=" + DEFAULT_PUBLISH_YEAR, "publishYear.equals=" + UPDATED_PUBLISH_YEAR);
    }

    @Test
    @Transactional
    void getAllBooksByPublishYearIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear in
        defaultBookFiltering(
            "publishYear.in=" + DEFAULT_PUBLISH_YEAR + "," + UPDATED_PUBLISH_YEAR,
            "publishYear.in=" + UPDATED_PUBLISH_YEAR
        );
    }

    @Test
    @Transactional
    void getAllBooksByPublishYearIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear is not null
        defaultBookFiltering("publishYear.specified=true", "publishYear.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByPublishYearContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear contains
        defaultBookFiltering("publishYear.contains=" + DEFAULT_PUBLISH_YEAR, "publishYear.contains=" + UPDATED_PUBLISH_YEAR);
    }

    @Test
    @Transactional
    void getAllBooksByPublishYearNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where publishYear does not contain
        defaultBookFiltering("publishYear.doesNotContain=" + UPDATED_PUBLISH_YEAR, "publishYear.doesNotContain=" + DEFAULT_PUBLISH_YEAR);
    }

    @Test
    @Transactional
    void getAllBooksByCopiesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where copies equals to
        defaultBookFiltering("copies.equals=" + DEFAULT_COPIES, "copies.equals=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    void getAllBooksByCopiesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where copies in
        defaultBookFiltering("copies.in=" + DEFAULT_COPIES + "," + UPDATED_COPIES, "copies.in=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    void getAllBooksByCopiesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is not null
        defaultBookFiltering("copies.specified=true", "copies.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByCopiesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is greater than or equal to
        defaultBookFiltering("copies.greaterThanOrEqual=" + DEFAULT_COPIES, "copies.greaterThanOrEqual=" + UPDATED_COPIES);
    }

    @Test
    @Transactional
    void getAllBooksByCopiesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is less than or equal to
        defaultBookFiltering("copies.lessThanOrEqual=" + DEFAULT_COPIES, "copies.lessThanOrEqual=" + SMALLER_COPIES);
    }

    @Test
    @Transactional
    void getAllBooksByCopiesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is less than
        defaultBookFiltering("copies.lessThan=" + UPDATED_COPIES, "copies.lessThan=" + DEFAULT_COPIES);
    }

    @Test
    @Transactional
    void getAllBooksByCopiesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where copies is greater than
        defaultBookFiltering("copies.greaterThan=" + SMALLER_COPIES, "copies.greaterThan=" + DEFAULT_COPIES);
    }

    @Test
    @Transactional
    void getAllBooksByPublisherIsEqualToSomething() throws Exception {
        Publisher publisher;
        if (TestUtil.findAll(em, Publisher.class).isEmpty()) {
            bookRepository.saveAndFlush(book);
            publisher = PublisherResourceIT.createEntity();
        } else {
            publisher = TestUtil.findAll(em, Publisher.class).get(0);
        }
        em.persist(publisher);
        em.flush();
        book.setPublisher(publisher);
        bookRepository.saveAndFlush(book);
        Long publisherId = publisher.getId();
        // Get all the bookList where publisher equals to publisherId
        defaultBookShouldBeFound("publisherId.equals=" + publisherId);

        // Get all the bookList where publisher equals to (publisherId + 1)
        defaultBookShouldNotBeFound("publisherId.equals=" + (publisherId + 1));
    }

    @Test
    @Transactional
    void getAllBooksByAuthorIsEqualToSomething() throws Exception {
        Author author;
        if (TestUtil.findAll(em, Author.class).isEmpty()) {
            bookRepository.saveAndFlush(book);
            author = AuthorResourceIT.createEntity();
        } else {
            author = TestUtil.findAll(em, Author.class).get(0);
        }
        em.persist(author);
        em.flush();
        book.addAuthor(author);
        bookRepository.saveAndFlush(book);
        Long authorId = author.getId();
        // Get all the bookList where author equals to authorId
        defaultBookShouldBeFound("authorId.equals=" + authorId);

        // Get all the bookList where author equals to (authorId + 1)
        defaultBookShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    private void defaultBookFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookShouldBeFound(shouldBeFound);
        defaultBookShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookShouldBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].publishYear").value(hasItem(DEFAULT_PUBLISH_YEAR)))
            .andExpect(jsonPath("$.[*].copies").value(hasItem(DEFAULT_COPIES)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_COVER))));

        // Check, that the count call also returns 1
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookShouldNotBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBook() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook
            .isbn(UPDATED_ISBN)
            .name(UPDATED_NAME)
            .publishYear(UPDATED_PUBLISH_YEAR)
            .copies(UPDATED_COPIES)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE);

        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookToMatchAllProperties(updatedBook);
    }

    @Test
    @Transactional
    void putNonExistingBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL_ID, book.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook.name(UPDATED_NAME).cover(UPDATED_COVER).coverContentType(UPDATED_COVER_CONTENT_TYPE);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBook, book), getPersistedBook(book));
    }

    @Test
    @Transactional
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook
            .isbn(UPDATED_ISBN)
            .name(UPDATED_NAME)
            .publishYear(UPDATED_PUBLISH_YEAR)
            .copies(UPDATED_COPIES)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookUpdatableFieldsEquals(partialUpdatedBook, getPersistedBook(partialUpdatedBook));
    }

    @Test
    @Transactional
    void patchNonExistingBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL_ID, book.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBook() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the book
        restBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, book.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookRepository.count();
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

    protected Book getPersistedBook(Book book) {
        return bookRepository.findById(book.getId()).orElseThrow();
    }

    protected void assertPersistedBookToMatchAllProperties(Book expectedBook) {
        assertBookAllPropertiesEquals(expectedBook, getPersistedBook(expectedBook));
    }

    protected void assertPersistedBookToMatchUpdatableProperties(Book expectedBook) {
        assertBookAllUpdatablePropertiesEquals(expectedBook, getPersistedBook(expectedBook));
    }
}
