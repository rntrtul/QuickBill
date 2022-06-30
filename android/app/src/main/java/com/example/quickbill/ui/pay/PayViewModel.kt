package com.example.quickbill.ui.pay

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickbill.api.API
import com.journeyapps.barcodescanner.ScanIntentResult

class PayViewModel : ViewModel() {

    private var _scanSuccessful = false
    private val _text = MutableLiveData<String>().apply {
        value = "This is pay Fragment"
    }
    val text: LiveData<String> = _text
    val scanSuccessful get() = _scanSuccessful

    fun processQrResult(result: ScanIntentResult) {
        val TAG = "PayFragment - processQrResult()"
        val contents = result.contents!!

        val scanTokens = contents.split('-')
        Log.d(TAG, "Scan Tokens: $scanTokens")

        if (scanTokens.size != 3) {
            Log.e(TAG, "Too many tokens in QR")
            return
        }

        val locationId = scanTokens[0]

        val tableNum = try {
            Integer.parseInt(scanTokens[1])
        } catch (exception: Exception) {
            Log.e(TAG, "Could not parse table number!")
            return
        }

        val restaurantName = scanTokens[2]

        // Set the location ID and table num (also requesting the bill from the API).
        API.instance.setLocationAndTableNum(locationId, tableNum, restaurantName)

        Log.d(TAG, "API instance Bill: " + API.instance.order)

        if (API.instance.order != null) {
            _scanSuccessful = true
        } else {
            Log.e(TAG, "Bill was null!")
        }
    }
}