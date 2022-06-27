package com.example.quickbill.api


import com.example.quickbill.ui.pay.Bill
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL


class API {

    private val baseURL = "https://quickbill.alexnainer.com/api/"

    private object Holder {
        val instance = API()
    }

    companion object {
        val instance: API by lazy { Holder.instance }
    }

    var locationId: String? = null
    var tableNum: Int? = null

    fun setLocationAndTableNum( locationId : String?, tableNum : Int? ) {
        this.locationId = locationId
        this.tableNum = tableNum
    }

    fun isQrCodeScanned() : Boolean {
        if ( locationId == null || tableNum == null ) return false;
        return true;
    }

    fun getBill(): Bill {
        var result: String = ""
        var job = GlobalScope.launch(Dispatchers.IO) {
            result = URL( baseURL + "order/" + "location/" + locationId + "/table/" + tableNum).readText()
        }
        runBlocking {
            job.join() // wait until child coroutine completes
        }
        return Gson().fromJson(result, Bill::class.java)
    }
}


