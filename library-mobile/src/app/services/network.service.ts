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

// Variable de entorno local para evitar importar environment
const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  private networkStatus = new BehaviorSubject<boolean>(true);
  private backendStatus = new BehaviorSubject<boolean>(true);

  constructor() {
    this.initNetworkListener();
    this.startBackendMonitoring();
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

  // ✅ MEJORA PWA: Monitoreo del backend
  private startBackendMonitoring(): void {
    // Verificar cada 30 segundos si hay conexión
    setInterval(() => {
      if (this.networkStatus.value) {
        this.checkBackendHealth();
      } else {
        this.backendStatus.next(false);
      }
    }, 30000);

    // Verificación inicial
    if (this.networkStatus.value) {
      this.checkBackendHealth();
    }
  }

  // Verificar si el backend está disponible
  async checkBackendHealth(): Promise<boolean> {
    if (!navigator.onLine) {
      this.backendStatus.next(false);
      return false;
    }

    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000); // 5s timeout

      const response = await fetch(`${API_URL}/authenticate`, {
        method: 'HEAD',
        cache: 'no-cache',
        signal: controller.signal
      });

      clearTimeout(timeoutId);
      const isAvailable = response.ok || response.status === 401; // 401 = backend funciona pero no autenticado
      this.backendStatus.next(isAvailable);
      return isAvailable;
    } catch (error) {
      console.log('⚠️ Backend no disponible:', error);
      this.backendStatus.next(false);
      return false;
    }
  }

  // Observable del estado del backend
  get backendStatus$(): Observable<boolean> {
    return this.backendStatus.asObservable();
  }

  // Verificar estado actual del backend
  get isBackendAvailable(): boolean {
    return this.backendStatus.value;
  }
}
