package com.easypark.app.shared.domain.repository

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation
import com.easypark.app.parkingdetails.domain.model.ParkingDetail
import com.easypark.app.shared.domain.model.ParkingModel

interface ParkingRepository {
    suspend fun getAvailableParkings(): List<ParkingModel>
    suspend fun getParkingDetail(id: String): ParkingDetail
    suspend fun registerParking(parking: ParkingModel): Boolean
    suspend fun getBookingConfirmation(id: String): BookingConfirmation
}