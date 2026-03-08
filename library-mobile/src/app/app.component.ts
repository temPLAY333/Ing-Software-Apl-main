import { Component } from '@angular/core';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';
import { PwaService } from './services/pwa.service';
import { NetworkService } from './services/network.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  imports: [IonApp, IonRouterOutlet],
})
export class AppComponent {
  constructor(
    private pwaService: PwaService,
    private networkService: NetworkService
  ) {
    // Inicializar servicios PWA y Network
    console.log('🚀 PWA Service initialized');
  }
}
