package com.example.quickbill.api


import android.util.Log
import com.example.quickbill.ui.pay.Order
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.ui.pay.BillResponse
import com.example.quickbill.ui.pay.BillState
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
    companion object {

        // TODO: should not have trailing slash - e.g. baseUrl + "/order
        private const val baseURL = "https://quickbill.alexnainer.com/api/"

        fun callBill(billState: BillState) {
            Log.d("API", "Call Bill!!")
            var result = ""
            val job = GlobalScope.launch(Dispatchers.IO) {
                try {
                    result =
                        URL(baseURL + "order/" + "location/" + billState.locationId + "/table/" + billState.tableNum).readText()

                    Log.d("API LOG", "got result: $result")
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
                BillState.instance.billResponse = Gson().fromJson(result, BillResponse::class.java)
                // Testing line items part
//                var payment: Payment = Gson().fromJson(JSONObject(result).getJSONObject("order").toString(), Payment::class.java)
//                Log.d("FirebaseLog", "Going to add to Firebase")
//                Log.d("FirebaseLog", payment.toString())
//                var res = FirebaseManager.addOrderToFirebase(payment)
            }
        }

        fun sendPaymentRequest(requestBody: JSONObject): Payment? {
            val paymentURL = baseURL + "payment"
            val client = OkHttpClient()
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestBody.toString().toRequestBody(mediaType)
            val request: Request = Request.Builder()
                .url(paymentURL)
                .post(body)
                .build()

            val response: Response?
            var payment: Payment? = null
            try {
                response = client.newCall(request).execute()
                Log.d("NETWORK LOG", "Response: $response")
                payment = Gson().fromJson(response.body.string(), Payment::class.java)
                Log.d("API", "Going to add to Firebase")
                Log.d("API", payment.toString())
//                var res = FirebaseManager.addOrderToFirebase(payment)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return payment
        }
    }
}


