package com.sgaraba.library.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sgaraba.library.domain.Book;
import com.sgaraba.library.domain.Publisher;
import com.sgaraba.library.repository.PublisherRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PublisherServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    private Publisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new Publisher();
        publisher.setId(1L);
        publisher.setName("Penguin Books");
    }

    @Test
    void testSave() {
        // Given
        when(publisherRepository.save(any(Publisher.class))).thenReturn(publisher);

        // When
        Publisher result = publisherService.save(publisher);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Penguin Books");
        verify(publisherRepository, times(1)).save(publisher);
    }

    @Test
    void testUpdate() {
        // Given
        when(publisherRepository.save(any(Publisher.class))).thenReturn(publisher);

        // When
        Publisher result = publisherService.update(publisher);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(publisherRepository, times(1)).save(publisher);
    }

    @Test
    void testPartialUpdate() {
        // Given
        Publisher existingPublisher = new Publisher();
        existingPublisher.setId(1L);
        existingPublisher.setName("Old Publisher Name");

        Publisher updatePublisher = new Publisher();
        updatePublisher.setId(1L);
        updatePublisher.setName("New Publisher Name");

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(existingPublisher));
        when(publisherRepository.save(any(Publisher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Publisher> result = publisherService.partialUpdate(updatePublisher);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Publisher Name");
        verify(publisherRepository, times(1)).findById(1L);
        verify(publisherRepository, times(1)).save(any(Publisher.class));
    }

    @Test
    void testPartialUpdateNotFound() {
        // Given
        Publisher updatePublisher = new Publisher();
        updatePublisher.setId(999L);
        updatePublisher.setName("New Publisher Name");

        when(publisherRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Publisher> result = publisherService.partialUpdate(updatePublisher);

        // Then
        assertThat(result).isEmpty();
        verify(publisherRepository, times(1)).findById(999L);
        verify(publisherRepository, never()).save(any(Publisher.class));
    }

    @Test
    void testFindAllWhereBookIsNull() {
        // Given
        Publisher publisher1 = new Publisher();
        publisher1.setId(1L);
        publisher1.setName("Publisher Without Book");
        publisher1.setBook(null);

        Publisher publisher2 = new Publisher();
        publisher2.setId(2L);
        publisher2.setName("Publisher With Book");
        Book book = new Book();
        publisher2.setBook(book);

        Publisher publisher3 = new Publisher();
        publisher3.setId(3L);
        publisher3.setName("Another Publisher Without Book");
        publisher3.setBook(null);

        when(publisherRepository.findAll()).thenReturn(Arrays.asList(publisher1, publisher2, publisher3));

        // When
        List<Publisher> result = publisherService.findAllWhereBookIsNull();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Publisher::getId).containsExactlyInAnyOrder(1L, 3L);
        verify(publisherRepository, times(1)).findAll();
    }

    @Test
    void testFindOne() {
        // Given
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

        // When
        Optional<Publisher> result = publisherService.findOne(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Penguin Books");
        verify(publisherRepository, times(1)).findById(1L);
    }

    @Test
    void testFindOneNotFound() {
        // Given
        when(publisherRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Publisher> result = publisherService.findOne(999L);

        // Then
        assertThat(result).isEmpty();
        verify(publisherRepository, times(1)).findById(999L);
    }

    @Test
    void testDelete() {
        // Given
        doNothing().when(publisherRepository).deleteById(1L);

        // When
        publisherService.delete(1L);

        // Then
        verify(publisherRepository, times(1)).deleteById(1L);
    }
}
