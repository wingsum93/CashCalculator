package com.ericho.cashcalculator.data.repository

import android.content.Context
import com.ericho.cashcalculator.data.local.RecordsDatabase
import com.ericho.cashcalculator.data.model.CashRecord

class CashRecordRepository(private val context: Context) {

    private val recordsDao = RecordsDatabase.getDatabase(context).getRecordsDao()

    suspend fun saveCashRecord(cashRecord: CashRecord) {
        recordsDao.saveCashRecord(cashRecord)
    }

    fun getAllCashRecords(isAsc: Boolean) =
        recordsDao.getAllCashRecords(isAsc = isAsc)


    suspend fun deleteRecord(cashRecord: CashRecord) {
        recordsDao.deleteRecord(cashRecord)
    }

}