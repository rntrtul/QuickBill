package com.example.quickbill

import sqip.CardDetails
import sqip.CardEntryActivityCommand
import sqip.CardNonceBackgroundHandler
import java.io.IOException

class CardEntryBackgroundHandler: CardNonceBackgroundHandler {
    override fun handleEnteredCardInBackground(cardDetails : CardDetails): CardEntryActivityCommand {
        try {
            return CardEntryActivityCommand.Finish()
//            response = sendPaymentRequest(cardDetails.nonce)
//            if (response.isSuccessful()) {
//                return CardEntryActivityCommand.Finish()
//            } else {
//                return CardEntryActivityCommand.ShowError(response.errorMessage)
//            }
        } catch(exception: IOException) {
            return CardEntryActivityCommand.ShowError("ERROR")
        }
    }
}