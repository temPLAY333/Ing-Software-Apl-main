package com.sgaraba.library.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sgaraba.library.domain.Book;
import com.sgaraba.library.domain.BorrowedBook;
import com.sgaraba.library.repository.BookRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for {@link BookServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setIsbn("978-3-16-148410-0");
        book.setName("Test Book");
        book.setPublishYear("2024");
        book.setCopies(5);
    }

    @Test
    void testSave() {
        // Given
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        Book result = bookService.save(book);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getIsbn()).isEqualTo("978-3-16-148410-0");
        assertThat(result.getName()).isEqualTo("Test Book");
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdate() {
        // Given
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        Book result = bookService.update(book);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testPartialUpdate() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setIsbn("978-0-00-000000-0");
        existingBook.setName("Old Book Name");
        existingBook.setPublishYear("2020");
        existingBook.setCopies(3);

        Book updateBook = new Book();
        updateBook.setId(1L);
        updateBook.setName("New Book Name");
        updateBook.setCopies(10);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Book> result = bookService.partialUpdate(updateBook);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Book Name");
        assertThat(result.get().getCopies()).isEqualTo(10);
        assertThat(result.get().getIsbn()).isEqualTo("978-0-00-000000-0"); // Should remain unchanged
        assertThat(result.get().getPublishYear()).isEqualTo("2020"); // Should remain unchanged
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testPartialUpdateAllFields() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setIsbn("978-0-00-000000-0");
        existingBook.setName("Old Book Name");
        existingBook.setPublishYear("2020");
        existingBook.setCopies(3);

        Book updateBook = new Book();
        updateBook.setId(1L);
        updateBook.setIsbn("978-3-16-148410-0");
        updateBook.setName("New Book Name");
        updateBook.setPublishYear("2024");
        updateBook.setCopies(10);
        updateBook.setCover(new byte[] { 1, 2, 3 });
        updateBook.setCoverContentType("image/png");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Book> result = bookService.partialUpdate(updateBook);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIsbn()).isEqualTo("978-3-16-148410-0");
        assertThat(result.get().getName()).isEqualTo("New Book Name");
        assertThat(result.get().getPublishYear()).isEqualTo("2024");
        assertThat(result.get().getCopies()).isEqualTo(10);
        assertThat(result.get().getCover()).isEqualTo(new byte[] { 1, 2, 3 });
        assertThat(result.get().getCoverContentType()).isEqualTo("image/png");
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testPartialUpdateNotFound() {
        // Given
        Book updateBook = new Book();
        updateBook.setId(999L);
        updateBook.setName("New Book Name");

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.partialUpdate(updateBook);

        // Then
        assertThat(result).isEmpty();
        verify(bookRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testFindAllWithEagerRelationships() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = Arrays.asList(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllWithEagerRelationships(pageable)).thenReturn(bookPage);

        // When
        Page<Book> result = bookService.findAllWithEagerRelationships(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        verify(bookRepository, times(1)).findAllWithEagerRelationships(pageable);
    }

    @Test
    void testFindAllWhereBorrowedBookIsNull() {
        // Given
        Book book1 = new Book();
        book1.setId(1L);
        book1.setName("Available Book");
        // Empty borrowedBooks set by default

        Book book2 = new Book();
        book2.setId(2L);
        book2.setName("Borrowed Book");
        BorrowedBook borrowedBook = new BorrowedBook();
        book2.addBorrowedBook(borrowedBook);

        Book book3 = new Book();
        book3.setId(3L);
        book3.setName("Another Available Book");
        // Empty borrowedBooks set by default

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        // When
        List<Book> result = bookService.findAllWhereBorrowedBookIsNull();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getId).containsExactlyInAnyOrder(1L, 3L);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testFindOne() {
        // Given
        when(bookRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(book));

        // When
        Optional<Book> result = bookService.findOne(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Test Book");
        verify(bookRepository, times(1)).findOneWithEagerRelationships(1L);
    }

    @Test
    void testFindOneNotFound() {
        // Given
        when(bookRepository.findOneWithEagerRelationships(999L)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.findOne(999L);

        // Then
        assertThat(result).isEmpty();
        verify(bookRepository, times(1)).findOneWithEagerRelationships(999L);
    }

    @Test
    void testDelete() {
        // Given
        doNothing().when(bookRepository).deleteById(1L);

        // When
        bookService.delete(1L);

        // Then
        verify(bookRepository, times(1)).deleteById(1L);
    }
}
