package com.easypark.app.shared.data.repository

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation
import com.easypark.app.parkingdetails.domain.model.ParkingDetail
import com.easypark.app.shared.domain.model.ParkingModel
import com.easypark.app.shared.domain.repository.ParkingRepository

class ParkingRepositoryImpl : ParkingRepository {

    // Datos compartidos para que coincidan
    private val mockList = listOf(
        ParkingModel(
            "1",
            "Estacionamiento Central Oquendo",
            "Calle Oquendo 123",
            3.0,
            true,
            -17.3935,
            -66.1570
        ),
        ParkingModel("2", "Parqueo Viedma", "Av. Aniceto Arce", 5.0, true, -17.3895, -66.1490),
        ParkingModel("3", "Garage Norte", "Av. America", 4.0, false, -17.3750, -66.1550)
    )

    override suspend fun getAvailableParkings(): List<ParkingModel> = mockList

    override suspend fun getParkingDetail(id: String): ParkingDetail {
        val basicInfo = mockList.find { it.id == id }
        return ParkingDetail(
            id = id,
            name = basicInfo?.name ?: "Parqueo $id",
            address = basicInfo?.address ?: "Dirección desconocida",
            pricePerHour = "${basicInfo?.pricePerHour ?: 0.0} Bs",
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
            pricePerHour = parking?.pricePerHour ?: 0.0
        )
    }
}