import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpResponse } from '@angular/common/http';

import SharedModule from 'app/shared/shared.module';
import { IBorrowedBook } from '../borrowed-book.model';
import { BorrowedBookService } from '../service/borrowed-book.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';

@Component({
  selector: 'jhi-borrow-book',
  templateUrl: './borrow-book.component.html',
  styleUrl: './borrow-book.component.scss',
  imports: [SharedModule, RouterModule, FormsModule, ReactiveFormsModule],
})
export default class BorrowBookComponent implements OnInit {
  availableBooks = signal<IBook[]>([]);
  clients = signal<IClient[]>([]);
  borrowedBooks = signal<IBorrowedBook[]>([]);
  isLoading = signal(false);
  success = signal(false);
  error = signal(false);

  borrowForm = new FormGroup({
    clientId: new FormControl('', [Validators.required]),
    bookId: new FormControl('', [Validators.required]),
    borrowDate: new FormControl(new Date().toISOString().split('T')[0], [Validators.required]),
  });

  private readonly borrowedBookService = inject(BorrowedBookService);
  private readonly bookService = inject(BookService);
  private readonly clientService = inject(ClientService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.loadAvailableBooks();
    this.loadClients();
    this.loadBorrowedBooks();
  }

  loadAvailableBooks(): void {
    this.bookService.query().subscribe({
      next: (res: HttpResponse<IBook[]>) => {
        this.availableBooks.set((res.body ?? []).filter((book: IBook) => (book.copies ?? 0) > 0));
      },
    });
  }

  loadClients(): void {
    this.clientService.query().subscribe({
      next: (res: HttpResponse<IClient[]>) => {
        this.clients.set(res.body ?? []);
      },
    });
  }

  loadBorrowedBooks(): void {
    this.isLoading.set(true);
    this.borrowedBookService.query().subscribe({
      next: (res: HttpResponse<IBorrowedBook[]>) => {
        this.borrowedBooks.set(res.body ?? []);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  borrowBook(): void {
    if (this.borrowForm.valid) {
      const formValue = this.borrowForm.value;
      const borrowedBook: any = {
        borrowDate: formValue.borrowDate,
        book: this.availableBooks().find((b: IBook) => b.id === Number(formValue.bookId)),
        client: this.clients().find((c: IClient) => c.id === Number(formValue.clientId)),
      };

      this.borrowedBookService.create(borrowedBook).subscribe({
        next: () => {
          this.success.set(true);
          this.error.set(false);
          this.borrowForm.reset();
          this.loadAvailableBooks();
          this.loadBorrowedBooks();
          setTimeout(() => this.success.set(false), 3000);
        },
        error: () => {
          this.error.set(true);
          this.success.set(false);
        },
      });
    }
  }

  returnBook(borrowedBook: IBorrowedBook): void {
    if (borrowedBook.id && confirm('¿Confirmar devolución del libro?')) {
      this.borrowedBookService.delete(borrowedBook.id).subscribe({
        next: () => {
          this.loadAvailableBooks();
          this.loadBorrowedBooks();
        },
      });
    }
  }

  trackId(index: number, item: IBorrowedBook): number {
    return item.id!;
  }
}
