import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, from, of } from 'rxjs';
import { tap, catchError, switchMap } from 'rxjs/operators';
import { Book } from '../models/book.model';
import { environment } from '../../environments/environment';
import { StorageService } from './storage.service';
import { NetworkService } from './network.service';

const BOOKS_STORAGE_KEY = 'cached_books';
const BOOK_DETAIL_PREFIX = 'cached_book_';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private apiUrl = `${environment.apiUrl}/books`;

  constructor(
    private http: HttpClient,
    private storage: StorageService,
    private network: NetworkService
  ) {}

  findAll(params?: any): Observable<Book[]> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        httpParams = httpParams.set(key, params[key]);
      });
    }

    // Si hay conexión, obtener de la API
    if (this.network.isOnline) {
      return this.http.get<Book[]>(this.apiUrl, { params: httpParams }).pipe(
        tap(books => {
          // Guardar lista completa en caché
          this.storage.set(BOOKS_STORAGE_KEY, books);

          // ✅ MEJORA PWA: Precachear cada libro individual para que funcionen offline
          books.forEach(book => {
            this.storage.set(`${BOOK_DETAIL_PREFIX}${book.id}`, book);
          });
          console.log(`✅ Precacheados ${books.length} libros para uso offline`);
        }),
        catchError(error => {
          console.error('Error fetching books from API, loading from cache:', error);
          return this.getBooksFromCache();
        })
      );
    } else {
      // Sin conexión, obtener del cache
      console.log('📴 Offline mode: Loading books from cache');
      return this.getBooksFromCache();
    }
  }

  findOne(id: number): Observable<Book> {
    // Si hay conexión, obtener de la API
    if (this.network.isOnline) {
      return this.http.get<Book>(`${this.apiUrl}/${id}`).pipe(
        tap(book => {
          // Guardar en cache
          this.storage.set(`${BOOK_DETAIL_PREFIX}${id}`, book);
        }),
        catchError(error => {
          console.error('Error fetching book from API, loading from cache:', error);
          return this.getBookFromCache(id);
        })
      );
    } else {
      // Sin conexión, obtener del cache
      console.log('📴 Offline mode: Loading book from cache');
      return this.getBookFromCache(id);
    }
  }

  create(book: Book): Observable<Book> {
    return this.http.post<Book>(this.apiUrl, book).pipe(
      tap(() => {
        // Invalidar cache de libros
        this.storage.remove(BOOKS_STORAGE_KEY);
      })
    );
  }

  update(book: Book): Observable<Book> {
    return this.http.put<Book>(`${this.apiUrl}/${book.id}`, book).pipe(
      tap(() => {
        // Actualizar cache
        this.storage.set(`${BOOK_DETAIL_PREFIX}${book.id}`, book);
        this.storage.remove(BOOKS_STORAGE_KEY);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        // Eliminar del cache
        this.storage.remove(`${BOOK_DETAIL_PREFIX}${id}`);
        this.storage.remove(BOOKS_STORAGE_KEY);
      })
    );
  }

  // Métodos privados para manejo de cache
  private getBooksFromCache(): Observable<Book[]> {
    return from(this.storage.get(BOOKS_STORAGE_KEY)).pipe(
      switchMap(books => {
        if (books && books.length > 0) {
          return of(books);
        } else {
          return of([]);
        }
      })
    );
  }

  private getBookFromCache(id: number): Observable<Book> {
    return from(this.storage.get(`${BOOK_DETAIL_PREFIX}${id}`)).pipe(
      switchMap(book => {
        if (book) {
          return of(book);
        } else {
          throw new Error('Book not found in cache');
        }
      })
    );
  }
}
