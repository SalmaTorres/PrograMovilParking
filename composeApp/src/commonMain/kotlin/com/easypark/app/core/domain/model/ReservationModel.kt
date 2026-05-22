import com.easypark.app.core.domain.model.PriceModel
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime

data class ReservationModel(
    val id: Int = 0,
    val parkingName: String,
    val address: String,
    val spaceId: Int = 0,
    val spaceNumber: Int,
    val startTime: Long,
    val endTime: Long,
    val totalPrice: PriceModel,
    val paymentMethod: String = "CASH",
    val status: String = "ACTIVE",
    val vehiclePlate: String = "",
    val vehicleType: String = "",
    val arrivalTime: Long = 0L,
    val parkingId: Int = 0
) {
    private fun formatMillisToTime(millis: Long): String {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        // Esto convierte los milisegundos a la hora local de tu teléfono
        val localDateTime = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())

        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')

        return "$hour:$minute"
    }

    val startTimeStr: String get() = formatMillisToTime(startTime)
    val endTimeStr: String get() = formatMillisToTime(endTime)
    val arrivalTimeStr: String get() = if (arrivalTime > 0L) formatMillisToTime(arrivalTime) else "--:--"

    val durationText: String get() {
        val diff = endTime - startTime
        val minutes = diff / 60000
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return when {
            hours > 0 -> "${hours}h ${remainingMinutes}min"
            else -> "${minutes} min"
        }
    }

    fun getRemainingGracePeriodMillis(): Long {
        val tenMinutesInMillis = 10 * 60 * 1000L
        val expirationTime = startTime + tenMinutesInMillis
        // Cambia la línea roja por esta:
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val remaining = expirationTime - now
        return if (remaining >= 0) remaining else 0L
    }

    fun formatRemainingTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}