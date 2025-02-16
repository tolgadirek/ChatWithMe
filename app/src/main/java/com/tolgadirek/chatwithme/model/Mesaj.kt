package com.tolgadirek.chatwithme.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mesaj(
    @ColumnInfo(name = "tarih")
    @PrimaryKey val tarih: Long,  // ID olarak tarih kullanıyoruz

    @ColumnInfo(name = "mesaj")
    val mesaj: String,

    @ColumnInfo(name = "kullaniciMi")
    val kullaniciMi: Boolean
)