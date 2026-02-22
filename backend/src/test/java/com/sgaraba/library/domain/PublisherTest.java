package com.sgaraba.library.domain;

import static com.sgaraba.library.domain.BookTestSamples.*;
import static com.sgaraba.library.domain.PublisherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sgaraba.library.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PublisherTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Publisher.class);
        Publisher publisher1 = getPublisherSample1();
        Publisher publisher2 = new Publisher();
        assertThat(publisher1).isNotEqualTo(publisher2);

        publisher2.setId(publisher1.getId());
        assertThat(publisher1).isEqualTo(publisher2);

        publisher2 = getPublisherSample2();
        assertThat(publisher1).isNotEqualTo(publisher2);
    }

    @Test
    void bookTest() {
        Publisher publisher = getPublisherRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        publisher.setBook(bookBack);
        assertThat(publisher.getBook()).isEqualTo(bookBack);
        assertThat(bookBack.getPublisher()).isEqualTo(publisher);

        publisher.book(null);
        assertThat(publisher.getBook()).isNull();
        assertThat(bookBack.getPublisher()).isNull();
    }
}
