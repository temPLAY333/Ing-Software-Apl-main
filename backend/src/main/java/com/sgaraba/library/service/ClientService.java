package com.sgaraba.library.service;

import com.sgaraba.library.domain.Client;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.sgaraba.library.domain.Client}.
 */
public interface ClientService {
    /**
     * Save a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    Client save(Client client);

    /**
     * Updates a client.
     *
     * @param client the entity to update.
     * @return the persisted entity.
     */
    Client update(Client client);

    /**
     * Partially updates a client.
     *
     * @param client the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Client> partialUpdate(Client client);

    /**
     * Get all the Client where BorrowedBook is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<Client> findAllWhereBorrowedBookIsNull();

    /**
     * Get the "id" client.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Client> findOne(Long id);

    /**
     * Delete the "id" client.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
