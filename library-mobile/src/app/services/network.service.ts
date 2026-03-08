import { Injectable } from '@angular/core';
import { Network } from '@capacitor/network';
import { BehaviorSubject, Observable, fromEvent, merge, of } from 'rxjs';
import { map } from 'rxjs/operators';

// Declaración para extender Window con Capacitor
declare global {
  interface Window {
    Capacitor?: any;
  }
}

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  private networkStatus = new BehaviorSubject<boolean>(true);

  constructor() {
    this.initNetworkListener();
  }

  async initNetworkListener(): Promise<void> {
    // Obtener estado inicial
    const status = await Network.getStatus();
    this.networkStatus.next(status.connected);

    // Escuchar cambios de red
    Network.addListener('networkStatusChange', (status) => {
      this.networkStatus.next(status.connected);
      if (status.connected) {
        console.log('✅ Conexión restaurada');
      } else {
        console.log('⚠️ Sin conexión a internet');
      }
    });

    // Fallback para navegadores que no soporten Capacitor
    if (typeof window !== 'undefined' && !window.Capacitor) {
      merge(
        of(navigator.onLine),
        fromEvent(window, 'online').pipe(map(() => true)),
        fromEvent(window, 'offline').pipe(map(() => false))
      ).subscribe(isOnline => {
        this.networkStatus.next(isOnline);
      });
    }
  }

  // Observable del estado de la red
  get isOnline$(): Observable<boolean> {
    return this.networkStatus.asObservable();
  }

  // Verificar estado actual
  get isOnline(): boolean {
    return this.networkStatus.value;
  }

  // Verificar estado actual de forma asíncrona
  async checkNetworkStatus(): Promise<boolean> {
    try {
      const status = await Network.getStatus();
      return status.connected;
    } catch (error) {
      // Fallback al API del navegador
      return navigator.onLine;
    }
  }
}
