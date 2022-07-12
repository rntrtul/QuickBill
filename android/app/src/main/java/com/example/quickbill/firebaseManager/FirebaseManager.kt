package com.example.quickbill.firebaseManager

import android.util.Log
import com.example.quickbill.MainActivity
import com.example.quickbill.ui.pay.Money
import com.example.quickbill.ui.pay.OrderItem
import com.example.quickbill.ui.pay.Payment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class NutritionInfo(
    val sugar_g: Double,
    val fiber_g: Double,
    val serving_size_g: Double,
    val sodium_mg: Double,
    val name: String,
    val potassium_g: Double,
    val fat_saturated_g: Double,
    val fat_total_g: Double,
    val calories: Double,
    val cholesterol_mg: Double,
    val protein_g: Double,
    val carbohydrates_total_g: Double,
    var date: Date,
)

class FirebaseManager {

    private var TAG = "FirebaseManaer"
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var db: FirebaseFirestore? = null

    // sample: https://api.nal.usda.gov/fdc/v1/foods/search?query=apple&pageSize=2&api_key=redacted
    // https://api.calorieninjas.com/v1/nutrition?query=
    private val baseURL = "https://api.calorieninjas.com/v1/"
    private var api_key = "redacted"

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

    fun getNutrition(item: OrderItem): NutritionInfo? {
        // May also add variant name
        val query_url = baseURL + "nutrition?query="+item.name;
        var response: Response? = null
        val client = OkHttpClient()
        try {
            val request: Request = Request.Builder()
                .url(query_url)
                .addHeader("X-Api-Key", api_key)
                .get()
                .build()

            response = client.newCall(request).execute()

            Log.d(TAG, "Response: $response")
            var nutritionInfo: NutritionInfo? = null

            if (response.isSuccessful) {
                Log.d(TAG, "Response successful!!")
                nutritionInfo = Gson().fromJson(response.body.string(), NutritionInfo::class.java)
                return nutritionInfo
            }
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "$e")
            Log.d(TAG, "Error executing nutrition info query")
        }
        return null
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

            // Add to the orders collection (tracks mainly spending)
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

            // Add to the nutrition collection
            val foodItem: HashMap<String, Any> = HashMap()
            foodItem.put("userId",0)
            foodItem.put("date", infoDeser.date)
            foodItem.put("foodName", item.name)
            try {
                foodItem.put("foodVariantName", item.variationName)
            } catch(e: Exception) {
                foodItem.put("foodVariantName", "")
            }
//            foodItem.put("calories", getNutrition(item))

            db!!.collection("testNutrition")
                .add(foodItem)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        TAG,
                        "Food item added with ID: " + documentReference.id
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