package com.sgaraba.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 5, max = 13)
    @Column(name = "isbn", length = 13, nullable = false, unique = true)
    private String isbn;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(min = 4, max = 50)
    @Column(name = "publish_year", length = 50, nullable = false)
    private String publishYear;

    @NotNull
    @Column(name = "copies", nullable = false)
    private Integer copies;

    @Lob
    @Column(name = "cover")
    private byte[] cover;

    @Column(name = "cover_content_type")
    private String coverContentType;

    @JsonIgnoreProperties(value = { "book" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Publisher publisher;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rel_book__author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    @JsonIgnoreProperties(value = { "book", "client" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "book")
    private BorrowedBook borrowedBook;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public Book isbn(String isbn) {
        this.setIsbn(isbn);
        return this;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return this.name;
    }

    public Book name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishYear() {
        return this.publishYear;
    }

    public Book publishYear(String publishYear) {
        this.setPublishYear(publishYear);
        return this;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public Integer getCopies() {
        return this.copies;
    }

    public Book copies(Integer copies) {
        this.setCopies(copies);
        return this;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public byte[] getCover() {
        return this.cover;
    }

    public Book cover(byte[] cover) {
        this.setCover(cover);
        return this;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return this.coverContentType;
    }

    public Book coverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
        return this;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public Publisher getPublisher() {
        return this.publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Book publisher(Publisher publisher) {
        this.setPublisher(publisher);
        return this;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthor(Author author) {
        this.authors.add(author);
        return this;
    }

    public Book removeAuthor(Author author) {
        this.authors.remove(author);
        return this;
    }

    public BorrowedBook getBorrowedBook() {
        return this.borrowedBook;
    }

    public void setBorrowedBook(BorrowedBook borrowedBook) {
        if (this.borrowedBook != null) {
            this.borrowedBook.setBook(null);
        }
        if (borrowedBook != null) {
            borrowedBook.setBook(this);
        }
        this.borrowedBook = borrowedBook;
    }

    public Book borrowedBook(BorrowedBook borrowedBook) {
        this.setBorrowedBook(borrowedBook);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return getId() != null && getId().equals(((Book) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", isbn='" + getIsbn() + "'" +
            ", name='" + getName() + "'" +
            ", publishYear='" + getPublishYear() + "'" +
            ", copies=" + getCopies() +
            ", cover='" + getCover() + "'" +
            ", coverContentType='" + getCoverContentType() + "'" +
            "}";
    }
}
