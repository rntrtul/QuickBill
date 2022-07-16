package com.example.quickbill.ui.pay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Order(
    val id: String,
    val locationId: String,
    val lineItems: List<OrderItem>,
    val totalMoney: Money,
    val restaurantName: String
)

data class Payment(
    val id: String,
    val amountMoney: Money,
    val totalMoney: Money,
    val status: String,
    val sourceType: String,
    val locationId: String,
    val orderId: String,
)

data class Money(
    val amount: Int,
    val currency: String
)

// default values due to: https://github.com/google/gson/issues/513
// mutable is null when mixed default values
class OrderItem(
    val name: String = "",
    val quantity: Int = 0,
    val variationName: String = "",
    val totalMoney: Money = Money(0, ""),
    val alreadyPaid: Boolean = false,
    initialSelected: Boolean = false
) {
    var selected by mutableStateOf(initialSelected)
}