package com.example.quickbill.ui.pay

import com.example.quickbill.api.API
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel


class BillViewModel : ViewModel() {

    private val _bill: Bill = API.instance.getBill()

    private val _items = _bill.lineItems.toMutableStateList()
    private val _totalCost = mutableStateOf(0.0)

    val items: List<OrderItem> get() = _items
    val totalCost: Double get() = _totalCost.value

    fun itemSelected(item: OrderItem, selected: Boolean) {
        item.selected = selected
        if (selected) {
            _totalCost.value += item.totalMoney.amount.toDouble()
        } else {
            _totalCost.value -= item.totalMoney.amount.toDouble()
        }
    }
}


