package com.easypark.app.reservationhistory.data.repository

import com.easypark.app.reservationhistory.domain.model.ReservationItem
import com.easypark.app.reservationhistory.domain.model.ReservationStatus
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository

class MockReservationHistoryRepository : ReservationHistoryRepository {
    override suspend fun getReservations(): List<ReservationItem> {
        return listOf(
            ReservationItem(
                id = "1",
                clientName = "Alex Johnson",
                spaceLabel = "Espacio A-12",
                startTime = "10:00 AM",
                endTime = "2:00 PM",
                status = ReservationStatus.ACTIVE
            ),
            ReservationItem(
                id = "2",
                clientName = "Alex Johnson",
                spaceLabel = "Espacio A-12",
                startTime = "10:00 AM",
                endTime = "2:00 PM",
                status = ReservationStatus.ACTIVE
            ),
            ReservationItem(
                id = "3",
                clientName = "Alex Johnson",
                spaceLabel = "Espacio A-12",
                startTime = "10:00 AM",
                endTime = "2:00 PM",
                status = ReservationStatus.ACTIVE
            ),
            ReservationItem(
                id = "4",
                clientName = "Marcus Chen",
                spaceLabel = "Espacio C-21",
                startTime = "08:00 AM",
                endTime = "12:45 PM",
                status = ReservationStatus.ENDING_SOON,
                timeLeftText = "15 MINS FALTAN"
            ),
            ReservationItem(
                id = "5",
                clientName = "Sarah Smith",
                spaceLabel = "Espacio B-05",
                startTime = "09:00 AM",
                endTime = "11:00 AM",
                status = ReservationStatus.FINISHED
            ),
            ReservationItem(
                id = "6",
                clientName = "John Doe",
                spaceLabel = "Espacio D-03",
                startTime = "01:00 PM",
                endTime = "03:00 PM",
                status = ReservationStatus.FINISHED
            )
        )
    }
}
