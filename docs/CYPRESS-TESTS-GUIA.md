# Tests de Cypress - Login y Register

## Resumen

Se han mejorado y ampliado los tests de Cypress para las funcionalidades de **Login (Sign In)** y **Register (Sign Up)** del sistema de biblioteca.

## Tests Implementados

### 🔐 Login Tests (login-page.cy.ts)

**Total: 18 tests organizados en 5 categorías**

#### 1. Page Display (6 tests)
- ✅ Verificación del título de la página
- ✅ Acceso directo a /login
- ✅ Focus automático en campo de username
- ✅ Visualización de todos los elementos del formulario
- ✅ Verificación del enlace "Forgot Password"
- ✅ Verificación del enlace "Register"

#### 2. Form Validation (5 tests)
- ✅ Campo username requerido
- ✅ Campo password requerido
- ✅ No permite envío con formulario vacío
- ✅ Mensaje de error con contraseña incorrecta
- ✅ Mensaje de error con usuario inexistente

#### 3. Successful Login (3 tests)
- ✅ Login exitoso con credenciales válidas
- ✅ Login con "Remember Me" activado
- ✅ Redirección a home después del login exitoso

#### 4. Navigation (2 tests)
- ✅ Navegación a página de recuperación de contraseña
- ✅ Navegación a página de registro

#### 5. Security (2 tests)
- ✅ Password no se muestra en texto plano
- ✅ Mensaje de error persiste hasta nuevo intento

### 📝 Register Tests (register-page.cy.ts)

**Total: 38 tests organizados en 8 categorías**

#### 1. Page Display (6 tests)
- ✅ Acceso desde el menú
- ✅ Carga correcta de la página
- ✅ Visualización del título
- ✅ Visualización de todos los campos del formulario
- ✅ Focus automático en campo de username
- ✅ Enlace a página de login

#### 2. Username Validation (5 tests)
- ✅ Campo username requerido
- ✅ Validación de longitud mínima
- ✅ Validación de longitud máxima (50 caracteres)
- ✅ Aceptación de username válido
- ✅ Aceptación de caracteres especiales permitidos

#### 3. Email Validation (7 tests)
- ✅ Campo email requerido
- ✅ Rechazo de formato inválido
- ✅ Rechazo de email sin dominio
- ✅ Rechazo de email sin @
- ✅ Aceptación de formato correcto
- ✅ Aceptación de email con subdominios
- ✅ Validación de longitud mínima

#### 4. Password Validation (5 tests)
- ✅ Campo password requerido
- ✅ Validación de longitud mínima (4 caracteres)
- ✅ Aceptación de password válido
- ✅ Password no se muestra en texto plano
- ✅ Validación de longitud máxima (50 caracteres)

#### 5. Password Confirmation (4 tests)
- ✅ Passwords deben coincidir
- ✅ Botón deshabilitado si passwords no coinciden
- ✅ Botón habilitado con todos los campos válidos
- ✅ Error mostrado cuando passwords no coinciden

#### 6. Successful Registration (3 tests)
- ✅ Registro de usuario válido
- ✅ Mensaje de éxito después del registro
- ✅ Ocultamiento del formulario después del registro exitoso

#### 7. Error Handling (3 tests)
- ✅ Error cuando username ya existe
- ✅ Error cuando email ya existe
- ✅ Error genérico en fallo del servidor

#### 8. Form Interaction (2 tests)
- ✅ Limpieza de errores al corregir input
- ✅ Navegación con tabulador entre campos

## 📊 Cobertura Total

- **Login**: 18 tests
- **Register**: 38 tests
- **Total**: 56 tests de Cypress para autenticación

## 🚀 Cómo Ejecutar los Tests

### Prerrequisitos

1. Asegúrate de que la aplicación backend esté ejecutándose:
```powershell
cd backend
./mvnw
```

2. En otra terminal, la aplicación debe estar disponible en `http://localhost:8080/`

### Ejecutar Todos los Tests de Cypress

#### Modo Headless (sin interfaz gráfica)
```powershell
cd backend
npm run e2e
```

#### Modo Interactivo (con Cypress Test Runner)
```powershell
cd backend
npm run e2e:open
```

### Ejecutar Solo Tests de Login

```powershell
npm run cypress run --spec "src/test/javascript/cypress/e2e/account/login-page.cy.ts"
```

### Ejecutar Solo Tests de Register

```powershell
npm run cypress run --spec "src/test/javascript/cypress/e2e/account/register-page.cy.ts"
```

### Ejecutar Todos los Tests de Account

```powershell
npm run cypress run --spec "src/test/javascript/cypress/e2e/account/*.cy.ts"
```

## 🔧 Configuración

Los tests utilizan las siguientes configuraciones de Cypress (definidas en `cypress.config.ts`):

- **Base URL**: `http://localhost:8080/`
- **Viewport**: 1200x720
- **Retries**: 2 intentos por test
- **Video**: Deshabilitado
- **Screenshots**: Guardados en `target/cypress/screenshots`

## 📝 Variables de Entorno

Los tests de login utilizan estas variables de entorno:

```javascript
E2E_USERNAME = 'user' (por defecto)
E2E_PASSWORD = 'user' (por defecto)
```

Para cambiarlas, edita el archivo `cypress.config.ts` o crea un archivo `cypress.env.json`:

```json
{
  "E2E_USERNAME": "tu_usuario",
  "E2E_PASSWORD": "tu_password"
}
```

## 🎯 Casos de Uso Cubiertos

### Login
- ✅ Login exitoso con credenciales válidas
- ✅ Login fallido con credenciales inválidas
- ✅ Validación de campos requeridos
- ✅ Funcionalidad "Remember Me"
- ✅ Navegación a recuperación de contraseña
- ✅ Navegación a página de registro
- ✅ Seguridad (password oculto)

### Register
- ✅ Registro exitoso de nuevo usuario
- ✅ Validación completa de todos los campos
- ✅ Manejo de errores (usuario/email duplicados)
- ✅ Validación de formato de email
- ✅ Validación de longitudes min/max
- ✅ Confirmación de password
- ✅ Mensajes de éxito y error
- ✅ Navegación de formulario

## 📋 Selectores de Cypress Utilizados

Los tests utilizan selectores `data-cy` para mayor estabilidad:

```typescript
// Login
[data-cy="loginTitle"]
[data-cy="username"]
[data-cy="password"]
[data-cy="submit"]
[data-cy="loginError"]
[data-cy="forgetYourPasswordSelector"]

// Register
[data-cy="registerTitle"]
[data-cy="username"]
[data-cy="email"]
[data-cy="firstPassword"]
[data-cy="secondPassword"]
[data-cy="submit"]
```

## 🐛 Debugging

Para debuggear tests individuales:

1. Abre Cypress en modo interactivo: `npm run e2e:open`
2. Selecciona el navegador preferido
3. Click en el archivo de test que deseas ejecutar
4. Usa el selector de tiempo para ver cada paso
5. Inspecciona elementos con las herramientas de desarrollador

## 📈 Próximos Pasos

Posibles mejoras futuras:
- [ ] Tests de sesión persistente
- [ ] Tests de timeout de sesión
- [ ] Tests de múltiples intentos de login fallidos
- [ ] Tests de activación de cuenta por email
- [ ] Tests de integración con redes sociales (si aplica)
- [ ] Tests de accesibilidad (a11y)

## ✅ Tests Existentes Adicionales

El proyecto también incluye otros tests de Cypress:

- `logout.cy.ts` - Tests de cierre de sesión
- `password-page.cy.ts` - Tests de cambio de contraseña
- `reset-password-page.cy.ts` - Tests de recuperación de contraseña
- `settings-page.cy.ts` - Tests de configuración de usuario

Y tests de entidades:
- `author.cy.ts`, `book.cy.ts`, `client.cy.ts`, `publisher.cy.ts`, `borrowed-book.cy.ts`

---

**Última actualización**: Marzo 7, 2026
