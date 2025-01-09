package com.ericho.cashcalculator.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ericho.cashcalculator.data.model.CashRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCashRecord(cashRecord: CashRecord)

    @Query("SELECT * FROM cash_records ORDER BY id DESC")
    fun getSavedRecords(): Flow<List<CashRecord>>

    @Query("SELECT * FROM cash_records ORDER BY CASE WHEN :isAsc = 1 THEN id END ASC, CASE WHEN :isAsc = 0 THEN id END DESC")
    fun getAllCashRecords(isAsc: Boolean): Flow<List<CashRecord>>

    @Delete
    suspend fun deleteRecord(cashRecord: CashRecord)

}