const { onValueCreated } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");
const logger = require("firebase-functions/logger");

// Inicializar el SDK de Firebase Admin
admin.initializeApp();

/**
 * Cloud Function que se dispara automáticamente al crearse una nueva reserva.
 * Escucha la ruta: /reservations/{reservationId}
 */
exports.sendReservationNotification = onValueCreated(
  "/reservations/{reservationId}",
  async (event) => {
    const reservation = event.data.val();
    
    // Validar que la reserva tenga los datos necesarios
    if (!reservation) {
      logger.warn("La reserva creada no contiene datos.");
      return;
    }

    const driverId = reservation.driverId;
    const parkingName = reservation.parkingName || "Parqueo";

    if (!driverId) {
      logger.warn("La reserva no contiene un 'driverId'.");
      return;
    }

    try {
      logger.log(`Nueva reserva detectada para el conductor ID: ${driverId}. Buscando token...`);

      // 1. Buscar al usuario en el nodo 'users' mediante su ID numérico
      const userSnapshot = await admin
        .database()
        .ref("users")
        .orderByChild("id")
        .equalTo(driverId)
        .once("value");

      if (!userSnapshot.exists()) {
        logger.warn(`No se encontró ningún usuario con el ID: ${driverId}`);
        return;
      }

      // 2. Extraer el fcmToken del usuario encontrado
      let fcmToken = null;
      userSnapshot.forEach((childSnapshot) => {
        const userData = childSnapshot.val();
        if (userData && userData.fcmToken) {
          fcmToken = userData.fcmToken;
        }
      });

      if (!fcmToken) {
        logger.warn(`El usuario con ID ${driverId} no tiene un 'fcmToken' registrado.`);
        return;
      }

      logger.log(`Token FCM encontrado. Enviando notificación push...`);

      // 3. Construir el payload de la notificación push real
      const message = {
        token: fcmToken,
        notification: {
          title: "¡Reserva Confirmada! 🚗",
          body: `Tu espacio está reservado en ${parkingName}.`,
        },
        android: {
          notification: {
            sound: "default",
          },
        },
      };

      // 4. Enviar el mensaje a través de Firebase Cloud Messaging (FCM)
      const response = await admin.messaging().send(message);
      logger.log("Notificación push enviada exitosamente:", response);

    } catch (error) {
      logger.error("Error al procesar el envío de la notificación push:", error);
    }
  }
);
