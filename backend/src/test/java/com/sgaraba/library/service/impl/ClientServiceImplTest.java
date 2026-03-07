package com.sgaraba.library.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sgaraba.library.domain.BorrowedBook;
import com.sgaraba.library.domain.Client;
import com.sgaraba.library.repository.ClientRepository;
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
 * Unit tests for {@link ClientServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        client.setAddress("123 Main St");
        client.setPhone("555-1234");
    }

    @Test
    void testSave() {
        // Given
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        Client result = clientService.save(client);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testUpdate() {
        // Given
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        Client result = clientService.update(client);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testPartialUpdate() {
        // Given
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setFirstName("Jane");
        existingClient.setLastName("Smith");
        existingClient.setEmail("jane.smith@example.com");
        existingClient.setAddress("456 Oak Ave");
        existingClient.setPhone("555-5678");

        Client updateClient = new Client();
        updateClient.setId(1L);
        updateClient.setFirstName("John");
        updateClient.setEmail("john.doe@example.com");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Client> result = clientService.partialUpdate(updateClient);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.get().getLastName()).isEqualTo("Smith"); // Should remain unchanged
        assertThat(result.get().getAddress()).isEqualTo("456 Oak Ave"); // Should remain unchanged
        assertThat(result.get().getPhone()).isEqualTo("555-5678"); // Should remain unchanged
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testPartialUpdateAllFields() {
        // Given
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setFirstName("Jane");
        existingClient.setLastName("Smith");
        existingClient.setEmail("jane.smith@example.com");
        existingClient.setAddress("456 Oak Ave");
        existingClient.setPhone("555-5678");

        Client updateClient = new Client();
        updateClient.setId(1L);
        updateClient.setFirstName("John");
        updateClient.setLastName("Doe");
        updateClient.setEmail("john.doe@example.com");
        updateClient.setAddress("123 Main St");
        updateClient.setPhone("555-1234");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Client> result = clientService.partialUpdate(updateClient);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Doe");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.get().getAddress()).isEqualTo("123 Main St");
        assertThat(result.get().getPhone()).isEqualTo("555-1234");
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testPartialUpdateNotFound() {
        // Given
        Client updateClient = new Client();
        updateClient.setId(999L);
        updateClient.setFirstName("John");

        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Client> result = clientService.partialUpdate(updateClient);

        // Then
        assertThat(result).isEmpty();
        verify(clientRepository, times(1)).findById(999L);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testFindAllWhereBorrowedBookIsNull() {
        // Given
        Client client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("John");
        client1.setBorrowedBook(null);

        Client client2 = new Client();
        client2.setId(2L);
        client2.setFirstName("Jane");
        BorrowedBook borrowedBook = new BorrowedBook();
        client2.setBorrowedBook(borrowedBook);

        Client client3 = new Client();
        client3.setId(3L);
        client3.setFirstName("Bob");
        client3.setBorrowedBook(null);

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client1, client2, client3));

        // When
        List<Client> result = clientService.findAllWhereBorrowedBookIsNull();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Client::getId).containsExactlyInAnyOrder(1L, 3L);
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testFindOne() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When
        Optional<Client> result = clientService.findOne(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testFindOneNotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Client> result = clientService.findOne(999L);

        // Then
        assertThat(result).isEmpty();
        verify(clientRepository, times(1)).findById(999L);
    }

    @Test
    void testDelete() {
        // Given
        doNothing().when(clientRepository).deleteById(1L);

        // When
        clientService.delete(1L);

        // Then
        verify(clientRepository, times(1)).deleteById(1L);
    }
}
