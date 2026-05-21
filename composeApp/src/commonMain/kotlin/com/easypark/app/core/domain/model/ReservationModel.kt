import com.easypark.app.core.domain.model.PriceModel
import kotlinx.datetime.Clock

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
        val totalSeconds = millis / 1000
        val totalMinutes = totalSeconds / 60
        val hours = (totalMinutes / 60) % 24
        val minutes = totalMinutes % 60
        return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
    }

    val startTimeStr: String get() = formatMillisToTime(startTime)
    val endTimeStr: String get() = formatMillisToTime(endTime)
    val arrivalTimeStr: String get() = if (arrivalTime > 0L) formatMillisToTime(arrivalTime) else "--:--"

    val durationText: String get() {
        val diff = endTime - startTime
        val hours = diff / 3600000
        return if (hours <= 1) "1 hora" else "$hours horas"
    }

    fun getRemainingGracePeriodMillis(): Long {
        val tenMinutesInMillis = 10 * 60 * 1000L
        val expirationTime = startTime + tenMinutesInMillis
        // Cambia la línea roja por esta:
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val remaining = expirationTime - now
        return if (remaining >= 0) remaining else 0L
    }

    fun isGracePeriodExpired(): Boolean = getRemainingGracePeriodMillis() <= 0L

    fun formatRemainingTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}