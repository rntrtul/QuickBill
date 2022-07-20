package com.example.quickbill.ui.pay

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.quickbill.api.API


class BillViewModel : ViewModel() {

    private val _order: Order? = BillState.instance.billResponse?.order
    private val _items = _order?.lineItems?.toMutableStateList()
    private val _billItems = billFromOrder()
    private var _paymentTotal by mutableStateOf(0)

    val items: List<BillItem> get() = _billItems
    val paymentTotal get() = _paymentTotal

    private fun billFromOrder(): SnapshotStateList<BillItem> {
        Log.d("API LOG", "order item list: $_items")
        if ( _items == null ) { return listOf<BillItem>().toMutableStateList() }
        val a = _items?.map { orderItem ->
            BillItem(
                order = orderItem,
                initialQuantitySelected = orderItem.quantity
            )
        }

        return a!!.toMutableStateList()
    }

    private fun calcPaymentTotal() {
        _paymentTotal = selectedItems().sumOf { billItem ->
            billItem.amountPaying.amount
        }
    }

    fun itemSelected(item: BillItem, selected: Boolean) {
        _billItems.find { it.order.name == item.order.name }?.let { it ->
            it.selected = selected
            calcPaymentTotal()
        }
    }

    fun itemQuantityChosen(item: BillItem, quantity: Int) {
        _billItems.find { it.order.name == item.order.name }?.let { it ->
            it.quantitySelected = quantity
            it.amountPaying.amount = it.quantitySelected * it.order.basePriceMoney.amount
            calcPaymentTotal()
            Log.d("BILLVM", "${it.amountPaying} $quantity")
        }
    }

    fun selectedItems(): List<BillItem> {
        return _billItems.filter { item -> item.selected }
    }

    fun billTotal(): Int {
        return _order!!.totalMoney.amount
    }
}
