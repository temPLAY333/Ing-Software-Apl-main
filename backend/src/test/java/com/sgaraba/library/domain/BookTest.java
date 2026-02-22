package com.sgaraba.library.domain;

import static com.sgaraba.library.domain.AuthorTestSamples.*;
import static com.sgaraba.library.domain.BookTestSamples.*;
import static com.sgaraba.library.domain.BorrowedBookTestSamples.*;
import static com.sgaraba.library.domain.PublisherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sgaraba.library.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Book.class);
        Book book1 = getBookSample1();
        Book book2 = new Book();
        assertThat(book1).isNotEqualTo(book2);

        book2.setId(book1.getId());
        assertThat(book1).isEqualTo(book2);

        book2 = getBookSample2();
        assertThat(book1).isNotEqualTo(book2);
    }

    @Test
    void publisherTest() {
        Book book = getBookRandomSampleGenerator();
        Publisher publisherBack = getPublisherRandomSampleGenerator();

        book.setPublisher(publisherBack);
        assertThat(book.getPublisher()).isEqualTo(publisherBack);

        book.publisher(null);
        assertThat(book.getPublisher()).isNull();
    }

    @Test
    void authorTest() {
        Book book = getBookRandomSampleGenerator();
        Author authorBack = getAuthorRandomSampleGenerator();

        book.addAuthor(authorBack);
        assertThat(book.getAuthors()).containsOnly(authorBack);

        book.removeAuthor(authorBack);
        assertThat(book.getAuthors()).doesNotContain(authorBack);

        book.authors(new HashSet<>(Set.of(authorBack)));
        assertThat(book.getAuthors()).containsOnly(authorBack);

        book.setAuthors(new HashSet<>());
        assertThat(book.getAuthors()).doesNotContain(authorBack);
    }

    @Test
    void borrowedBookTest() {
        Book book = getBookRandomSampleGenerator();
        BorrowedBook borrowedBookBack = getBorrowedBookRandomSampleGenerator();

        book.setBorrowedBook(borrowedBookBack);
        assertThat(book.getBorrowedBook()).isEqualTo(borrowedBookBack);
        assertThat(borrowedBookBack.getBook()).isEqualTo(book);

        book.borrowedBook(null);
        assertThat(book.getBorrowedBook()).isNull();
        assertThat(borrowedBookBack.getBook()).isNull();
    }
}
