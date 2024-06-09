package com.example.mealapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tariff(
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "ingredients")
    var ingredients: String,
    @ColumnInfo(name = "image")
    var image: ByteArray
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tariff

        if (name != other.name) return false
        if (ingredients != other.ingredients) return false
        if (!image.contentEquals(other.image)) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + id
        return result
    }
}
