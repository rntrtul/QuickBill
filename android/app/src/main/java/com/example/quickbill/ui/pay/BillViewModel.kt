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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray


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
        if (_items == null) {
            return listOf<BillItem>().toMutableStateList()
        }

        val userOrders: List<UserOrder>? = BillState.instance.billResponse?.userOrders
        _items!!.forEach { orderItem ->

        }
        if (userOrders != null) {
            if (userOrders.isNotEmpty()) {

            }
        }
        val a = _items?.map { orderItem ->
            var itemAmountPaid = 0
            userOrders?.forEach { userOrder ->
                itemAmountPaid += userOrder.items.filter { item -> item.itemId == orderItem.name }
                    .sumOf { item -> item.amount.amount }
            }

            BillItem(
                order = orderItem,
                amountPaid = Money(itemAmountPaid, "CAD"),
                initialQuantitySelected = orderItem.quantity,
                alreadyPaid = orderItem.totalMoney.amount == itemAmountPaid
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
        Log.d("BILLVIEWMODEL", "==Refresh Bill")
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

    fun itemAmountPayingChange(item: BillItem, amount: Int) {
        _billItems.find { it.order.name == item.order.name }?.let { it ->
            it.amountPaying.amount = amount
            calcPaymentTotal()
        }
    }

    fun selectedItems(): List<BillItem> {
        return _billItems.filter { item -> item.selected }
    }

    fun billTotal(): Int {
        return _order!!.totalMoney.amount
    }
}
