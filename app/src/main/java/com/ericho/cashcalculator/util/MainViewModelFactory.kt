package com.ericho.cashcalculator.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ericho.cashcalculator.MainViewModel
import com.ericho.cashcalculator.data.repository.CashRecordRepository

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(CashRecordRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}