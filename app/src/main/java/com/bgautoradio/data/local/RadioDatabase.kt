package com.bgautoradio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RadioStationEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class RadioDatabase : RoomDatabase() {
    abstract fun stationDao(): RadioStationDao

    companion object {
        fun create(context: Context): RadioDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                RadioDatabase::class.java,
                "bg_auto_radio.db"
            )
            .fallbackToDestructiveMigration()
            .build()
    }
}
