import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./home/home.page').then((m) => m.HomePage),
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.page').then( m => m.LoginPage)
  },
  {
    path: 'books',
    loadComponent: () => import('./pages/books/books.page').then( m => m.BooksPage)
  },
  {
    path: 'book-detail/:id',
    loadComponent: () => import('./pages/book-detail/book-detail.page').then( m => m.BookDetailPage)
  },
];
