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

### 3. Instala las dependencias
Abre el proyecto en Android Studio Flamingo (2022.2.1) o superior.

Sincroniza Gradle y espera a que se instalen los paquetes.

### 4. Corre el backend (opcional)
Puedes usar la API en Railway (default) o correr el backend incluido localmente.

El backend es Node.js/Express + Firebase (consulta la carpeta /backend para detalles y variables de entorno).

### 5. Corre la app en tu dispositivo físico (recomendado)
Prueba en un Android real para evitar problemas de permisos/red/cámara.

No está recomendado en emulador.

