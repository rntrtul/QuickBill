package com.example.quickbill

import android.util.Log
import com.example.quickbill.api.API
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.ui.pay.BillState
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("FIREBASE LOG", "onNewToken: $token")
        val userId = FirebaseManager.getAuth().currentUser?.uid
        if (userId != null) {
            API.sendFirebaseToken(userId, token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FIREBASE LOG", "Message data payload: ${remoteMessage.data}")
            BillState.instance.billViewModel?.refreshBill()
        }
    }

    fun getSendToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            val userId = FirebaseManager.getAuth().currentUser?.uid
            Log.d("FIREBASE MESSAGING", "token: $token")
            Log.d("FIREBASE MESSAGING", "userId: $userId")
            if (userId != null) {
                API.sendFirebaseToken(userId, token)
            }

        })
    }


}