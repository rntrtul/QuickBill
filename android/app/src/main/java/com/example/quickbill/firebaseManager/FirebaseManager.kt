package com.example.quickbill.firebaseManager

import com.example.quickbill.MainActivity
import com.example.quickbill.api.API
import com.example.quickbill.ui.pay.Payment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Response

class FirebaseManager {

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

    fun addOrderToFirebase(order: Response): Boolean {
        return true
    }

}