package com.example.quickbill.ui.pay

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickbill.api.API
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class BillViewModel : ViewModel() {

    private var _order: Order? = BillState.instance.billResponse?.order
    private var _items = _order?.lineItems?.toMutableStateList()
    private var _billItems = billFromOrder()
    private var _paymentTotal by mutableStateOf(0)
    private val _isRefreshing = MutableStateFlow(false)

    val items: List<BillItem> get() = _billItems
    val paymentTotal get() = _paymentTotal
    val isRefreshing get() = _isRefreshing.asStateFlow()

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

    fun refreshBill() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            API.callBill(BillState.instance)
            _order = BillState.instance.billResponse?.order
            _items = _order?.lineItems?.toMutableStateList()
            _billItems = billFromOrder()
            _isRefreshing.emit(false)
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
