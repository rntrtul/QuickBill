package com.example.quickbill.ui.pay

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class BillViewModel : ViewModel() {
    private val _items = getOrderItems().toMutableStateList()
    private val _totalCost = mutableStateOf(0.0)

    val items: List<OrderItem> get() = _items
    val totalCost: Double get() = _totalCost.value

    fun itemSelected(item: OrderItem, selected: Boolean) {
        item.selected = selected
        if (selected) {
            _totalCost.value += item.cost
        } else {
            _totalCost.value -= item.cost
        }
    }
}

val chars = ('A'..'Z') + ('a'..'z')
private fun getOrderItems() = List(15) { i ->
    val length = (1..20).random()
    val name = (1..length).map { chars.random() }.joinToString("")
    OrderItem(name, i.toDouble())
}
