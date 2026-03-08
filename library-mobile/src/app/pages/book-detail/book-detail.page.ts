import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
  IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner,
  IonLabel, IonItem, IonList, IonIcon, IonBadge, IonButton
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { arrowBack, bookOutline, personOutline, businessOutline, calendarOutline, layersOutline, alertCircleOutline, barcodeOutline } from 'ionicons/icons';
import { BookService } from '../../services/book.service';
import { Book } from '../../models/book.model';

@Component({
  selector: 'app-book-detail',
  templateUrl: './book-detail.page.html',
  styleUrls: ['./book-detail.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule,
    IonButtons, IonBackButton, IonCard, IonCardHeader, IonCardTitle, IonCardContent,
    IonSpinner, IonLabel, IonItem, IonList, IonIcon, IonBadge, IonButton
  ]
})
export class BookDetailPage implements OnInit {
  book: Book | null = null;
  loading = true;
  error = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bookService: BookService
  ) {
    addIcons({ arrowBack, bookOutline, personOutline, businessOutline, calendarOutline, layersOutline, alertCircleOutline, barcodeOutline });
  }

  ngOnInit() {
    const bookId = this.route.snapshot.paramMap.get('id');
    if (bookId) {
      this.loadBook(+bookId);
    } else {
      this.error = true;
      this.loading = false;
    }
  }

  loadBook(id: number) {
    this.loading = true;
    this.error = false;
    this.bookService.findOne(id).subscribe({
      next: (data) => {
        this.book = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading book:', error);
        this.error = true;
        this.loading = false;
      }
    });
  }

  getAuthorsNames(): string {
    if (!this.book || !this.book.authors || this.book.authors.length === 0) {
      return 'No authors';
    }
    return this.book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ');
  }

  goBack() {
    this.router.navigate(['/books']);
  }

}
