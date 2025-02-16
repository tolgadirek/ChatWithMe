package com.tolgadirek.chatwithme.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tolgadirek.chatwithme.model.Mesaj

@Dao
interface MesajDAO {

    @Insert
    suspend fun mesajEkle(vararg mesaj : Mesaj) : List<Long>
    // eklediği besinlerin id'sini long olarak geri veriyor.

    @Query("SELECT * FROM Mesaj ORDER BY tarih ASC")
    suspend fun tumMesajlariGetir(): List<Mesaj>

    @Query("DELETE FROM Mesaj")
    suspend fun tumMesajlariSil()

}
