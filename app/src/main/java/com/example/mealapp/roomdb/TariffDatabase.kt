package com.example.mealapp.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mealapp.model.Tariff

@Database(entities = [Tariff::class], version = 1)
abstract class TariffDatabase : RoomDatabase() {
    abstract fun tariffDao(): TariffDao
}