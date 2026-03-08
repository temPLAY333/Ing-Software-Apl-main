import { Book } from './book.model';
import { Client } from './client.model';

export interface BorrowedBook {
  id?: number;
  borrowDate?: string;
  book?: Book;
  client?: Client;
}
