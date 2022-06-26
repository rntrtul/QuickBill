package com.example.quickbill.ui.pay

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking


class BillViewModel : ViewModel() {
    private val _bill: Bill = getBill()

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


private fun getBill(): Bill {
    var result: String = ""
    var job = GlobalScope.launch(Dispatchers.IO) {
        result = URL("https://quickbill.alexnainer.com/api/order/location/L3GAERGV19EXB/table/1").readText()
    }
    runBlocking {
        job.join() // wait until child coroutine completes
    }
    return Gson().fromJson(result, Bill::class.java)
}
