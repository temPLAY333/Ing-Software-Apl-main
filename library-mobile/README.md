# Library Mobile - Progressive Web App

Aplicación móvil Ionic/Angular para el sistema de gestión de biblioteca con **soporte offline completo** (PWA).

## ✨ Características PWA

- 🔌 **Funcionalidad Offline** - Funciona sin conexión a internet
- 📦 **Instalable** - Se puede instalar como app nativa
- 🔄 **Actualizaciones Automáticas** - Detecta y aplica nuevas versiones
- 💾 **Cache Inteligente** - Almacena datos localmente con estrategias optimizadas
- 📡 **Detección de Red** - Indica cuando estás online u offline

## 🚀 Inicio Rápido

### Requisitos Previos
- Node.js y npm instalados  
- Ionic CLI: `npm install -g @ionic/cli`
- Backend JHipster ejecutándose en http://localhost:8080

### Instalación

```bash
cd library-mobile
npm install
```

### Desarrollo

```bash
# Iniciar servidor de desarrollo
ionic serve
# o
npm start

# La aplicación se abrirá en http://localhost:8100
```

### Credenciales de Prueba
- **Usuario:** admin
- **Contraseña:** admin

## 📱 Funcionalidades

### Implementadas ✅
- ✅ Login con autenticación JWT
- ✅ Lista de libros desde API JHipster
- ✅ Detalle de libro (parcial)
- ✅ Logout
- ✅ Pull-to-refresh
- ✅ Manejo de errores con toast messages
- ✅ **PWA con Service Worker**
- ✅ **Cache offline de libros**
- ✅ **Detección de estado de red**
- ✅ **Almacenamiento local persistente**
- ✅ **Indicador visual Online/Offline**
- ✅ **Instalable como app nativa**

## 🏗️ Estructura del Proyecto

```
src/app/
├── models/           # Interfaces TypeScript
│   ├── author.model.ts con cache offline
│   ├── auth-interceptor-fn.ts    # Interceptor HTTP
│   ├── storage.service.ts        # Almacenamiento local (PWA)
│   ├── network.service.ts        # Detección de red (PWA)
│   └── pwa.service.ts           # Gestión de actualizaciones (PWA)
└── pages/            # Páginas
    ├── login/        # Pantalla de login
    ├── books/        # Lista de libros con estado de red
    └── book-detail/  # Detalle de libro
```

### Archivos PWA
```
├── ngsw-config.json          # Configuración Service Worker
├── src/
│   ├── manifest.webmanifest  # Manifest de la PWA
│   └── main.ts               # Bootstrap con Service Worker
└── www/                      # Build de producción
    ├── ngsw-worker.js        # Service Worker compilado
    └── ngsw.json             # Configuración runtime
│   ├── auth.service.ts           # Autenticación JWT
│   ├── book.service.ts           # API de libros
│   └── auth-interceptor-fn.ts    # Interceptor HTTP
└── pages/            # Páginas
    ├── login/        # Pantalla de login
    ├── books/        # Lista de libros
    └── book-detail/  # Detalle de libro
```

## 🔧 Configuración
🌐 PWA - Funcionalidad Offline

### Build de Producción (PWA)

```bash
# Build con Service Worker habilitado
npx ng build --configuration=production

# La salida estará en www/ con el Service Worker
```

### Servir PWA Localmente

```bash
# Necesitas un servi Opcionales

1. **Mejorar PWA:**
   - Background sync para operaciones offline
   - Push notifications
   - Optimizar estrategias de cach
### Probar Modo Offline

1. Abre http://localhost:8100 en Chrome
2. Navega a la lista de libros (se cachearán)
3. Abre DevTools (F12) > Network tab
4. Selecciona "Offline" en throttling
5. Recarga - la app funcionará sin conexión
6. Verás el badge "Offline" y los datos cacheados

### Instalar como App

- **Chrome Desktop:** Haz clic       # Servidor de desarrollo
ionic serve --lab                   # Modo lab (iOS/Android/Web)

# Build
ionic build                          # Build desarrollo
npx ng build --configuration=production  # Build PWA producción

# PWA
http-server www -p 8100             # Servir PWA localmente

# Capacitor (opcional para apps nativas)
ionic cap add android                # Agregar plataforma Android
ionic cap add ios                    # Agregar plataforma iOS
ionic cap sync                       # Sincronizar cambios
ionic cap open android              # Abrir en Android Studio
ionic cap open ios                  # Abrir en Xcode
```

## 📚 Documentación Adicional

- [Guía completa de PWA](../docs/PWA-GUIA.md)
- [Estado del proyecto](../docs/ESTADO-PROYECTO.md)

---

**Versión:** 1.1 (PWA)
**Última actualización:** 8 de marzo de 2026
Configurado en `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### CORS
El backend JHipster ya tiene CORS habilitado para `http://localhost:8100` en modo desarrollo.

## 📝 Próximos Pasos

1. **Convertir a PWA** (Punto 7 del proyecto)
   - Agregar @angular/pwa
   - Configurar Service Worker
   - Implementar funcionalidad offline

2. **Mejorar funcionalidades:**
   - Completar página de detalle de libro
   - Agregar búsqueda de libros
   - Implementar gestión de préstamos
   - Agregar guards de autenticación en rutas

## 🛠️ Comandos Útiles

```bash
# Desarrollo
ionic serve                    # Servidor de desarrollo
ionic serve --lab             # Modo lab (iOS/Android/Web)

# Build
ionic build                    # Build para producción
ionic build --prod            # Build optimizado

# Capacitor (nativo)
ionic cap add ios             # Agregar plataforma iOS
ionic cap add android         # Agregar plataforma Android
ionic cap sync                # Sincronizar cambios
ionic cap open ios            # Abrir en Xcode
ionic cap open android        # Abrir en Android Studio
```

## 📚 Recursos

- [Ionic Framework](https://ionicframework.com/)
- [Ionic Angular](https://ionicframework.com/docs/angular/overview)
- [Capacitor](https://capacitorjs.com/)
- [JHipster](https://www.jhipster.tech/)

---

**Última actualización:** 8 de marzo de 2026
