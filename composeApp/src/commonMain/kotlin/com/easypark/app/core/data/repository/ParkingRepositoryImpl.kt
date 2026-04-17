package com.easypark.app.core.data.repository

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation
import com.easypark.app.parkingdetails.domain.model.ParkingDetail
import com.easypark.app.core.domain.model.Currency
import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.Price
import com.easypark.app.core.domain.repository.ParkingRepository
import com.easypark.app.reservationsummary.domain.model.ReservationSummaryModel

class ParkingRepositoryImpl : ParkingRepository {

    // Datos compartidos para que coincidan
    private val mockList = listOf(
        ParkingModel(
            "1",
            "Estacionamiento Central Oquendo",
            "Calle Oquendo 123",
            Price(3.50, Currency.BOB),
            true,
            -17.3935,
            -66.1570
        ),
        ParkingModel("2", "Parqueo Viedma", "Av. Aniceto Arce", Price(5.0, Currency.BOB), true, -17.3895, -66.1490),
        ParkingModel("3", "Garage Norte", "Av. America", Price(4.0, Currency.BOB), false, -17.3750, -66.1550)
    )

    override suspend fun getAvailableParkings(): List<ParkingModel> = mockList

    override suspend fun getParkingDetail(id: String): ParkingDetail {
        val basicInfo = mockList.find { it.id == id }
        return ParkingDetail(
            id = id,
            name = basicInfo?.name ?: "Parqueo $id",
            address = basicInfo?.address ?: "Dirección desconocida",
            pricePerHour = basicInfo?.pricePerHour ?: Price(0.0),
            rating = 4.5,
            reviewCount = 120,
            schedule = "08:00 - 22:00",
            isAvailable = basicInfo?.isAvailable ?: true
        )
    }
    override suspend fun registerParking(parking: ParkingModel): Boolean = true

    override suspend fun getBookingConfirmation(id: String): BookingConfirmation {
        val parking = mockList.find { it.id == id }
        return BookingConfirmation(
            locationName = parking?.name ?: "Estacionamiento",
            address = parking?.address ?: "Dirección",
            spaceIdentifier = "A - ${id}12", // Simulamos un espacio dinámico
            durationHours = 2,
            pricePerHour = parking?.pricePerHour ?: Price(0.0)
        )
    }

    override suspend fun getReservationSummary(id: String): ReservationSummaryModel {
        val parking = mockList.find { it.id == id }
        return ReservationSummaryModel(
            id = id,
            parkingName = parking?.name ?: "Estacionamiento Central",
            address = parking?.address ?: "Av. Principal 123",
            assignedSpace = "A-12",
            entryTime = "09:30 AM",
            estimatedDuration = "2 horas",
            totalCost = ((parking?.pricePerHour?.amount ?: 5.0) * 2), // Mocking calculation
            paymentMethod = "Pagado con QR"
        )
    }
}