package com.easypark.app.core.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.SQLiteDriver
import com.easypark.app.bookingconfirmation.data.dao.BookingConfirmationDao
import com.easypark.app.core.data.dao.ReservationDao
import com.easypark.app.notifications.data.dao.NotificationDao
import com.easypark.app.parkingdetails.data.dao.ParkingDetailDao
import com.easypark.app.core.data.dao.SpaceDao
import com.easypark.app.core.data.dao.UserDao
import com.easypark.app.core.data.entity.*
import com.easypark.app.findparking.data.dao.FindParkingDao
import com.easypark.app.registerparking.data.dao.RegisterParkingDao
import com.easypark.app.reservationhistory.data.dao.ReservationHistoryDao
import com.easypark.app.reservationsummary.data.dao.ReservationSummaryDao
import com.easypark.app.signin.data.dao.SignInDao
import com.easypark.app.register.data.dao.RegisterDao
import com.easypark.app.registervehicle.data.dao.RegisterVehicleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        UserEntity::class,
        VehicleEntity::class,
        ParkingEntity::class,
        ReservationEntity::class,
        SpaceEntity::class,
        NotificationEntity::class,
        ReviewEntity::class,
        RemoteConfigEntity::class
    ],
    version = 2,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spaceDao(): SpaceDao
    abstract fun userDao(): UserDao
    abstract fun findParkingDao(): FindParkingDao
    abstract fun notificationDao(): NotificationDao
    abstract fun parkingDao(): ParkingDetailDao
    abstract fun registerParkingDao(): RegisterParkingDao
    abstract fun reservationHistoryDao(): ReservationHistoryDao
    abstract fun reservationSummaryDao(): ReservationSummaryDao
    abstract fun signInDao(): SignInDao
    abstract fun registerDao(): RegisterDao
    abstract fun registerVehicleDao(): RegisterVehicleDao
    abstract fun reservationDao(): ReservationDao
    abstract fun bookingConfirmationDao(): BookingConfirmationDao
    abstract fun remoteConfigDao(): RemoteConfigDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect fun getDatabaseBuilder(ctx: Any? = null): RoomDatabase.Builder<AppDatabase>

fun createDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
    driver: SQLiteDriver = BundledSQLiteDriver()
): AppDatabase {
    return builder
        .setDriver(driver)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}