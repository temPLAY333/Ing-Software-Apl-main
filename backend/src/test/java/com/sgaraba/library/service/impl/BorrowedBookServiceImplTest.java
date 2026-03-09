package com.sgaraba.library.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sgaraba.library.domain.BorrowedBook;
import com.sgaraba.library.repository.BorrowedBookRepository;
import java.time.LocalDate;
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
 * Unit tests for {@link BorrowedBookServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class BorrowedBookServiceImplTest {

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @InjectMocks
    private BorrowedBookServiceImpl borrowedBookService;

    private BorrowedBook borrowedBook;

    @BeforeEach
    void setUp() {
        borrowedBook = new BorrowedBook();
        borrowedBook.setId(1L);
        borrowedBook.setBorrowDate(LocalDate.of(2024, 1, 15));
    }

    @Test
    void testSave() {
        // Given
        when(borrowedBookRepository.save(any(BorrowedBook.class))).thenReturn(borrowedBook);

        // When
        BorrowedBook result = borrowedBookService.save(borrowedBook);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBorrowDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        verify(borrowedBookRepository, times(1)).save(borrowedBook);
    }

    @Test
    void testUpdate() {
        // Given
        when(borrowedBookRepository.save(any(BorrowedBook.class))).thenReturn(borrowedBook);

        // When
        BorrowedBook result = borrowedBookService.update(borrowedBook);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(borrowedBookRepository, times(1)).save(borrowedBook);
    }

    @Test
    void testPartialUpdate() {
        // Given
        BorrowedBook existingBorrowedBook = new BorrowedBook();
        existingBorrowedBook.setId(1L);
        existingBorrowedBook.setBorrowDate(LocalDate.of(2023, 12, 1));

        BorrowedBook updateBorrowedBook = new BorrowedBook();
        updateBorrowedBook.setId(1L);
        updateBorrowedBook.setBorrowDate(LocalDate.of(2024, 1, 15));

        when(borrowedBookRepository.findById(1L)).thenReturn(Optional.of(existingBorrowedBook));
        when(borrowedBookRepository.save(any(BorrowedBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<BorrowedBook> result = borrowedBookService.partialUpdate(updateBorrowedBook);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBorrowDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        verify(borrowedBookRepository, times(1)).findById(1L);
        verify(borrowedBookRepository, times(1)).save(any(BorrowedBook.class));
    }

    @Test
    void testPartialUpdateNotFound() {
        // Given
        BorrowedBook updateBorrowedBook = new BorrowedBook();
        updateBorrowedBook.setId(999L);
        updateBorrowedBook.setBorrowDate(LocalDate.of(2024, 1, 15));

        when(borrowedBookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<BorrowedBook> result = borrowedBookService.partialUpdate(updateBorrowedBook);

        // Then
        assertThat(result).isEmpty();
        verify(borrowedBookRepository, times(1)).findById(999L);
        verify(borrowedBookRepository, never()).save(any(BorrowedBook.class));
    }

    @Test
    void testFindAllWithEagerRelationships() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<BorrowedBook> borrowedBooks = Arrays.asList(borrowedBook);
        Page<BorrowedBook> borrowedBookPage = new PageImpl<>(borrowedBooks, pageable, borrowedBooks.size());

        when(borrowedBookRepository.findAllWithEagerRelationships(pageable)).thenReturn(borrowedBookPage);

        // When
        Page<BorrowedBook> result = borrowedBookService.findAllWithEagerRelationships(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        verify(borrowedBookRepository, times(1)).findAllWithEagerRelationships(pageable);
    }

    @Test
    void testFindOne() {
        // Given
        when(borrowedBookRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(borrowedBook));

        // When
        Optional<BorrowedBook> result = borrowedBookService.findOne(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getBorrowDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        verify(borrowedBookRepository, times(1)).findOneWithEagerRelationships(1L);
    }

    @Test
    void testFindOneNotFound() {
        // Given
        when(borrowedBookRepository.findOneWithEagerRelationships(999L)).thenReturn(Optional.empty());

        // When
        Optional<BorrowedBook> result = borrowedBookService.findOne(999L);

        // Then
        assertThat(result).isEmpty();
        verify(borrowedBookRepository, times(1)).findOneWithEagerRelationships(999L);
    }

    @Test
    void testDelete() {
        // Given
        doNothing().when(borrowedBookRepository).deleteById(1L);

        // When
        borrowedBookService.delete(1L);

        // Then
        verify(borrowedBookRepository, times(1)).deleteById(1L);
    }
}
