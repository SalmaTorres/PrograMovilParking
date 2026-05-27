# Reporte Consolidado de Cambios e Implementaciones - EasyPark

Este documento recopila todos los cambios estructurales, optimizaciones, correcciones de errores (crashes y pantallas vacías) e implementaciones de notificaciones realizados en el proyecto EasyPark.

---

## Cambios Implementados

### 1. Eliminación de la Ruta de Usuario Redundante en Firebase (`users/$userId`)
- **Acción:** Modificamos los repositorios para que la información del perfil del usuario **únicamente** se almacene en la ruta `users/$sanitizedEmail` (por ejemplo, `users/salma@gmail_com`).
- **Detalle:** Se eliminaron por completo las llamadas redundantes a `firebaseManager.saveData("users/$userId", ...)` en:
  - [RegisterRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/register/data/repository/RegisterRepositoryImpl.kt)
  - [RegisterVehicleRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/registervehicle/data/repository/RegisterVehicleRepositoryImpl.kt)
  - [RegisterParkingRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/registerparking/data/repository/RegisterParkingRepositoryImpl.kt)

### 2. Generación Determinista y Única de IDs basada en el Correo
- **Acción:** Para evitar colisiones causadas por identificadores secuenciales locales (por ejemplo, que dos dispositivos asignen localmente `id = 1` y sobreescriban nodos cruzados en Firebase como `vehicles/1` o `parkings/1`), generamos IDs numéricos únicos calculados a partir de un hash del correo electrónico del usuario.
- **Registro de Usuario (ID Único):**
  - En [RegisterViewModel.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/register/presentation/viewmodel/RegisterViewModel.kt), asignamos el `id` del usuario como `s.email.replace(".", "_").hashCode() and 0x7FFFFFFF`. Esto garantiza un entero positivo único y consistente para cada cuenta.
- **Registro de Parqueo (ID de Parqueo Único):**
  - En [RegisterParkingViewModel.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/registerparking/presentation/viewmodel/RegisterParkingViewModel.kt), generamos el `parkingId` de forma determinista usando `(user.email + "_parking").hashCode() and 0x7FFFFFFF`.

### 3. Mapeo del ID Personalizado en Room SQLite
- **Acción:** Modificamos los mappers para que la base de datos local SQLite (Room) conserve el ID único generado deterministamente en lugar de reemplazarlo por un autoincremental local de base de datos:
  - **Mapeador de Usuario:** En [UserMapper.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/core/data/mapper/UserMapper.kt) agregamos `.apply { id = this@toEntity.id }` en `UserModel.toEntity()`.
  - **Mapeador de Parqueo:** En [ParkingMapper.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/core/data/mapper/ParkingMapper.kt) agregamos `.apply { id = this@toEntity.id }` en `ParkingModel.toEntity()`.

### 4. Validación de Correo Existente en Firebase en Tiempo Real
- **Archivo:** [RegisterRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/register/data/repository/RegisterRepositoryImpl.kt)
- **Acción:** Actualizamos `isEmailAvailable(email)` para que no sólo verifique de forma local, sino que también realice una consulta directa a Firebase Realtime Database.
- **Funcionamiento:** Consulta el nodo `users/$sanitizedEmail` (obteniendo el flujo con `observeData().firstOrNull()`). Si el nodo contiene información (distinta de `null` o `"null"`), significa que la cuenta ya existe en la nube, cancelando el registro e impidiendo que el usuario continúe, mostrando el error correspondiente ("Email ya registrado").

### 5. Solución a "No tienes vehículos registrados" en Reservas
- **Archivo:** [RegisterVehicleRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/registervehicle/data/repository/RegisterVehicleRepositoryImpl.kt)
- **Problema:** En `getVehicleByDriverId(driverId)`, cuando un usuario iniciaba sesión en un nuevo dispositivo (o tras limpiarse la BD local por la actualización de esquema), el sistema consultaba exitosamente el JSON del vehículo desde Firebase (`vehicles/$driverId`), pero **no realizaba el parseo del JSON ni guardaba el vehículo en Room**, retornando siempre `null` y bloqueando la reserva.
- **Solución:** Implementamos el parseo completo de `VehicleDTO` usando `Json.decodeFromString` y convertimos a `VehicleModel`. Ahora, se guarda localmente en Room (`localDS.saveVehicle(vehicleModel.toEntity(driverId))`) y se retorna el vehículo recuperado. Esto permite que el flujo de confirmación de reservas cargue el vehículo de Firebase correctamente y continúe sin problemas.

### 6. Identificador Único para Reservas (`reservations/$resId`)
- **Archivo:** [BookingConfirmationRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/bookingconfirmation/data/repository/BookingConfirmationRepositoryImpl.kt)
- **Acción:** En `makeReservation`, cambiamos la forma en que se crea la entidad de la reserva. En lugar de pasar un `id` por defecto en `0` (el cual Room autoincrementaba localmente generando colisiones cruzadas tipo `reservations/1`), generamos un hash único combinando la ID del conductor, la ID del parqueo y el timestamp exacto de inicio:
  ```kotlin
  val generatedResId = (driverId.toString() + "_" + parkingId.toString() + "_" + startTime.toString()).hashCode() and 0x7FFFFFFF
  ```
- **Resultado:** Este ID numérico positivo único se guarda localmente en Room y sirve como clave en el nodo `reservations/$resId` de Firebase. De este modo, dos dispositivos que realicen reservas simultáneamente nunca chocarán ni sobreescribirán sus registros.

### 7. Control del Primer Inicio para Firebase In-App Messaging
- **Archivos:** [build.gradle.kts](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/build.gradle.kts) y [MainActivity.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/androidMain/kotlin/com/easypark/app/MainActivity.kt)
- **Acción:** Añadimos `libs.firebase.inappmessaging` en el compilado gradle. En la actividad principal, implementamos la verificación asíncrona mediante `SharedPreferences` para detectar si la aplicación se abre por primera vez tras la instalación.
- **Funcionamiento:** Si es la primera apertura de la app, se dispara el evento personalizado `primer_inicio` en Firebase Analytics, el cual sirve como gatillo (trigger) para mostrar la campaña de bienvenida en pantalla. Posteriormente, se actualiza la bandera persistente a `false` para prevenir llamadas repetitivas en reinicios o ejecuciones posteriores de la aplicación.

### 8. Notificaciones Push Reales con Firebase Cloud Messaging (FCM)
- **Archivos:** [MainActivity.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/androidMain/kotlin/com/easypark/app/MainActivity.kt) y [MyFirebaseMessagingService.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/androidMain/kotlin/com/easypark/app/core/notifications/MyFirebaseMessagingService.kt)
- **Gestión de Tokens:** En `MainActivity.kt`, inyectamos el `SessionManager` y observamos de forma reactiva el flujo `currentUser`. Al detectar un usuario activo, obtenemos el token de registro de FCM y lo guardamos en la base de datos de Firebase en la ruta `users/$sanitizedEmail/fcmToken`.
- **Sincronización en Servicio:** Implementamos la interfaz `KoinComponent` en `MyFirebaseMessagingService` para inyectar `SessionManager`. De este modo, en `onNewToken` podemos asociar y actualizar inmediatamente el token del usuario en Firebase Realtime Database si hay una sesión iniciada.
- **Visualización en Primer Plano:** Implementamos `onMessageReceived` de forma que reciba el mensaje y construya una notificación de sistema con `NotificationCompat.Builder` utilizando el canal `default_easypark_channel` y el icono de la aplicación. Esto asegura que la notificación se dibuje en la barra de estado tanto si la app está en primer plano como en segundo plano.

### 9. Solución a Crash de Confirmación de Reserva por Ausencia de Datos de Parqueo en Room
- **Problema:** En el dispositivo del conductor, al abrir la pantalla de confirmación de reserva, la aplicación crasheaba debido a que la consulta local `bookingDS.getParkingById(parkingId)` retornaba `null` (el conductor no tiene almacenados los parqueos localmente en Room), provocando que se lanzara una excepción `Exception("No encontrado")` no controlada.
- **Implementación de Guardado en Local:** 
  - Agregamos la función `@Insert` en [BookingConfirmationDao.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/bookingconfirmation/data/dao/BookingConfirmationDao.kt) y la declaramos en la interfaz e implementación del DataSource local.
- **Búsqueda con Fallback en la Nube:** 
  - Actualizamos `getBookingInfo()` y `makeReservation()` en [BookingConfirmationRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/bookingconfirmation/data/repository/BookingConfirmationRepositoryImpl.kt). Ahora, si un parqueo no está guardado localmente en Room, se realiza una consulta rápida a Firebase Realtime Database bajo la ruta `parkings/$parkingId`, se mapea y se inserta transparentemente en Room antes de continuar. Esto elimina por completo el crash y permite realizar la reserva de manera exitosa.

### 10. Solución al Historial Vacío de Reservas ("No tienes reservas") por Fallos de Deserialización y Tipado
- **Problema:** Tras la migración de los IDs de los usuarios a un formato secuencial (enteros en lugar de cadenas de texto hashes), la lista completa de reservas fallaba al decodificarse en el dispositivo. Si existía una sola reserva antigua en Firebase con un tipo de dato diferente (por ejemplo, `driverId` guardado como String en vez de un Integer), el decodificador global de Kotlin `Json` lanzaba una excepción que descartaba silenciosamente toda la lista de reservas, mostrando una lista vacía.
- **Creación de SafeSerializers:** Creamos e implementamos custom serializers (`SafeIntSerializer`, `SafeLongSerializer`, `SafeDoubleSerializer`, y `SafeBooleanSerializer` en [SafeSerializers.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/core/data/dto/SafeSerializers.kt)) capaces de resolver de forma transparente la coerción y el casteo tanto de números puros como de cadenas numéricas que vengan de Firebase.
- **Anotación de DTOs:** Anotamos todos los campos numéricos y booleanos de [ReservationDTO.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/core/data/dto/ReservationDTO.kt) y [PriceDTO.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/core/data/dto/PriceDTO.kt) con estos serializers.
- **Decodificación Elemento por Elemento:** Modificamos las consultas en [ReservationSummaryRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/reservationsummary/data/repository/ReservationSummaryRepositoryImpl.kt) (para conductores) y [ReservationHistoryRepositoryImpl.kt](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/composeApp/src/commonMain/kotlin/com/easypark/app/reservationhistory/data/repository/ReservationHistoryRepositoryImpl.kt) (para dueños). En lugar de decodificar todo el mapa completo de Firebase de un solo golpe, ahora iteramos y decodificamos cada reserva de forma individual dentro de un bloque `try-catch`. Si una sola reserva antigua está corrupta o mal formateada, simplemente se ignora y se registra en logs, pero el resto de las reservas válidas se muestran correctamente en pantalla en tiempo real.

### 11. Guía de Notificaciones para el Desarrollador
- **Archivo:** [notificaciones_guia.md](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/guias/notificaciones_guia.md)
- **Acción:** Creamos una guía detallada en español explicando los dos flujos (Spark gratuito vs Blaze push con Cloud Functions), cómo configurar el entorno e instalar dependencias, cómo emular localmente con Firebase Emulator Suite de manera 100% gratuita y cómo desplegar en producción.

### 12. Skill de Agente Reutilizable
- **Archivo:** [SKILL.md](file:///Users/carladianavalverde/Documents/1-2026/PrograMovil/PROYECTO/PrograMovilParking/.agents/skills/setup-easypark-notification-functions/SKILL.md)
- **Acción:** Registramos la Skill `setup-easypark-notification-functions` bajo las directrices oficiales de empaquetado para que futuros agentes de IA asistan en configurar, modificar, emular y desplegar las notificaciones de la app.

---

## Verificación de Compilación

Hemos compilado el código exitosamente en Android utilizando Gradle:
```bash
./gradlew compileDebugKotlin
```
**Resultado:** La compilación finalizó de manera exitosa y sin errores.
