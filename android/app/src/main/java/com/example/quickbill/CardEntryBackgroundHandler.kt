package com.example.quickbill

import android.util.Log
import com.example.quickbill.api.API
import com.example.quickbill.ui.pay.BillState
import com.example.quickbill.ui.pay.Payment
import sqip.CardDetails
import sqip.CardEntryActivityCommand
import sqip.CardNonceBackgroundHandler
import java.io.IOException

class CardEntryBackgroundHandler: CardNonceBackgroundHandler {
    override fun handleEnteredCardInBackground(cardDetails : CardDetails): CardEntryActivityCommand {
        try {
            val payment: Payment? = API.instance.makePayment(cardDetails.nonce, BillState.instance)
            Log.d("NETWORK LOG", "--------------------------")
            Log.d("NETWORK LOG", payment.toString())
            Log.d("NETWORK LOG", "--------------------------")

            if (payment != null) {
                val isOrderPaid: Boolean = API.instance.attachPaymentToOrder(payment)
            }


            return CardEntryActivityCommand.Finish()
        } catch(exception: IOException) {
            return CardEntryActivityCommand.ShowError("Payment is invalid")
        }
    }
}