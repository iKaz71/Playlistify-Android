# 🎧 Playlistify Android ![32](https://github.com/user-attachments/assets/d00a8963-3a2c-41f0-af76-f1b08d9399db)

<div align="center">
  <img src="https://github.com/user-attachments/assets/245eef83-2343-411a-8531-7a6899b4949c" alt="playlistify_logo_transparente" width="350"/>
</div>

**Playlistify** es una app para crear y gestionar salas colaborativas de música. Permite que varias personas agreguen, eliminen y controlen canciones en tiempo real desde sus dispositivos, haciendo la música grupal algo fácil y divertido, ideal para reuniones y fiestas.

---

## 📝 Memoria Técnica

### Objetivo del App

Playlistify busca solucionar la gestión musical en bares y reuniones, permitiendo que cualquiera aporte a la playlist sin que el anfitrión tenga que estar presente todo el tiempo. Nace a partir de la necesidad en un negocio donde la música depende de YouTube Premium, y las playlists de la plataforma suelen tener bugs o requieren supervisión constante.

### Descripción del logo

El logo es un fantasma con una tornamesa o disco para mezclar, usando los colores predominantes del bar donde nació el proyecto.  
**Significado:** El fantasma representa que el DJ o dueño puede estar "ausente", pero la música sigue fluyendo y organizada gracias a Playlistify: el DJ fantasma sigue poniendo ambiente "desde el código".

### Justificación técnica

- **Android soportado:** Solo teléfonos, desde Android 8.0 (Oreo, API 26) en adelante.  
- **Dispositivos probados:** Samsung Galaxy A34 5G y vivo Y15s (además de equipos actuales como Pixel/Samsung/Xiaomi).
- **Orientación soportada:** Solo vertical (portrait). Rotación desactivada a nivel app.
- **Simulador:** No recomendado (el emulador no se conecta con el backend, probar en dispositivo físico).
- **Dependencia:** Requiere una TV o pantalla con la app PlaylistifyTV (no incluida) y acceso a YouTube.

### Acceso y credenciales

- **No requiere Google Sign-In obligatorio** (es opcional, en el futuro permitirá más features).
- **No requiere usuario ni contraseña para probar lo esencial.**

---

## ⚙️ Instalación y Configuración

### 1. Clona el repositorio

bash
- git clone https://github.com/iKaz71/Playlistify-Android.git
- cd Playlistify-Android

### 2. Configura tus claves de API
Crea un archivo local.properties en la raíz del proyecto con las siguientes variables:

- sdk.dir=RUTA_A_TU_SDK_ANDROID
- YOUTUBE_API_KEY=TU_API_KEY_DE_YOUTUBE
- GOOGLE_CLIENT_ID=TU_CLIENT_ID_DE_GOOGLE
- Además, debes agregar tu archivo google-services.json (descargado desde Firebase Console) dentro de la carpeta app/.

⚠️ No compartas ni subas tus claves reales o archivos sensibles al repositorio.
⚠️ No olvides colocar tambien tu archivo de Google-Service.json en la raiz

### 3. Instala las dependencias
Abre el proyecto en Android Studio Flamingo (2022.2.1) o superior.

Sincroniza Gradle y espera a que se instalen los paquetes.

### 4. Corre el backend (opcional)
Puedes usar la API en Railway (default) o correr el backend incluido localmente.

El backend es Node.js/Express + Firebase (consulta la carpeta /backend para detalles y variables de entorno).

### 5. Corre la app en tu dispositivo físico (recomendado)
Prueba en un Android real para evitar problemas de permisos/red/cámara.

No está recomendado en emulador.


## 📸 Capturas de Pantalla

### Bienvenida

<div align="center">
  <img src="https://github.com/user-attachments/assets/b2def7a2-8c35-4708-ae43-62d8d0918098" alt="Bienvenida 1" width="220"/>
  <img src="https://github.com/user-attachments/assets/59cfa098-e8bd-48a2-9d0e-347c610cf243" alt="Bienvenida 2" width="220"/>
</div>


---

### Sala principal & Buscador de canciones

<div align="center">
  <img src="https://github.com/user-attachments/assets/317de155-436f-47d1-aaaa-8327904cc9da" alt="Sala principal 1" width="180"/>
  <img src="https://github.com/user-attachments/assets/f22d37c8-0a08-464d-a1e2-1c3124bde44c" alt="Sala principal 3" width="180"/>
  <img src="https://github.com/user-attachments/assets/0d76dacf-9382-42fb-a756-b9cee2abf597" alt="Sala principal 2" width="180"/>
  
  <img src="https://github.com/user-attachments/assets/c2584fa1-831b-42ab-bd1d-df9d365c3914" alt="Buscador 1" width="180"/>
  <img src="https://github.com/user-attachments/assets/f7e3b14f-76ab-4dd6-a478-02d41088d248" alt="Buscador 2" width="180"/>
  <img src="https://github.com/user-attachments/assets/6f3495e8-06c4-405c-88f6-66e7798c3d09" alt="Buscador 3" width="180"/>
  <img src="https://github.com/user-attachments/assets/8c718ef2-b8ee-4a45-8c83-33b802d0d996" alt="Buscador 4" width="180"/>
  <img src="https://github.com/user-attachments/assets/a2b92823-6e92-41ca-a468-10e80214ec10" alt="Buscador 5" width="180"/>

</div>



---

## 🛠️ Tecnologías y librerías utilizadas

Playlistify Android aprovecha lo mejor del ecosistema Android moderno para ofrecer una experiencia fluida, rápida y segura:

- **Lenguaje principal:** Kotlin
- **SDK:**  
  - *compileSdk:* 35  
  - *minSdk:* 24 (Android 7.0)  
  - *targetSdk:* 35  
- **Interfaz de usuario:** Jetpack Compose (Material 2 y Material 3)
- **Navegación:** Navigation Compose
- **Carga de imágenes:** Coil
- **Escaneo de códigos QR y cámara:** CameraX + MLKit
- **Persistencia y datos en tiempo real:**  
  - Firebase Auth  
  - Firebase Database  
  - Firebase Firestore  
  - DataStore Preferences
- **Google Sign-In:** Integración para login opcional
- **Red/API:** Retrofit, OkHttp, Gson
- **Material Design:** Material clásico y Material 3
- **Coroutines:** Para programación asíncrona moderna
- **Testing:** JUnit, Espresso, Compose UI Testing
- **SplashScreen:** Core Splashscreen para animación de inicio

> **Plugins principales:**  
> - `com.android.application`  
> - `org.jetbrains.kotlin.android`  
> - `org.jetbrains.kotlin.plugin.compose`  
> - `com.google.gms.google-services`

**Notas técnicas:**
- Utiliza Java 11 para máxima compatibilidad con librerías modernas.
- Las claves de API necesarias (`YOUTUBE_API_KEY`, `GOOGLE_CLIENT_ID`) se configuran de forma segura en el archivo `local.properties` y se inyectan vía Gradle, evitando exponerlas en el código fuente.
- Es necesario agregar el archivo `google-services.json` (obtenido desde Firebase Console) en la carpeta `/app` para la integración de Firebase.

---



## 📒 Notas Importantes

- **No compartas ni subas tus claves API.**

- La app está en pruebas continuas y se espera mejorar para futuras versiones (soporte Android, features, etc.).

---

## 📝 Licencia

Playlistify se publica bajo la **licencia MIT**.  
Puedes modificar y usar el código, pero no se ofrece garantía ni soporte oficial.

---

## 🔧 Backend/API

El backend es un servidor **Node.js/Express + Firebase**.  
La app TV conecta automáticamente con el endpoint de producción por default.

- [Repositorio backend](https://github.com/iKaz71/playlistify-api)

---

## 🚀 Ecosistema Playlistify

- [Playlistify Android](https://github.com/iKaz71/Playlistify-Android)
- [Playlistify iOS](https://github.com/iKaz71/Playlistify-iOS)
- [TV Playlistify](https://github.com/iKaz71/TvPlaylistify)

---

