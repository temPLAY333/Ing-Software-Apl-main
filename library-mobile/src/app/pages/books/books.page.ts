import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { IonContent, IonHeader, IonTitle, IonToolbar, IonList, IonItem, IonLabel,
         IonButton, IonButtons, IonIcon, IonRefresher, IonRefresherContent, IonSpinner, IonBadge } from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { logOutOutline, bookOutline, refreshOutline, cloudOfflineOutline, cloudDoneOutline } from 'ionicons/icons';
import { BookService } from '../../services/book.service';
import { AuthService } from '../../services/auth.service';
import { NetworkService } from '../../services/network.service';
import { Book } from '../../models/book.model';

@Component({
  selector: 'app-books',
  templateUrl: './books.page.html',
  styleUrls: ['./books.page.scss'],
  standalone: true,
  imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule,
            IonList, IonItem, IonLabel, IonButton, IonButtons, IonIcon,
            IonRefresher, IonRefresherContent, IonSpinner, IonBadge]
})
export class BooksPage implements OnInit {
  books: Book[] = [];
  loading = false;
  isOnline = true;
  isBackendAvailable = true;
  private previousOnlineState = true;

  constructor(
    private bookService: BookService,
    private authService: AuthService,
    private networkService: NetworkService,
    private router: Router
  ) {
    addIcons({ logOutOutline, bookOutline, refreshOutline, cloudOfflineOutline, cloudDoneOutline });
  }

  ngOnInit() {
    this.loadBooks();

    // Suscribirse al estado de la red
    this.networkService.isOnline$.subscribe(isOnline => {
      const wasOfflineNowOnline = !this.previousOnlineState && isOnline;
      this.isOnline = isOnline;
      this.previousOnlineState = isOnline;

      // Solo recargar si acabamos de recuperar conexión
      if (wasOfflineNowOnline) {
        console.log('✅ Conexión restaurada, recargando libros...');
        this.loadBooks();
      }
    });

    // ✅ MEJORA PWA: Suscribirse al estado del backend
    this.networkService.backendStatus$.subscribe(isAvailable => {
      this.isBackendAvailable = isAvailable;
      if (!isAvailable) {
        console.log('⚠️ Backend no disponible, usando caché');
      }
    });
  }

  loadBooks() {
    this.loading = true;
    this.bookService.findAll().subscribe({
      next: (books: Book[]) => {
        this.books = books;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error cargando libros:', error);
        this.loading = false;
      }
    });
  }

  onBookClick(book: Book) {
    this.router.navigate(['/book-detail', book.id]);
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  handleRefresh(event: Event) {
    this.loadBooks();
    setTimeout(() => {
      (event.target as any).complete();
    }, 1000);
  }

  getAuthorsNames(book: Book): string {
    if (!book.authors || book.authors.length === 0) {
      return '';
    }
    return book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ');
  }
}
