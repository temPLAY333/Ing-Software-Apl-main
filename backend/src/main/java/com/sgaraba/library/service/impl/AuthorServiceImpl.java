package com.sgaraba.library.service.impl;

import com.sgaraba.library.domain.Author;
import com.sgaraba.library.repository.AuthorRepository;
import com.sgaraba.library.service.AuthorService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sgaraba.library.domain.Author}.
 */
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author save(Author author) {
        LOG.debug("Request to save Author : {}", author);
        return authorRepository.save(author);
    }

    @Override
    public Author update(Author author) {
        LOG.debug("Request to update Author : {}", author);
        return authorRepository.save(author);
    }

    @Override
    public Optional<Author> partialUpdate(Author author) {
        LOG.debug("Request to partially update Author : {}", author);

        return authorRepository
            .findById(author.getId())
            .map(existingAuthor -> {
                if (author.getFirstName() != null) {
                    existingAuthor.setFirstName(author.getFirstName());
                }
                if (author.getLastName() != null) {
                    existingAuthor.setLastName(author.getLastName());
                }

                return existingAuthor;
            })
            .map(authorRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> findOne(Long id) {
        LOG.debug("Request to get Author : {}", id);
        return authorRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Author : {}", id);
        authorRepository.deleteById(id);
    }
}
