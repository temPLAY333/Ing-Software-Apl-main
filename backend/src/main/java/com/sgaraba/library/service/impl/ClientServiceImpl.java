package com.sgaraba.library.service.impl;

import com.sgaraba.library.domain.Client;
import com.sgaraba.library.repository.ClientRepository;
import com.sgaraba.library.service.ClientService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sgaraba.library.domain.Client}.
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client save(Client client) {
        LOG.debug("Request to save Client : {}", client);
        return clientRepository.save(client);
    }

    @Override
    public Client update(Client client) {
        LOG.debug("Request to update Client : {}", client);
        return clientRepository.save(client);
    }

    @Override
    public Optional<Client> partialUpdate(Client client) {
        LOG.debug("Request to partially update Client : {}", client);

        return clientRepository
            .findById(client.getId())
            .map(existingClient -> {
                if (client.getFirstName() != null) {
                    existingClient.setFirstName(client.getFirstName());
                }
                if (client.getLastName() != null) {
                    existingClient.setLastName(client.getLastName());
                }
                if (client.getEmail() != null) {
                    existingClient.setEmail(client.getEmail());
                }
                if (client.getAddress() != null) {
                    existingClient.setAddress(client.getAddress());
                }
                if (client.getPhone() != null) {
                    existingClient.setPhone(client.getPhone());
                }

                return existingClient;
            })
            .map(clientRepository::save);
    }

    /**
     *  Get all the clients where BorrowedBook is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Client> findAllWhereBorrowedBookIsNull() {
        LOG.debug("Request to get all clients where BorrowedBook is null");
        return StreamSupport.stream(clientRepository.findAll().spliterator(), false)
            .filter(client -> client.getBorrowedBooks() == null || client.getBorrowedBooks().isEmpty())
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findOne(Long id) {
        LOG.debug("Request to get Client : {}", id);
        return clientRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Client : {}", id);
        clientRepository.deleteById(id);
    }
}
