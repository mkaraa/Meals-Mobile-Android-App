package com.example.mealapp.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mealapp.model.Tariff
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface TariffDao {

    @Query("SELECT * FROM Tariff")
    fun getAllTariffs(): Flowable<List<Tariff>>

    @Query("SELECT * FROM Tariff WHERE id = :id")
    fun getTariffById(id: Int): Flowable<Tariff>

    @Insert
    fun saveTariff(tariff: Tariff): Completable

    @Delete
    fun deleteTariff(tariff: Tariff): Completable
}