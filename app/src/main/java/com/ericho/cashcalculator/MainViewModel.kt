package com.ericho.cashcalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ericho.cashcalculator.data.model.CashRecord
import com.ericho.cashcalculator.data.repository.CashRecordRepository
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: CashRecordRepository
) : ViewModel() {
    val denominations = listOf(1000, 500, 100, 50, 20, 10) //面值，面額
    var counts = mutableStateListOf<String>()
    var total: Long by mutableLongStateOf(0) //總金額
    var totalNotes: Int by mutableIntStateOf(0) //紙幣總數

    private var isAscending = MutableStateFlow(true)

    val savedRecords = isAscending.flatMapLatest { isAsc ->
        repository.getAllCashRecords(isAsc)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val gson = Gson()

    init {
        resetCalculator()
    }

    fun updateCount(index: Int, count: String) {
        counts[index] = count
        calculateTotal()
    }

    private fun calculateTotal() {
        total = denominations.zip(counts)
            .sumOf { (denom, count) -> denom * (count.toLongOrNull() ?: 0) }
        totalNotes = counts.sumOf { it.toIntOrNull() ?: 0 }
    }

    fun resetCalculator() {
        counts.clear()
        counts.addAll(List(denominations.size) { "" })
        total = 0
        totalNotes = 0
        isAscending.value = false
    }

    fun saveRecord(record: CashRecord) {
        viewModelScope.launch {
            repository.saveCashRecord(record)
        }
    }

    fun deleteRecord(record: CashRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    fun toggleRecordOrder() {
        isAscending.value = !isAscending.value
        println("toggleRecordOrder: ${isAscending.value}")
        println(gson.toJson(savedRecords.value))
    }
}
