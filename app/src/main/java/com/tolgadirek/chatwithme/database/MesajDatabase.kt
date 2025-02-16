package com.tolgadirek.chatwithme.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tolgadirek.chatwithme.model.Mesaj

@Database(entities = [Mesaj::class], version = 1)
abstract class MesajDatabase:RoomDatabase() {
    abstract fun mesajDao(): MesajDAO

    companion object {
        @Volatile
        private var INSTANCE: MesajDatabase? = null

        fun databaseOlustur(context: Context): MesajDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MesajDatabase::class.java,
                    "MesajDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}