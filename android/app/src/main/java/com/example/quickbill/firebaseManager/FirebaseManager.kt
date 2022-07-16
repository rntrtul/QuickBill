package com.example.quickbill.firebaseManager

import android.util.Log
import com.example.quickbill.MainActivity
import com.example.quickbill.ui.pay.Money
import com.example.quickbill.ui.pay.OrderItem
import com.example.quickbill.ui.pay.Payment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.*

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


    // sample: https://api.nal.usda.gov/fdc/v1/foods/search?query=apple&pageSize=2&api_key=redacted
    // https://api.calorieninjas.com/v1/nutrition?query=


    companion object {
        private var db: FirebaseFirestore? = null
        private var TAG = "FirebaseManager"
        private val baseURL = "https://api.calorieninjas.com/v1/"
        private var api_key = "redacted"
        private var mOrderItems: ArrayList<OrderItem> = ArrayList() // TODO: change type of object from OrderItem to something else
        private var mNutritionItems: ArrayList<NutritionInfo> = ArrayList()
        private var orderDocumentSnapshot: DocumentSnapshot? = null
        private var nutritionDocumentSnapshot: DocumentSnapshot? = null
        private var auth: FirebaseAuth = Firebase.auth

        // to reference currently signed user - auth.currentUser


        // TODO : make a separate listener that listens for changes. Return the arraylist only
        fun getData(collectionName: String) {

            val collectionRef : CollectionReference
            collectionRef = db!!.collection(collectionName)

            var snapshot: DocumentSnapshot? = null
            if (collectionName == "testOrders") {
                snapshot = orderDocumentSnapshot!!
            } else if (collectionName == "testNutrition"){
                snapshot = nutritionDocumentSnapshot!!
            }

            var query: Query

            // Do not want duplicate items
            if (snapshot != null) {
                query = collectionRef
                    .whereEqualTo("user_id", 0) //FirebaseAuth.getInstance().getCurrentUser().getUid()
                //.orderBy("date", Query.Direction.ASCENDING) // Need to add custom index on console
                    .startAfter(snapshot)
            } else {
                query = collectionRef
                    .whereEqualTo("user_id", 0) //FirebaseAuth.getInstance().getCurrentUser().getUid()
                //.orderBy("date", Query.Direction.ASCENDING) // Need to add custom index on console
            }

            query.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot>() {
                @Override
                fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful) {
                        for (document: QueryDocumentSnapshot in task.getResult()) {
                            if (collectionName == "testOrders") {
                                var item: OrderItem
                                item = document.toObject(OrderItem::class.java)
                                mOrderItems.add(item)
                            } else if (collectionName=="testNutrition"){
                                var item: NutritionInfo
                                item = document.toObject(NutritionInfo::class.java)
                                mNutritionItems.add(item)
                            }
                        }
                        if (!task.getResult().isEmpty){
                            if (collectionName == "testOrders") {
                                orderDocumentSnapshot=task.getResult().getDocuments().get(task.getResult().size()-1) // references last queried doc
                            } else if (collectionName=="testNutrition"){
                                nutritionDocumentSnapshot=task.getResult().getDocuments().get(task.getResult().size()-1) // references last queried doc
                            }
                        }
                        // recyclerViewAdapter.notifyDatasetChanged(
                    } else{
                        Log.d(TAG, "Failed to get orders, check logs")
                    }
                }
            })
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
                order.put("userId", 0) // FirebaseAuth.getInstance().getCurrentUser().getUid()
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
                foodItem.put("userId",0) // FirebaseAuth.getInstance().getCurrentUser().getUid()
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

        fun getAuth(): FirebaseAuth {
            return auth
        }



        fun initialize() {
            //Obtain Firestore
            db = FirebaseFirestore.getInstance()
        }
    }

}