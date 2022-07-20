package com.example.quickbill.ui.pay

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickbill.api.API
import com.example.quickbill.util.QRUtil
import com.journeyapps.barcodescanner.ScanIntentResult

class PayViewModel : ViewModel() {

    private var _scanSuccessful = false
    private var _scanValid = true
    private val _text = MutableLiveData<String>().apply {
        value = "This is pay Fragment"
    }
    val text: LiveData<String> = _text
    val scanSuccessful get() = _scanSuccessful
    val scanValid get() = _scanValid

    fun processQrResult(result: ScanIntentResult) {
        val TAG = "PayFragment - processQrResult()"

        val parsedQRCode = QRUtil.parsedQRCodeFactory( result )
        _scanValid = parsedQRCode != null
        if (!_scanValid) {
            return
        }

        // Set the location ID and table num (also requesting the bill from the API).
        parsedQRCode!!.setBill( BillState.instance )
        API.callBill( BillState.instance )

        Log.d(TAG, "API instance Bill: " + BillState.instance.billResponse!!.order)

        if (BillState.instance.billResponse != null) {
            _scanSuccessful = true
        } else {
            Log.e(TAG, "Bill was null!")
        }
    }
}