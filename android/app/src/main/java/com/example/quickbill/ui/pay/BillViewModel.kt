package com.example.quickbill.ui.pay

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.quickbill.api.API


class BillViewModel : ViewModel() {

    private val _order: Order? = API.instance.order
    private val _items = _order?.lineItems?.toMutableStateList()

    val items: List<OrderItem> get() = _items!!

    fun itemSelected(item: OrderItem, selected: Boolean) {
        items.find { it.name == item.name }?.let { it ->
            it.selected = selected
            Log.d("BILLVIEWMODEL", it.toString())
        }
    }

    fun paymentTotal(): Int {
        return _items?.sumOf { orderItem ->
            if (orderItem.selected) orderItem.totalMoney.amount else 0
        } ?: 0
    }

    fun selectedItems(): List<OrderItem> {
        return _items?.filter { item -> item.selected }!!
    }

    fun billTotal(): Int {
        return _items?.sumOf { orderItem -> orderItem.totalMoney.amount } ?: 0
    }
}


