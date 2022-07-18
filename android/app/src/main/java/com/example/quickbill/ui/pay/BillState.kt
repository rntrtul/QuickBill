package com.example.quickbill.ui.pay

import android.util.Log
import com.example.quickbill.api.API

class BillState {
    private object Holder {
        val instance = BillState()
    }

    companion object {
        val instance: BillState by lazy { Holder.instance }
    }

    var locationId: String? = null
    var tableNum: Int? = null
    var restaurantName: String? = null
    var amountToPay = 0
    var order: Order? = null

    fun reset() {
        locationId = null
        tableNum = null
        restaurantName = null
        amountToPay = 0
        order = null
    }
}