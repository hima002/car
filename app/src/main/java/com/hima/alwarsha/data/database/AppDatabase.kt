package com.hima.alwarsha.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hima.alwarsha.data.dao.CarDao
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.entity.CarMaintenanceConfigEntity
import com.hima.alwarsha.data.entity.FuelLogEntity
import com.hima.alwarsha.data.entity.MaintenanceItemEntity
import com.hima.alwarsha.data.entity.ServiceLogEntity
import com.hima.alwarsha.data.entity.TripLogEntity
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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cars ADD COLUMN oilType TEXT NOT NULL DEFAULT 'FULL_SYNTHETIC'")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cars ADD COLUMN engineVariant TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alwarsha_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
                        database.carDao().insertMaintenanceItems(DefaultMaintenanceCatalog.items)
                    }
                }
            }
        }
    }
}
