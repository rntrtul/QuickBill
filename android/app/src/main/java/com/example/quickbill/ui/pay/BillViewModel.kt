package com.example.quickbill.ui.pay

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.quickbill.api.API


class BillViewModel : ViewModel() {

    private val _order: Order? = API.instance.order

    private val _items = _order?.lineItems?.toMutableStateList()
    private val _totalCost = mutableStateOf(0)

    val items: SnapshotStateList<OrderItem>? get() = _items
    val totalCost: Int get() = _totalCost.value

    fun itemSelected(item: OrderItem, selected: Boolean) {
        item.selected = selected
        if (selected) {
            _totalCost.value += item.totalMoney.amount
        } else {
            _totalCost.value -= item.totalMoney.amount
        }
    }

    fun billTotal(): Int {
        return _items?.sumOf { orderItem -> orderItem.totalMoney.amount } ?: 0
    }
}


