package com.example.quickbill.util

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.quickbill.api.API
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.ui.pay.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import sqip.Card
import sqip.CardDetails
import sqip.CardEntryActivityResult
import java.util.*
import kotlin.collections.ArrayList


fun isCardEntryRequestCode(requestCode: Int): Boolean {
    return requestCode == 51789
}


fun getOrderItem(selectedItem: BillItem): JSONObject {
    val item = JSONObject()

    val amount = JSONObject()
    amount.put("amount", selectedItem.amountPaying.amount)
    amount.put("currency", selectedItem.amountPaying.currency)

    try {
        item.put("itemId", selectedItem.order.name)
        item.put("amount", amount)
        item.put("quantity", selectedItem.quantitySelected)
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    return item
}


fun getOrderItems(selectedItems: List<BillItem>): JSONArray {
    val items = JSONArray()

    for(selectedItem in selectedItems) {
        val itemToAdd: JSONObject = getOrderItem(selectedItem)
        items.put(itemToAdd)
    }

    return items
}


fun getUserOrder(selectedItems: List<BillItem>): JSONObject {
    val userOrder = JSONObject()
    val userId: String = FirebaseManager.getAuth().currentUser!!.uid
    val items: JSONArray = getOrderItems(selectedItems)
    val amount: Int = BillState.instance.amountToPay

    try {
        userOrder.put("userId", userId)
        userOrder.put("items", items)
        userOrder.put("amount", amount)
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    return userOrder
}


fun makePayment(nonce: String): Payment? {
    val sourceId: String = nonce
    val orderId = BillState.instance.billResponse!!.order.id
    val idempotencyKey: UUID = UUID.randomUUID() // Generates random UUID
    val amount: Int = BillState.instance.amountToPay

    val selectedItems: List<BillItem> = BillState.instance.billViewModel!!.selectedItems()
    val userOrder: JSONObject = getUserOrder(selectedItems)

    Log.d("NETWORK LOG", "User Order: $userOrder")

    val requestBody = JSONObject()
    try {
        requestBody.put("sourceId", sourceId)
        requestBody.put("orderId", orderId)
        requestBody.put("idempotencyKey", idempotencyKey.toString())
        requestBody.put("amountMoney", amount.toString())
        requestBody.put("userOrder", userOrder)
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    Log.d("NETWORK LOG", "Sending payment request with body: $requestBody")
    return API.sendPaymentRequest(requestBody)
}


fun handleShowPaymentSuccessful(context: Context) {
    val alertDialog = AlertDialog.Builder(context)
    alertDialog.setTitle("Payment Successful")
//    val billResponse: billResponse? = BillState.instance.billResponse
//    val amountPaid = order?.totalMoney?.amount!!.toInt()
//    alertDialog.setMessage("Paid ${centsToDisplayedAmount(amountPaid)}!")
    alertDialog.setPositiveButton("Done") { dialog, _ ->
        dialog.dismiss()
    }
    alertDialog.show()
}


fun handleCardEntryResult(result: CardEntryActivityResult) {
    Log.d("NETWORK LOG", "Card Entry Result: $result")
    if (result.isSuccess()) {
//        val cardResult: CardDetails = result.getSuccessValue()
//        val card: Card = cardResult.card
//        val nonce = cardResult.nonce
        Log.d("NETWORK LOG", "Card Entry Result Success")
        // handleShowPaymentSuccessful()
    } else if (result.isCanceled()) {
        Log.d("NETWORK LOG", "Invalid Card Entered")
        // TODO
        // handleShowPaymentInvalid()
    }
}