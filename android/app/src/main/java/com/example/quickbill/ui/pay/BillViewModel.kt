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
            it.chosen = selected
            Log.d("BILLVIEWMODEL", it.toString())
        }
    }

    fun selectedItems(): List<OrderItem> {
        return _items?.filter { item -> item.chosen }!!
    }

    fun paymentTotal(): Int {
        return selectedItems().sumOf { orderItem ->
            if (orderItem.chosen) orderItem.totalMoney.amount else 0
        }
    }

    fun billTotal(): Int {
        return _order!!.totalMoney.amount
    }
}


