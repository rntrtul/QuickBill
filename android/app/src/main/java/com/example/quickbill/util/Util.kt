package com.example.quickbill.util

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.concurrent.TimeUnit

// Used for displaying prices and amount to pay.
fun centsToDisplayedAmount(amount: Int): String {
    val dollars = amount / 100
    val cents = amount % 100

    return if (cents < 10) "$$dollars.0$cents" else "$$dollars.$cents"
}

fun parseMoney(s: String): Int {

    val cleaned = s.replace("""[$,.]""".toRegex(), "")

    if (cleaned.isEmpty()) {
        Log.d("PARSE", cleaned)
        return -1
    }
    var num = cleaned.toInt()

    if (!s.contains('.')) {
        num *= 100
    }

    return num
}

fun validateMoneyAmount(
    s: String,
    max: Int
): Boolean {
    // validates only 1 decimal and less than max and > 0
    if (s.count { char -> char == '.' } > 1) {
        Log.d("VALIDATE", "too many .")
        return false
    }

    val num = parseMoney(s)

    if (num > max || num < 0) {
        Log.d("VALIDATE", "num ($num) out of range 0-$max")
        return false
    }

    return true
}

fun daysBetween(start: Date, end: Date, endIncluded: Boolean = false): Int {
    val days = TimeUnit.DAYS.convert(end.time - start.time, TimeUnit.MILLISECONDS).toInt()
    return if (endIncluded) days + 1 else days
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
