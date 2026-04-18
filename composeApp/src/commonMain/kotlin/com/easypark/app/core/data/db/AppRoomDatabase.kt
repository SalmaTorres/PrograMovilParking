package com.easypark.app.core.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.easypark.app.core.data.dao.NotificationDao
import com.easypark.app.core.data.dao.ParkingDao
import com.easypark.app.core.data.dao.ReservationDao
import com.easypark.app.core.data.dao.SpaceDao
import com.easypark.app.core.data.dao.UserDao
import com.easypark.app.core.data.dao.VehicleDao
import com.easypark.app.core.data.entity.NotificationEntity
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.core.data.entity.VehicleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        UserEntity::class,
        VehicleEntity::class,
        ParkingEntity::class,
        ReservationEntity::class,
        SpaceEntity::class,
        NotificationEntity::class
    ],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun parkingDao(): ParkingDao
    abstract fun reservationDao(): ReservationDao
    abstract fun notificationDao(): NotificationDao
    abstract fun spaceDao(): SpaceDao
    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect fun getDatabaseBuilder(ctx: Any? = null): RoomDatabase.Builder<AppDatabase>

fun createDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver()) // Importante: Usa el driver empaquetado
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}