package com.sgaraba.library.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.sgaraba.library.domain.Book} entity. This class is used
 * in {@link com.sgaraba.library.web.rest.BookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter isbn;

    private StringFilter name;

    private StringFilter publishYear;

    private IntegerFilter copies;

    private LongFilter publisherId;

    private LongFilter authorId;

    private LongFilter borrowedBookId;

    private Boolean distinct;

    public BookCriteria() {}

    public BookCriteria(BookCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.isbn = other.optionalIsbn().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.publishYear = other.optionalPublishYear().map(StringFilter::copy).orElse(null);
        this.copies = other.optionalCopies().map(IntegerFilter::copy).orElse(null);
        this.publisherId = other.optionalPublisherId().map(LongFilter::copy).orElse(null);
        this.authorId = other.optionalAuthorId().map(LongFilter::copy).orElse(null);
        this.borrowedBookId = other.optionalBorrowedBookId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getIsbn() {
        return isbn;
    }

    public Optional<StringFilter> optionalIsbn() {
        return Optional.ofNullable(isbn);
    }

    public StringFilter isbn() {
        if (isbn == null) {
            setIsbn(new StringFilter());
        }
        return isbn;
    }

    public void setIsbn(StringFilter isbn) {
        this.isbn = isbn;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getPublishYear() {
        return publishYear;
    }

    public Optional<StringFilter> optionalPublishYear() {
        return Optional.ofNullable(publishYear);
    }

    public StringFilter publishYear() {
        if (publishYear == null) {
            setPublishYear(new StringFilter());
        }
        return publishYear;
    }

    public void setPublishYear(StringFilter publishYear) {
        this.publishYear = publishYear;
    }

    public IntegerFilter getCopies() {
        return copies;
    }

    public Optional<IntegerFilter> optionalCopies() {
        return Optional.ofNullable(copies);
    }

    public IntegerFilter copies() {
        if (copies == null) {
            setCopies(new IntegerFilter());
        }
        return copies;
    }

    public void setCopies(IntegerFilter copies) {
        this.copies = copies;
    }

    public LongFilter getPublisherId() {
        return publisherId;
    }

    public Optional<LongFilter> optionalPublisherId() {
        return Optional.ofNullable(publisherId);
    }

    public LongFilter publisherId() {
        if (publisherId == null) {
            setPublisherId(new LongFilter());
        }
        return publisherId;
    }

    public void setPublisherId(LongFilter publisherId) {
        this.publisherId = publisherId;
    }

    public LongFilter getAuthorId() {
        return authorId;
    }

    public Optional<LongFilter> optionalAuthorId() {
        return Optional.ofNullable(authorId);
    }

    public LongFilter authorId() {
        if (authorId == null) {
            setAuthorId(new LongFilter());
        }
        return authorId;
    }

    public void setAuthorId(LongFilter authorId) {
        this.authorId = authorId;
    }

    public LongFilter getBorrowedBookId() {
        return borrowedBookId;
    }

    public Optional<LongFilter> optionalBorrowedBookId() {
        return Optional.ofNullable(borrowedBookId);
    }

    public LongFilter borrowedBookId() {
        if (borrowedBookId == null) {
            setBorrowedBookId(new LongFilter());
        }
        return borrowedBookId;
    }

    public void setBorrowedBookId(LongFilter borrowedBookId) {
        this.borrowedBookId = borrowedBookId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCriteria that = (BookCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isbn, that.isbn) &&
            Objects.equals(name, that.name) &&
            Objects.equals(publishYear, that.publishYear) &&
            Objects.equals(copies, that.copies) &&
            Objects.equals(publisherId, that.publisherId) &&
            Objects.equals(authorId, that.authorId) &&
            Objects.equals(borrowedBookId, that.borrowedBookId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn, name, publishYear, copies, publisherId, authorId, borrowedBookId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIsbn().map(f -> "isbn=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalPublishYear().map(f -> "publishYear=" + f + ", ").orElse("") +
            optionalCopies().map(f -> "copies=" + f + ", ").orElse("") +
            optionalPublisherId().map(f -> "publisherId=" + f + ", ").orElse("") +
            optionalAuthorId().map(f -> "authorId=" + f + ", ").orElse("") +
            optionalBorrowedBookId().map(f -> "borrowedBookId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
