package com.sgaraba.library.service.impl;

import com.sgaraba.library.domain.BorrowedBook;
import com.sgaraba.library.repository.BorrowedBookRepository;
import com.sgaraba.library.service.BorrowedBookService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sgaraba.library.domain.BorrowedBook}.
 */
@Service
@Transactional
public class BorrowedBookServiceImpl implements BorrowedBookService {

    private static final Logger LOG = LoggerFactory.getLogger(BorrowedBookServiceImpl.class);

    private final BorrowedBookRepository borrowedBookRepository;

    public BorrowedBookServiceImpl(BorrowedBookRepository borrowedBookRepository) {
        this.borrowedBookRepository = borrowedBookRepository;
    }

    @Override
    public BorrowedBook save(BorrowedBook borrowedBook) {
        LOG.debug("Request to save BorrowedBook : {}", borrowedBook);
        return borrowedBookRepository.save(borrowedBook);
    }

    @Override
    public BorrowedBook update(BorrowedBook borrowedBook) {
        LOG.debug("Request to update BorrowedBook : {}", borrowedBook);
        return borrowedBookRepository.save(borrowedBook);
    }

    @Override
    public Optional<BorrowedBook> partialUpdate(BorrowedBook borrowedBook) {
        LOG.debug("Request to partially update BorrowedBook : {}", borrowedBook);

        return borrowedBookRepository
            .findById(borrowedBook.getId())
            .map(existingBorrowedBook -> {
                if (borrowedBook.getBorrowDate() != null) {
                    existingBorrowedBook.setBorrowDate(borrowedBook.getBorrowDate());
                }

                return existingBorrowedBook;
            })
            .map(borrowedBookRepository::save);
    }

    public Page<BorrowedBook> findAllWithEagerRelationships(Pageable pageable) {
        return borrowedBookRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorrowedBook> findOne(Long id) {
        LOG.debug("Request to get BorrowedBook : {}", id);
        return borrowedBookRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete BorrowedBook : {}", id);
        borrowedBookRepository.deleteById(id);
    }
}
