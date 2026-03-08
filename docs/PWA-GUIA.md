# Guía de PWA - Library Mobile

## 📱 Progressive Web App Completada

La aplicación Ionic ha sido convertida en una **Progressive Web App (PWA)** completamente funcional con soporte offline.

## ✅ Características Implementadas

### 🔌 Funcionalidad Offline
- **Cache automático** de todos los assets (HTML, CSS, JS)
- **Cache inteligente** de datos de la API
- **Almacenamiento local** persistente con Capacitor Preferences
- **Fallback automático** cuando no hay conexión

### 📡 Detección de Red
- **Indicador visual** del estado de conexión (Online/Offline)
- **Banner informativo** en modo offline
- **Recarga automática** al restaurar conexión
- **Sincronización** de datos cuando vuelve la red

### 🔄 Actualizaciones Automáticas
- **Verificación automática** cada 6 horas
- **Prompt al usuario** para actualizar versión
- **Instalación en segundo plano**

### 📦 Instalable como App Nativa
- **Modo standalone** (sin barra del navegador)
- **Icono en pantalla de inicio**
- **Splash screen** personalizado
- **Orientación portrait** por defecto

## 🚀 Cómo Usar

### 1. Build de Producción

```bash
cd library-mobile

# Build de producción (activa Service Worker)
npx ng build --configuration=production

# La salida estará en www/
```

### 2. Servir la PWA Localmente

Para probar la PWA necesitas un servidor HTTP:

```powershell
# Opción 1: http-server (instalar si no lo tienes)
npm install -g http-server
cd www
http-server -p 8100

# Opción 2: Python
cd www
python -m http.server 8100

# Opción 3: PHP
cd www
php -S localhost:8100
```

### 3. Probar en el Navegador

1. Abre `http://localhost:8100` en Chrome
2. Abre DevTools (F12) > Application > Service Workers
3. Verifica que el service worker esté activo
4. Ve a Application > Manifest para ver la configuración PWA

### 4. Probar Modo Offline

**Método 1: DevTools**
1. Abre DevTools (F12)
2. Ve a Network tab
3. Selecciona "Offline" en el dropdown de throttling
4. Recarga la página - debería funcionar sin conexión

**Método 2: Red real**
1. Accede a la app normalmente
2. Navega a la lista de libros (se cachearán)
3. Desconecta WiFi/Ethernet
4. Recarga la página - verás el badge "Offline" y los datos cacheados

### 5. Instalar como App

**En Chrome Desktop:**
1. Abre la PWA en Chrome
2. Haz clic en el icono "+" en la barra de direcciones
3. O ve a Menú > Instalar Library Mobile

**En Android:**
1. Abre la PWA en Chrome
2. Aparecerá un banner "Agregar a pantalla de inicio"
3. O ve a Menú > Agregar a pantalla de inicio

**En iOS:**
1. Abre la PWA en Safari
2. Toca el botón "Compartir"
3. Selecciona "Agregar a pantalla de inicio"

## 🔧 Configuración

### Service Worker (ngsw-config.json)

```json
{
  "assetGroups": [
    {
      "name": "app",
      "installMode": "prefetch",  // Descarga inmediata
      "resources": { ... }
    }
  ],
  "dataGroups": [
    {
      "name": "api-books",
      "cacheConfig": {
        "maxSize": 100,           // Máx 100 libros en cache
        "maxAge": "1h",           // Cache válido por 1 hora
        "strategy": "freshness"   // Intenta red primero
      }
    }
  ]
}
```

### Manifest (manifest.webmanifest)

```json
{
  "name": "Library Mobile",
  "short_name": "Library",
  "theme_color": "#3880ff",      // Color de tema
  "background_color": "#ffffff", // Color de fondo
  "display": "standalone",       // Modo app nativa
  "start_url": "/"
}
```

## 📊 Estrategias de Cache

### 1. App Shell (Prefetch)
Todos los archivos estáticos se descargan en la primera visita:
- `index.html`
- CSS compilado
- JavaScript bundles
- Manifest

### 2. Assets (Lazy)
Los assets se cachean cuando se usan:
- Imágenes en `/assets`
- Iconos
- Fuentes

### 3. API Data (Freshness)
Para datos de la API (libros, autores):
- **Online**: Intenta obtener de la red primero
- Si falla o timeout (10s): Usa cache
- **Offline**: Usa cache directamente
- Cache válido por 1 hora

## 🐛 Debugging

### Ver Service Worker activo
```javascript
// En la consola del navegador
navigator.serviceWorker.getRegistrations().then(regs => {
  regs.forEach(reg => console.log(reg));
});
```

### Verificar cache
```javascript
// Ver todas las caches
caches.keys().then(keys => console.log(keys));

// Ver contenido de una cache
caches.open('ngsw:app:cache').then(cache => {
  cache.keys().then(keys => console.log(keys));
});
```

### Limpiar cache
```javascript
// Eliminar todas las caches
caches.keys().then(keys => {
  keys.forEach(key => caches.delete(key));
});

// Desregistrar service worker
navigator.serviceWorker.getRegistrations().then(regs => {
  regs.forEach(reg => reg.unregister());
});
```

## 📱 Servicios Implementados

### StorageService
```typescript
// Guardar datos
await storageService.set('books', booksArray);

// Obtener datos
const books = await storageService.get('books');

// Eliminar
await storageService.remove('books');

// Limpiar todo
await storageService.clear();
```

### NetworkService
```typescript
// Suscribirse al estado de red
networkService.isOnline$.subscribe(isOnline => {
  console.log(isOnline ? 'Online' : 'Offline');
});

// Verificar estado actual
if (networkService.isOnline) {
  // Hacer petición a API
}
```

### PwaService
```typescript
// Verificar actualizaciones manualmente
const hasUpdate = await pwaService.checkUpdate();

// Activar actualización
await pwaService.activateUpdate();
```

## 📈 Métricas de Build

Build de producción actual:
- **Initial bundle**: 808.30 kB (186.27 kB gzipped)
- **Lazy chunks**: ~27 KB total
- **Service Worker**: ~10 KB
- **Total assets**: ~850 KB

## 🔍 Verificación de Funcionalidad

### Checklist de Pruebas

- [ ] Service Worker registrado correctamente
- [ ] Manifest detectado en DevTools
- [ ] App instalable (aparece prompt)
- [ ] Cache de assets funciona
- [ ] Navegación offline funciona
- [ ] Badge "Offline" aparece al desconectar
- [ ] Datos de libros se cachean
- [ ] Recarga automática al volver online
- [ ] Actualizaciones se detectan

## 🎯 Próximos Pasos Opcionales

1. **Background Sync**
   - Guardar operaciones pendientes offline
   - Sincronizar cuando vuelva conexión

2. **Push Notifications**
   - Notificar nuevos libros
   - Recordatorios de devolución

3. **Advanced Caching**
   - Cache de imágenes de portadas
   - Estrategias por tipo de contenido

4. **Analytics**
   - Rastrear uso offline
   - Métricas de instalación

## 📚 Recursos

- [Angular Service Worker](https://angular.io/guide/service-worker-intro)
- [Ionic PWA Docs](https://ionicframework.com/docs/angular/pwa)
- [Capacitor Preferences](https://capacitorjs.com/docs/apis/preferences)
- [Capacitor Network](https://capacitorjs.com/docs/apis/network)
- [Web.dev PWA](https://web.dev/progressive-web-apps/)

---

**Última actualización:** 8 de marzo de 2026
**Versión:** 1.0
**Estado:** ✅ Completado
