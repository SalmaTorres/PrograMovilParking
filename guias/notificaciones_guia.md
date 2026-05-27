# Guía de Notificaciones en Tiempo Real (EasyPark)

Esta guía explica los dos mecanismos implementados para manejar notificaciones en tiempo real en EasyPark, cómo configurarlos, emularlos localmente de manera gratuita y cómo desplegarlos en producción.

---

## Mecanismo 1: Notificaciones Directas (Gratuito - Plan Spark)

### Cómo funciona:
Dado que el plan Spark de Firebase es totalmente gratuito, no permite ejecutar código en el lado del servidor (Cloud Functions) en producción. Para resolver esto de manera gratuita, la aplicación escribe y escucha directamente en la base de datos de Firebase Realtime Database:

1. **Escritura Directa:** Al realizar una acción (confirmar reserva, marcar llegada, cancelar reserva), el repositorio Kotlin correspondiente guarda un nodo de notificación en la ruta `notifications/$userId/res_$timestamp` en Firebase Realtime Database.
2. **Escucha Reactiva (In-App):** La pantalla de notificaciones de la aplicación (`NotificationsScreen.kt`) observa en tiempo real la ruta `notifications/$userId` mediante un Flow reactivo. Al haber cualquier cambio, la lista de notificaciones se actualiza instantáneamente en pantalla.

### Código de ejemplo en Kotlin (Escritura):
```kotlin
val notification = """
{
    "id": $reservationId,
    "title": "¡Reserva Realizada!",
    "message": "Tu reserva en ${parking.name} ha sido registrada con éxito.",
    "time": "Ahora",
    "isUnread": true
}
""".trimIndent()

firebaseManager.saveData("notifications/$driverId/res_$startTime", notification)
```

---

## Mecanismo 2: Notificaciones Push con Cloud Functions (Pago - Plan Blaze)

### Cómo funciona:
Este método automatiza el envío de notificaciones push en segundo plano directamente al dispositivo del usuario utilizando **Firebase Cloud Messaging (FCM)**. Funciona mediante un trigger en el servidor:

1. **Trigger de Base de Datos:** Una Firebase Cloud Function escucha la ruta `/reservations/{reservationId}` en Firebase Realtime Database.
2. **Detección y Token:** Cuando se crea una nueva reserva, la función detecta el `driverId`, consulta la base de datos para buscar el token de dispositivo del conductor (`users/$email_sanitizado/fcmToken`) y obtiene el token.
3. **Envío con FCM:** La función invoca a `admin.messaging().send()` enviando una notificación push real que el dispositivo de Android intercepta y muestra en la barra de estado del sistema (gracias a `MyFirebaseMessagingService.kt`).

### Código del Trigger (`functions/index.js`):
El archivo de Cloud Functions actual está en JavaScript (V2 de Firebase Functions):
```javascript
const { onValueCreated } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");
const logger = require("firebase-functions/logger");

admin.initializeApp();

exports.sendReservationNotification = onValueCreated(
  "/reservations/{reservationId}",
  async (event) => {
    const reservation = event.data.val();
    if (!reservation) return;

    const driverId = reservation.driverId;
    const parkingName = reservation.parkingName || "Parqueo";
    if (!driverId) return;

    try {
      logger.log(`Nueva reserva detectada para el conductor ID: ${driverId}. Buscando token...`);

      // 1. Buscar el usuario mediante su ID secuencial
      const userSnapshot = await admin.database().ref("users").orderByChild("id").equalTo(driverId).once("value");
      if (!userSnapshot.exists()) return;

      // 2. Extraer fcmToken
      let fcmToken = null;
      userSnapshot.forEach((childSnapshot) => {
        const userData = childSnapshot.val();
        if (userData && userData.fcmToken) {
          fcmToken = userData.fcmToken;
        }
      });

      if (!fcmToken) return;

      // 3. Enviar mensaje FCM
      const message = {
        token: fcmToken,
        notification: {
          title: "¡Reserva Confirmada! 🚗",
          body: `Tu espacio está reservado en ${parkingName}.`,
        },
        android: {
          notification: { sound: "default" },
        },
      };

      const response = await admin.messaging().send(message);
      logger.log("Notificación push enviada exitosamente:", response);
    } catch (error) {
      logger.error("Error al enviar notificación push:", error);
    }
  }
);
```

---

## Guía de Ejecución y Pruebas Locales (Gratuito)

Puedes probar y depurar las Cloud Functions en tu máquina local sin necesidad de actualizar a la versión Blaze ni ingresar una tarjeta de crédito.

### Requisitos Previos:
- Tener instalado **Node.js** y **npm** en tu computadora.
- Tener instalado **Java Runtime Environment (JRE)** (es necesario para ejecutar los emuladores de Firebase).
- Firebase CLI instalado globalmente (`npm install -g firebase-tools`).

### Paso a Paso para Emular Localmente:

1. **Instalar Dependencias:**
   Entra a la carpeta de funciones e instala las dependencias de Node:
   ```bash
   cd functions
   npm install
   ```

2. **Iniciar Emuladores de Firebase:**
   Desde la raíz del proyecto, ejecuta el comando para arrancar la suite de emulación local:
   ```bash
   firebase emulators:start
   ```
   *Nota: Esto iniciará el emulador de Realtime Database y el emulador de Cloud Functions locales.*

3. **Acceder a la Consola Local:**
   Abre en tu navegador `http://localhost:4000` para entrar al panel de administración del emulador.

4. **Probar el Trigger:**
   - Ve a la pestaña **Database Emulator**.
   - Añade un registro de prueba bajo la ruta `/reservations/prueba_1` con campos como `"driverId": 1` y `"parkingName": "Mi Parqueo Central"`.
   - Revisa la pestaña de **Logs Emulator** o la consola. Verás cómo se dispara la función `sendReservationNotification` automáticamente al detectar la inserción.

---

## Guía de Despliegue en Producción (Plan Blaze)

Si decides habilitar el plan Blaze (pago por uso) de Firebase, sigue estos pasos para desplegar las Cloud Functions en la nube real de Firebase:

1. **Actualizar el Plan:**
   Ve a la consola web de Firebase, ingresa a la configuración de tu proyecto `easypark-20100` y actualízalo al plan Blaze (cuenta con una cuota gratuita mensual amplia para proyectos pequeños).

2. **Habilitar APIs de Google Cloud:**
   El comando de despliegue habilitará automáticamente, o te solicitará activar, las siguientes APIs de Google Cloud Console:
   - `cloudbuild.googleapis.com` (Cloud Build API)
   - `cloudfunctions.googleapis.com` (Cloud Functions API)
   - `artifactregistry.googleapis.com` (Artifact Registry API)

3. **Iniciar Sesión en Firebase CLI:**
   Asegúrate de estar autenticado en la terminal con la cuenta propietaria del proyecto Firebase:
   ```bash
   firebase login
   ```

4. **Seleccionar tu Proyecto Activo:**
   ```bash
   firebase use easypark-20100
   ```

5. **Desplegar las Funciones:**
   Ejecuta el siguiente comando para subir únicamente el código de las Cloud Functions a producción:
   ```bash
   firebase deploy --only functions
   ```
   *El proceso tarda unos 2 a 5 minutos. Al finalizar, la terminal te entregará una URL de éxito y las notificaciones en tiempo real estarán totalmente automatizadas y en producción.*
