package com.sgaraba.library.service;

import com.sgaraba.library.domain.Author;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.sgaraba.library.domain.Author}.
 */
public interface AuthorService {
    /**
     * Save a author.
     *
     * @param author the entity to save.
     * @return the persisted entity.
     */
    Author save(Author author);

    /**
     * Updates a author.
     *
     * @param author the entity to update.
     * @return the persisted entity.
     */
    Author update(Author author);

    /**
     * Partially updates a author.
     *
     * @param author the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Author> partialUpdate(Author author);

    /**
     * Get the "id" author.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Author> findOne(Long id);

    /**
     * Delete the "id" author.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
