package com.ericho.cashcalculator.data.model

import android.text.format.DateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ericho.cashcalculator.data.util.MapConverter

@Entity(tableName = "cash_records")
@TypeConverters(MapConverter::class)
data class CashRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val total: Long,
    val noteDescription: Map<Int, String>, // {(2000,"34"),(500,"20")}
    val totalNotes: Int,
    val message: String?,
    val date: Long
) {
    var formattedDate: String = DateFormat.format("dd/MM/yyyy hh:mm aa", date).toString()
}
