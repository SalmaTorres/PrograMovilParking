import com.easypark.app.core.domain.model.PriceModel

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
    val status: String = "ACTIVE"
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

    val durationText: String get() {
        val diff = endTime - startTime
        val hours = diff / 3600000
        return if (hours <= 1) "1 hora" else "$hours horas"
    }
}