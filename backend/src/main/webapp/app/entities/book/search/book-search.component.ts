import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { combineLatest } from 'rxjs';

import SharedModule from 'app/shared/shared.module';
import { IBook } from '../book.model';
import { BookService } from '../service/book.service';
import { IPublisher } from 'app/entities/publisher/publisher.model';
import { PublisherService } from 'app/entities/publisher/service/publisher.service';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';

@Component({
  selector: 'jhi-book-search',
  templateUrl: './book-search.component.html',
  styleUrl: './book-search.component.scss',
  imports: [SharedModule, RouterModule, FormsModule, ReactiveFormsModule],
})
export default class BookSearchComponent implements OnInit {
  books = signal<IBook[]>([]);
  publishers = signal<IPublisher[]>([]);
  authors = signal<IAuthor[]>([]);
  isLoading = signal(false);

  searchForm = new FormGroup({
    searchTerm: new FormControl(''),
    publisher: new FormControl(''),
    author: new FormControl(''),
    yearFrom: new FormControl(''),
    yearTo: new FormControl(''),
  });

  private readonly bookService = inject(BookService);
  private readonly publisherService = inject(PublisherService);
  private readonly authorService = inject(AuthorService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.loadAll();
    this.loadPublishers();
    this.loadAuthors();
  }

  loadAll(): void {
    this.isLoading.set(true);
    this.bookService.query().subscribe({
      next: (res: HttpResponse<IBook[]>) => {
        this.books.set(res.body ?? []);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  loadPublishers(): void {
    this.publisherService.query().subscribe({
      next: (res: HttpResponse<IPublisher[]>) => {
        this.publishers.set(res.body ?? []);
      },
    });
  }

  loadAuthors(): void {
    this.authorService.query().subscribe({
      next: (res: HttpResponse<IAuthor[]>) => {
        this.authors.set(res.body ?? []);
      },
    });
  }

  search(): void {
    const formValue = this.searchForm.value;
    let filteredBooks = [...this.books()];

    if (formValue.searchTerm) {
      const term = formValue.searchTerm.toLowerCase();
      // eslint-disable-next-line @typescript-eslint/prefer-nullish-coalescing
      filteredBooks = filteredBooks.filter(book => book.name?.toLowerCase().includes(term) || book.isbn?.toLowerCase().includes(term));
    }

    if (formValue.publisher) {
      filteredBooks = filteredBooks.filter(book => book.publisher?.id === Number(formValue.publisher));
    }

    if (formValue.author) {
      filteredBooks = filteredBooks.filter(book => book.authors?.some((author: IAuthor) => author.id === Number(formValue.author)));
    }

    if (formValue.yearFrom) {
      filteredBooks = filteredBooks.filter(book => book.publishYear && book.publishYear >= formValue.yearFrom!);
    }

    if (formValue.yearTo) {
      filteredBooks = filteredBooks.filter(book => book.publishYear && book.publishYear <= formValue.yearTo!);
    }

    this.books.set(filteredBooks);
  }

  reset(): void {
    this.searchForm.reset();
    this.loadAll();
  }

  viewDetails(book: IBook): void {
    this.router.navigate(['/book', book.id, 'view']);
  }

  trackId(index: number, item: IBook): number {
    return item.id;
  }
}
