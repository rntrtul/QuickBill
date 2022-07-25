package com.example.quickbill.ui.pay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quickbill.util.centsToDisplayedAmount
import com.google.firebase.firestore.auth.User
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

data class BillResponse(
    val order: Order,
    val userOrders: List<UserOrder>
)

data class UserOrder(
    val userId: String,
    val items: List<SquareItem>,
    val amount: Int
)

data class SquareItem(
    val itemId: String,
    val amount: Money,
    val quantity: Int
)

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
    var lineItems: ArrayList<OrderItem>,
    var date: Date,
)

data class Money(
    var amount: Int,
    val currency: String
) {
    fun displayAmount(): String {
        return centsToDisplayedAmount(amount)
    }
}

data class OrderItem(
    val name: String,
    val quantity: Int,
    val variationName: String,
    val totalMoney: Money,
    val basePriceMoney: Money,
)

class BillItem(
    val order: OrderItem,
    var alreadyPaid: Boolean = false,
    var amountPaid : Money = Money(0, "CAD"),
    initialSelected: Boolean = false,
    initialQuantitySelected: Int = 1,
    initialAmountPaying: Money = Money(0, ""),
) {
    var selected by mutableStateOf(initialSelected)
    var quantitySelected by mutableStateOf(initialQuantitySelected)
    var amountPaying by mutableStateOf(initialAmountPaying)

    init {
        amountPaying.amount = quantitySelected * order.basePriceMoney.amount
    }
}