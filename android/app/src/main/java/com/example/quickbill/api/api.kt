package com.example.quickbill.api


import android.util.Log
import com.example.quickbill.ui.pay.Bill
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.util.*


class API {

    private val baseURL = "https://quickbill.alexnainer.com/api/"

    var amountToPay = 0
    var bill: Bill? = null

    private object Holder {
        val instance = API()
    }

    companion object {
        val instance: API by lazy { Holder.instance }
    }

    var locationId: String? = null
    var tableNum: Int? = null
    var restaurantName: String? = null

    fun setLocationAndTableNum( locationId : String?, tableNum : Int?, restaurantName : String? ) {
        this.locationId = locationId
        this.tableNum = tableNum
        this.restaurantName = restaurantName
        this.callBill()
    }

    fun invalidateLocationAndTableNum() {
        locationId = null
        tableNum = null
    }

    fun isQrCodeScanned() : Boolean {
        if ( locationId == null || tableNum == null ) return false;
        return true;
    }

    fun callBill() {
        var result: String = ""
        var job = GlobalScope.launch(Dispatchers.IO) {
            try {
                result = URL( baseURL + "order/" + "location/" + locationId + "/table/" + tableNum).readText()
            } catch (e: Exception) {
                result = ""

            }

        }
        runBlocking {
            job.join() // wait until child coroutine completes
        }
        if (result != "") {
            this.bill = Gson().fromJson(result, Bill::class.java)
        }
    }

    fun makePayment(nonce: String): String {
        val payment_url = baseURL + "payment"
        val idempotencyKey: UUID = UUID.randomUUID() // Generates random UUID
        val sourceId: String = nonce
        val amount: Int = amountToPay

        val jsonObject = JSONObject();
        try {
            jsonObject.put("sourceId", sourceId);
            jsonObject.put("idempotencyKey", idempotencyKey.toString());
            jsonObject.put("amountMoney", amount.toString());
        } catch (e: JSONException) {
            e.printStackTrace();
        }

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonObject.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(payment_url)
            .post(body)
            .build()

        var response: Response? = null
        var resStr = ""
        try {
            response = client.newCall(request).execute()
            resStr = response.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return resStr
    }
}


