package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CarDao
import com.example.data.entity.CarEntity
import com.example.data.entity.CarMaintenanceConfigEntity
import com.example.data.entity.FuelLogEntity
import com.example.data.entity.MaintenanceItemEntity
import com.example.data.entity.ServiceLogEntity
import com.example.data.entity.TripLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CarEntity::class,
        MaintenanceItemEntity::class,
        CarMaintenanceConfigEntity::class,
        ServiceLogEntity::class,
        FuelLogEntity::class,
        TripLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `trip_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `carId` INTEGER NOT NULL,
                        `dayEpoch` INTEGER NOT NULL,
                        `distanceKm` REAL NOT NULL,
                        FOREIGN KEY(`carId`) REFERENCES `cars`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_trip_logs_carId_dayEpoch` ON `trip_logs` (`carId`, `dayEpoch`)"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autokeep_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.carDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(carDao: CarDao) {
            // Insert default maintenance items catalog ONLY (no fake/sample cars or logs)
            carDao.insertMaintenanceItems(PrepopulateData.defaultMaintenanceItems)
        }
    }
}
