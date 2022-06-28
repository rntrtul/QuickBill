package com.example.quickbill

import android.util.Log
import com.example.quickbill.api.API
import sqip.CardDetails
import sqip.CardEntryActivityCommand
import sqip.CardNonceBackgroundHandler
import java.io.IOException

class CardEntryBackgroundHandler: CardNonceBackgroundHandler {
    override fun handleEnteredCardInBackground(cardDetails : CardDetails): CardEntryActivityCommand {
        try {
            val response = API.instance.makePayment(cardDetails.nonce)
            Log.d("NETWORK LOG", "--------------------------")
            Log.d("NETWORK LOG", response)
            Log.d("NETWORK LOG", "--------------------------")
            return CardEntryActivityCommand.Finish()
        } catch(exception: IOException) {
            return CardEntryActivityCommand.ShowError("Payment is invalid")
        }
    }
}