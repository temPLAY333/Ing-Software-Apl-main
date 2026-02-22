package com.sgaraba.library.service.impl;

import com.sgaraba.library.domain.Book;
import com.sgaraba.library.repository.BookRepository;
import com.sgaraba.library.service.BookService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sgaraba.library.domain.Book}.
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private static final Logger LOG = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        LOG.debug("Request to save Book : {}", book);
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book) {
        LOG.debug("Request to update Book : {}", book);
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> partialUpdate(Book book) {
        LOG.debug("Request to partially update Book : {}", book);

        return bookRepository
            .findById(book.getId())
            .map(existingBook -> {
                if (book.getIsbn() != null) {
                    existingBook.setIsbn(book.getIsbn());
                }
                if (book.getName() != null) {
                    existingBook.setName(book.getName());
                }
                if (book.getPublishYear() != null) {
                    existingBook.setPublishYear(book.getPublishYear());
                }
                if (book.getCopies() != null) {
                    existingBook.setCopies(book.getCopies());
                }
                if (book.getCover() != null) {
                    existingBook.setCover(book.getCover());
                }
                if (book.getCoverContentType() != null) {
                    existingBook.setCoverContentType(book.getCoverContentType());
                }

                return existingBook;
            })
            .map(bookRepository::save);
    }

    public Page<Book> findAllWithEagerRelationships(Pageable pageable) {
        return bookRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     *  Get all the books where BorrowedBook is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Book> findAllWhereBorrowedBookIsNull() {
        LOG.debug("Request to get all books where BorrowedBook is null");
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false).filter(book -> book.getBorrowedBook() == null).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findOne(Long id) {
        LOG.debug("Request to get Book : {}", id);
        return bookRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }
}
