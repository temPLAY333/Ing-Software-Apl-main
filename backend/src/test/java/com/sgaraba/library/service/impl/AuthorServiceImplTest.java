package com.sgaraba.library.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sgaraba.library.domain.Author;
import com.sgaraba.library.repository.AuthorRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link AuthorServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
    }

    @Test
    void testSave() {
        // Given
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        // When
        Author result = authorService.save(author);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testUpdate() {
        // Given
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        // When
        Author result = authorService.update(author);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testPartialUpdate() {
        // Given
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setFirstName("Jane");
        existingAuthor.setLastName("Smith");

        Author updateAuthor = new Author();
        updateAuthor.setId(1L);
        updateAuthor.setFirstName("John");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Author> result = authorService.partialUpdate(updateAuthor);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Smith"); // Should remain unchanged
        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testPartialUpdateWithLastName() {
        // Given
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setFirstName("Jane");
        existingAuthor.setLastName("Smith");

        Author updateAuthor = new Author();
        updateAuthor.setId(1L);
        updateAuthor.setLastName("Johnson");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Author> result = authorService.partialUpdate(updateAuthor);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Jane"); // Should remain unchanged
        assertThat(result.get().getLastName()).isEqualTo("Johnson");
        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testPartialUpdateNotFound() {
        // Given
        Author updateAuthor = new Author();
        updateAuthor.setId(999L);
        updateAuthor.setFirstName("John");

        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Author> result = authorService.partialUpdate(updateAuthor);

        // Then
        assertThat(result).isEmpty();
        verify(authorRepository, times(1)).findById(999L);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void testFindOne() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        // When
        Optional<Author> result = authorService.findOne(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getFirstName()).isEqualTo("John");
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testFindOneNotFound() {
        // Given
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Author> result = authorService.findOne(999L);

        // Then
        assertThat(result).isEmpty();
        verify(authorRepository, times(1)).findById(999L);
    }

    @Test
    void testDelete() {
        // Given
        doNothing().when(authorRepository).deleteById(1L);

        // When
        authorService.delete(1L);

        // Then
        verify(authorRepository, times(1)).deleteById(1L);
    }
}
