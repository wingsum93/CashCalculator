package com.ericho.cashcalculator.util

import com.ericho.cashcalculator.R
import java.text.NumberFormat
import java.util.Locale

fun Long.toHKCurrencyString(): String {
    val number = this.toDouble()
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "HK"))
    formatter.maximumFractionDigits = 0
    return formatter.format(number)
}

object Utils {

    fun getDenominationImageResource(denomination: Int): Int {
        return when (denomination) {
            1000 -> R.drawable.one_thousand_note
            500 -> R.drawable.five_hundred_note
            100 -> R.drawable.hundred_note
            50 -> R.drawable.fifty_note
            20 -> R.drawable.twenty_note
            10 -> R.drawable.ten_note
            else -> R.drawable.ten_note
        }
    }


}