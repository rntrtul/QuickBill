package com.example.quickbill.ui.pay

data class Order(
    val id: String,
    val locationId: String,
    val lineItems: ArrayList<OrderItem>,
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
    var lineItems: ArrayList<OrderItem>,
)

data class Money(
    val amount: Int,
    val currency: String
)

data class OrderItem(
    val name: String,
    val quantity: String,
    val variationName: String,
    val totalMoney: Money,
    val alreadyPayed: Boolean = false,
    var selected: Boolean = false
)