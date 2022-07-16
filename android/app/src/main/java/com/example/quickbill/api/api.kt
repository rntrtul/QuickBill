package com.example.quickbill.api


import android.util.Log
import com.example.quickbill.ui.pay.Order
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.ui.pay.Payment
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.*


class API {

    // TODO: should not have trailing slash - e.g. baseUrl + "/order
    private val baseURL = "https://quickbill.alexnainer.com/api/"
    var amountToPay = 0

    // TODO: API class should be stateless - should not have a bill class, instead caller will pass params to api function calls
    var order: Order? = null

    private object Holder {
        val instance = API()
    }

    companion object {
        val instance: API by lazy { Holder.instance }
    }

    // TODO: this should be part of a class
    var locationId: String? = null
    var tableNum: Int? = null
    var restaurantName: String? = null

    // TODO: this should not exist
    fun setLocationAndTableNum(locationId: String?, tableNum: Int?, restaurantName: String?) {
        Log.d("API", "locationId $locationId")
        Log.d("API", "tableNum $tableNum")
        Log.d("API", "restaurantName $restaurantName")
        this.locationId = locationId
        this.tableNum = tableNum
        this.restaurantName = restaurantName
        this.callBill()
    }

    // TODO: same as above, API should not have a state
    fun invalidateLocationAndTableNum() {
        locationId = null
        tableNum = null
    }

    fun callBill() {
        Log.d("API", "Call Bill!!")
        var result = ""
        val job = GlobalScope.launch(Dispatchers.IO) {
            try {
                result =
                    URL(baseURL + "order/" + "location/" + locationId + "/table/" + tableNum).readText()
            } catch (e: Exception) {
                Log.e("API", "$e")
                Log.e("API", "Error calling bill")
                result = ""

            }

        }
        runBlocking {
            job.join() // wait until child coroutine completes
        }
        if (result != "") {
            this.order = Gson().fromJson(result, Order::class.java)
        }
    }

    // TODO: orderId should be passed in
    fun makePayment(nonce: String): Payment? {
        val payment_url = baseURL + "payment"
        val orderId = this.order?.id
        val idempotencyKey: UUID = UUID.randomUUID() // Generates random UUID
        val sourceId: String = nonce
        val amount: Int = amountToPay

        val jsonObject = JSONObject()
        try {
            jsonObject.put("sourceId", sourceId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("idempotencyKey", idempotencyKey.toString())
            jsonObject.put("amountMoney", amount.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonObject.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(payment_url)
            .post(body)
            .build()

        val response: Response?
        var payment: Payment? = null
        try {
            response = client.newCall(request).execute()
            payment = Gson().fromJson(response.body.string(), Payment::class.java)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return payment
    }

    // TODO: orderId should be passed in
    fun attachPaymentToOrder(payment: Payment): Boolean {
        if (this.order?.id == null) {
            return false
        }
        val orderId = this.order?.id
        val url = baseURL + "order/" + orderId + "/pay"
        val idempotencyKey: UUID = UUID.randomUUID() // Generates random UUID
        val paymentIds: Array<String> = arrayOf(payment.id)

        val jsonObject = JSONObject()
        try {
            jsonObject.put("orderId", orderId)
            jsonObject.put("idempotencyKey", idempotencyKey.toString())
            jsonObject.put("paymentIds", JSONArray(paymentIds))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonObject.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response: Response?
        try {
            response = client.newCall(request).execute()
            Log.d("API", "Response: $response")
            if (response.isSuccessful) {
                Log.d("API", "Response successful!!")
//                var addedToDb = FirebaseManager.addOrderToFirebase(response)
//                if (addedToDb) {
//                    Log.d("FirebaseManager","Successfully added to db")
//                    return true
//                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return false
    }
}


