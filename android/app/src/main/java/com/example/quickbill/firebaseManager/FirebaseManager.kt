package com.example.quickbill.firebaseManager

import android.util.Log
import com.example.quickbill.MainActivity
import com.example.quickbill.ui.pay.Money
import com.example.quickbill.ui.pay.OrderItem
import com.example.quickbill.ui.pay.Payment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.Response


class FirebaseManager {

    private var TAG = "FirebaseManaer"
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var db: FirebaseFirestore? = null

    private object Holder {
        val instance = FirebaseManager()
    }

    companion object {
        val instance: FirebaseManager by lazy { Holder.instance }
    }

    fun initialize(mainActivity: MainActivity) {

        // Obtain the FirebaseAnalytics instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mainActivity);

        //Obtain Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun addOrderToFirebase(info: Response): Boolean {
        var infoDeser = Gson().fromJson(info.body.string(), Payment::class.java)
        var lineItems = infoDeser.lineItems
        var foodNames = ArrayList<Pair<String,String>>()
        var paidAmounts = ArrayList<Money>()
        var failed = 1

        for (item: OrderItem in lineItems) {
//            foodNames.add(Pair(item.name,item.variationName))
//            paidAmounts.add(item.totalMoney)
            val order: HashMap<String, Any> = HashMap()
            order.put("userId", 0)
            order.put("orderId", 0)
            order.put("foodName", item.name)
            try {
                order.put("foodVariantName", item.variationName)
            } catch(e: Exception) {
                order.put("foodVariantName", "")
            }

            // Add order with a generated ID
            db!!.collection("testOrders")
                .add(order)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        TAG,
                        "Order added with ID: " + documentReference.id
                    )
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    failed = 1
                }
        }
        return failed==0
    }

}