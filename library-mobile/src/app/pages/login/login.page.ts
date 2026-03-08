import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar, IonCard, IonCardContent, IonCardHeader,
         IonCardTitle, IonItem, IonLabel, IonInput, IonButton, IonToast } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule,
            ReactiveFormsModule, IonCard, IonCardContent, IonCardHeader, IonCardTitle,
            IonItem, IonLabel, IonInput, IonButton, IonToast]
})
export class LoginPage {
  loginForm: FormGroup;
  showToast = false;
  toastMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['admin', Validators.required],
      password: ['admin', Validators.required]
    });
  }

  onLogin() {
    if (this.loginForm.valid) {
      const credentials = this.loginForm.value;
      this.authService.login(credentials).subscribe({
        next: (response) => {
          this.toastMessage = 'Login successful!';
          this.showToast = true;
          setTimeout(() => {
            this.router.navigate(['/books']);
          }, 500);
        },
        error: (error) => {
          this.toastMessage = 'Login failed. Please check your credentials.';
          this.showToast = true;
        }
      });
    }
  }
}
