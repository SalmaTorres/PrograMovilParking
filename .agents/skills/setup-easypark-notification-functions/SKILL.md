---
name: setup-easypark-notification-functions
description: >-
  Instrucciones paso a paso para que el agente asista en la configuración, emulación local y despliegue de las Cloud Functions y triggers de base de datos para notificaciones en tiempo real en EasyPark.
---

# Setup EasyPark Notification Functions

## Overview
Esta skill proporciona instrucciones estructuradas para inicializar, configurar, probar de manera local (emulación) y desplegar Cloud Functions para enviar notificaciones push en tiempo real a los usuarios de la aplicación EasyPark cuando se crea o modifica una reserva en la base de datos Firebase Realtime Database.

## Dependencies
- Firebase CLI (`firebase-tools`) instalado en el entorno.
- Node.js y npm configurados.
- Java JRE instalado para ejecutar el Local Emulator Suite.

## Quick Start

Para iniciar rápidamente la emulación local de las Cloud Functions y probar las notificaciones:
```bash
# 1. Navegar al directorio de funciones e instalar dependencias
cd functions
npm install

# 2. Iniciar emuladores locales desde la raíz del proyecto
cd ..
firebase emulators:start
```

---

## Workflow

### 1. Inicialización del Entorno de Firebase Functions
Si el proyecto no cuenta con el directorio `functions` inicializado, el agente debe:
- Proponer el comando `firebase init functions` en la raíz del proyecto.
- Seleccionar el lenguaje **JavaScript** o **TypeScript** (por defecto en este proyecto se usa JavaScript/Node.js).
- Habilitar ESLint si el usuario lo desea para consistencia de código.

### 2. Codificación del Trigger de Notificaciones
El agente debe asegurarse de que el archivo `functions/index.js` (o `index.ts`) contenga el trigger que escucha la ruta de las reservas `/reservations/{reservationId}`:
- Debe utilizar la API V2 de Cloud Functions (`firebase-functions/v2/database`).
- Debe importar `firebase-admin` para interactuar de forma segura con la Realtime Database y el servicio de FCM (`admin.messaging()`).
- Debe recuperar el ID del conductor (`driverId`), buscar su correo sanitizado o su registro de usuario correspondiente en `/users` para obtener su `fcmToken`.
- Debe construir el mensaje y enviarlo usando `admin.messaging().send(message)`.

### 3. Emulación y Pruebas Gratuitas
Para probar el flujo localmente sin incurrir en costos ni requerir el plan de pago Blaze de Firebase:
- Ejecutar `firebase emulators:start` en la terminal.
- Indicar al usuario que ingrese a la consola local en `http://localhost:4000`.
- Insertar un objeto en el Database Emulator bajo la ruta `/reservations/{reservationId}` para comprobar que la función se ejecuta de forma correcta observando la terminal de logs del emulador.

### 4. Despliegue en Producción (Plan Blaze)
Cuando el usuario confirme que está en el plan Blaze de Firebase y desee subir el trigger a producción:
- Verificar que el proyecto correcto esté seleccionado con `firebase use easypark-20100`.
- Ejecutar el comando de despliegue para las funciones:
  ```bash
  firebase deploy --only functions
  ```

---

## Rate Limiting
El envío de notificaciones a través de Firebase Cloud Messaging está sujeto a los límites de cuota de la API de FCM de Firebase.
- **Límite por dispositivo:** Evitar enviar múltiples notificaciones repetitivas en ráfaga a un mismo token en menos de 1 segundo.
- **Reintentos:** En caso de fallas temporales (código de error de red o servidor 5xx), implementar reintentos exponenciales con un límite de hasta 3 intentos antes de abortar.

---

## Common Mistakes
1. **Ignorar el Plan de Firebase:** Intentar desplegar las funciones reales con `firebase deploy` estando en el plan gratuito Spark. Esto fallará indicando la necesidad de actualizar al plan Blaze. El agente debe proponer el uso del emulador local en su lugar.
2. **Búsqueda Incorrecta de Token:** Filtrar al usuario en Firebase utilizando un tipo de ID incompatible (como un String hash antiguo versus los nuevos IDs secuenciales de tipo Integer). Siempre verificar el tipo de dato del campo de filtro en la consulta `orderByChild("id").equalTo(driverId)`.
3. **No Sanitizar Claves:** Al registrar tokens o notificaciones en nodos asociados al correo del usuario, olvidar cambiar los caracteres especiales (ej: reemplazar puntos `.` por guiones bajos `_`).
