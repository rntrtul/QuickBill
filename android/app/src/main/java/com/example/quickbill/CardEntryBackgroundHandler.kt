package com.example.quickbill

import android.util.Log
import com.example.quickbill.ui.pay.BillItem
import com.example.quickbill.ui.pay.BillState
import com.example.quickbill.ui.pay.BillViewModel
import com.example.quickbill.ui.pay.Payment
import com.example.quickbill.util.PaymentUtil
import sqip.CardDetails
import sqip.CardEntryActivityCommand
import sqip.CardNonceBackgroundHandler
import java.io.IOException

class CardEntryBackgroundHandler: CardNonceBackgroundHandler {
    override fun handleEnteredCardInBackground(cardDetails : CardDetails): CardEntryActivityCommand {
        try {
            val payment: Payment? = PaymentUtil.makePayment(cardDetails.nonce)
            Log.d("NETWORK LOG", payment.toString())

            return CardEntryActivityCommand.Finish()
        } catch(exception: IOException) {
            return CardEntryActivityCommand.ShowError("Payment is invalid")
        }
    }
}