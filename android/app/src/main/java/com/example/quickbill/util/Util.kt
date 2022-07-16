package com.example.quickbill.util

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

// Used for displaying prices and amount to pay.
fun centsToDisplayedAmount(amount: Int): String {
    val dollars = amount / 100
    val cents = amount % 100

    return if (cents < 10) "$$dollars.0$cents" else "$$dollars.$cents"
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
