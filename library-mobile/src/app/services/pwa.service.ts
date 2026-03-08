import { Injectable, ApplicationRef } from '@angular/core';
import { SwUpdate, VersionReadyEvent } from '@angular/service-worker';
import { filter, first } from 'rxjs/operators';
import { concat, interval } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PwaService {
  constructor(
    private swUpdate: SwUpdate,
    private appRef: ApplicationRef
  ) {
    if (this.swUpdate.isEnabled) {
      this.checkForUpdates();
      this.handleUpdates();
    }
  }

  // Verificar actualizaciones cada 6 horas
  private checkForUpdates(): void {
    const appIsStable$ = this.appRef.isStable.pipe(
      first(isStable => isStable === true)
    );

    const everySixHours$ = interval(6 * 60 * 60 * 1000);
    const everySixHoursOnceAppIsStable$ = concat(appIsStable$, everySixHours$);

    everySixHoursOnceAppIsStable$.subscribe(async () => {
      try {
        const updateFound = await this.swUpdate.checkForUpdate();
        if (updateFound) {
          console.log('Nueva versión disponible');
        }
      } catch (err) {
        console.error('Error checking for updates:', err);
      }
    });
  }

  // Manejar actualizaciones disponibles
  private handleUpdates(): void {
    this.swUpdate.versionUpdates
      .pipe(
        filter((evt): evt is VersionReadyEvent => evt.type === 'VERSION_READY')
      )
      .subscribe(evt => {
        console.log('Current version:', evt.currentVersion);
        console.log('Available version:', evt.latestVersion);

        if (confirm('Nueva versión disponible. ¿Desea actualizar ahora?')) {
          this.activateUpdate();
        }
      });
  }

  // Activar actualización
  async activateUpdate(): Promise<void> {
    try {
      await this.swUpdate.activateUpdate();
      document.location.reload();
    } catch (err) {
      console.error('Error activating update:', err);
    }
  }

  // Verificar si hay actualizaciones manualmente
  async checkUpdate(): Promise<boolean> {
    try {
      return await this.swUpdate.checkForUpdate();
    } catch (err) {
      console.error('Error checking for update:', err);
      return false;
    }
  }
}
