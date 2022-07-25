package com.example.quickbill.firebaseManager

import android.util.Log
import com.example.quickbill.ui.pay.Money
import com.example.quickbill.ui.pay.OrderItem
import com.example.quickbill.ui.pay.Payment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
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
    val userId: String,
    var date: Date,
    var orderId: String
)

data class FirebaseOrderItem(
    val orderId: String,
    val cost: Money?,
    val date: String
)


class FirebaseManager {


    // sample: https://api.nal.usda.gov/fdc/v1/foods/search?query=apple&pageSize=2&api_key=redacted
    // https://api.calorieninjas.com/v1/nutrition?query=

    interface MyCallback {
        fun onCallback(item: Map<String,Any>)
    }

    companion object {
        private var db: FirebaseFirestore? = null
        private var TAG = "FirebaseManager"
        private val baseURL = "https://api.calorieninjas.com/v1/"
        private var api_key = "RGnaYobkRoru061sUEV0cg==VR5GXMOyhcDU5kD7"
        private var mOrderItems: ArrayList<FirebaseOrderItem> = ArrayList()
        private var mNutritionItems: ArrayList<Map<String,Any>> = ArrayList()
        private var orderDocumentSnapshot: DocumentSnapshot? = null
        private var nutritionDocumentSnapshot: DocumentSnapshot? = null
        private var auth: FirebaseAuth = Firebase.auth

        // to reference currently signed user - auth.currentUser

        // Operational
        fun addItemToOrderItems(item: Map<String, Any>, money: Money? = null) {
            var fb_item: FirebaseOrderItem = FirebaseOrderItem(orderId = item.get("orderId").toString()
                , cost = money
                , date=item.get("date").toString())
//            , costCAD = Money(amount=item.get("costCAD").toString().toInt(), "CAD")
//            , date = Date(item.get("date").toString().toLong()))
            this.mOrderItems.add(fb_item)
//            Log.d(TAG, "Added item to mOrderItems")
        }

        // Operational
        fun addItemToNutritionItems(item: Map<String, Any>) {
            this.mNutritionItems.add(item)
        }

        // Operational
        fun parseTask(collectionName: String, task: Task<QuerySnapshot>, myCallback: MyCallback) {
//            if (task.isSuccessful) {
//                for (document in task.result) {
//                    Log.d(TAG, document.id + " => " + document.data)
//                }
//            } else {
//                Log.w(TAG, "Error getting documents.", task.exception)
//            }
            if (task.isSuccessful) {
                for (document: QueryDocumentSnapshot in task.result) {
                    var item: Map<String, Any>
//                    Log.d(TAG, "document is {${document.getData()}")
                    item = document.getData()
                    if (collectionName == "testFoodOrders") {
                        this.addItemToOrderItems(item)
                        myCallback.onCallback(item)
                    } else if (collectionName == "testNutrition") {
                        this.mNutritionItems.add(item)
                        myCallback.onCallback(item)
                    }
                }
                if (!task.getResult().isEmpty) {
                    if (collectionName == "testFoodOrders") {
                        this.orderDocumentSnapshot = task.getResult().getDocuments()
                            .get(task.getResult().size() - 1) // references last queried doc
                    } else if (collectionName == "testNutrition") {
                        this.nutritionDocumentSnapshot = task.getResult().getDocuments()
                            .get(task.getResult().size() - 1) // references last queried doc
                    }
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.exception)
            }
        }

        // Operational
        fun getData(collectionName: String, myCallback: MyCallback, foodName: String? = null) {

            var snapshot: DocumentSnapshot? = null
            if (collectionName == "testFoodOrders") {
                Log.d(TAG, collectionName)
                snapshot = this.orderDocumentSnapshot
            } else if (collectionName == "testNutrition"){
//                assert((foodName != "None"))
                snapshot = this.nutritionDocumentSnapshot
            }
//            Log.d(TAG,"Passed the snapshot section")

            if (snapshot != null) {
                db!!.collection(collectionName)
                    .whereEqualTo("userId", "0") // TODO: auth.getCurrentUser()?.getUid().toString()
                    .startAfter(snapshot)
                    .get()
                    .addOnCompleteListener { task ->
                        parseTask(collectionName, task, myCallback)
                    }
            } else {
                db!!.collection(collectionName)
                    .whereEqualTo("userId", "0") // TODO: auth.getCurrentUser()?.getUid().toString()
                    .get()
                    .addOnCompleteListener { task ->
                        parseTask(collectionName, task, myCallback)
                    }
            }
        }


        // Operational
        fun getNutrition(name: String): NutritionInfo? {
//        fun getNutrition(item: OrderItem): NutritionInfo? {
            // May also add variant name
//            val query_url = baseURL + "nutrition?query="+item.name;
            val query_url = baseURL + "nutrition?query="+name;
            val client = OkHttpClient()
            var nutritionInfo: NutritionInfo
            var responseBody: String = ""
            val job = GlobalScope.launch(Dispatchers.IO) {
                var response: Response? = null
                try {
                    val request: Request = Request.Builder()
                        .url(query_url)
                        .addHeader("X-Api-Key", api_key)
                        .get()
                        .build()
                    response = client.newCall(request).execute()

                    // Assuming one object
                    var arrayRes: JSONObject = JSONObject(response.body.string())
                    responseBody = arrayRes.getJSONArray("items").getJSONObject(0).toString()
                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Response again: ${responseBody}")

                } catch (e: java.lang.Exception) {
                    Log.d(TAG, "$e")
                    Log.d(TAG, "Error executing nutrition info query")
                    response = null
                    responseBody = ""
                }
            }
            runBlocking {
                job.join() // wait until child coroutine completes
            }

            if (responseBody != "") {
                Log.d(TAG, "Response body again: ${responseBody}")
                nutritionInfo = Gson().fromJson(
                    responseBody,
                    NutritionInfo::class.java
                )
                Log.d(TAG, nutritionInfo.toString())
                return nutritionInfo
            }

            return null
        }



        fun addOrderToFirebase(infoDeser: Payment): Boolean {
//            var infoDeser = Gson().fromJson(info.body.string(), Payment::class.java)
            var lineItems = infoDeser.lineItems
            var foodNames = ArrayList<Pair<String,String>>()
            var paidAmounts = ArrayList<Money>()
            var failed = 1

            // Add to the orders collection (tracks mainly spending)
            val order: HashMap<String, Any> = HashMap()
            order.put("userId", auth.getCurrentUser()?.getUid().toString())
            order.put("orderId", infoDeser.id.toString())
//            try {
//                order.put("lineItems", lineItems.toArray())
//            } catch (e: Exception) {
//                order.put("lineItems", listOf(String))
//            }
            order.put("date", infoDeser.date)
            order.put("cost", infoDeser.totalMoney)
            Log.d(TAG, order.toString())
            // Add order with a generated ID
            db!!.collection("testFoodOrders")
                .add(order)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        TAG,
                        "Order added with ID: " + documentReference.id
                    )
                    addItemToOrderItems(order, infoDeser.totalMoney)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    failed = 1
                }

            if (lineItems != null && !lineItems.isEmpty()) {
                for (item: OrderItem in lineItems) {
//            foodNames.add(Pair(item.name,item.variationName))
//            paidAmounts.add(item.totalMoney)
                    // Add to the nutrition collection
                    val foodItem: HashMap<String, Any> = HashMap()
                    foodItem.put("userId", auth.getCurrentUser()?.getUid().toString())
                    foodItem.put("orderId", 0) //TODO: inforDeser.id
                    foodItem.put("date", infoDeser.date)
                    foodItem.put("foodName", item.name.lowercase())
                    try {
                        foodItem.put("foodVariantName", item.variationName.lowercase())
                    } catch(e: Exception) {
                        foodItem.put("foodVariantName", "")
                    }
                    var nutritionInfo: NutritionInfo? = getNutrition(item.name.lowercase())
                    // TODO: instead of using item.name, use item
                    foodItem.put("calories", nutritionInfo?.calories.toString())
                    foodItem.put("serving_size", nutritionInfo?.serving_size_g.toString())
                    foodItem.put("fat", nutritionInfo?.fat_total_g.toString())
                    foodItem.put("saturated_fat", nutritionInfo?.fat_saturated_g.toString())
                    foodItem.put("carbohydrates", nutritionInfo?.carbohydrates_total_g.toString())
                    foodItem.put("protein", nutritionInfo?.protein_g.toString())
                    foodItem.put("sodium", nutritionInfo?.sodium_mg.toString())
                    foodItem.put("sugar", nutritionInfo?.sugar_g.toString())
                    foodItem.put("fiber", nutritionInfo?.fiber_g.toString())
                    foodItem.put("cholesterol", nutritionInfo?.cholesterol_mg.toString())

                    db!!.collection("testNutrition")
                        .add(foodItem)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                TAG,
                                "Food item added with ID: " + documentReference.id
                            )
                            addItemToNutritionItems(foodItem)
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                            failed = 1
                        }
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
//            Log.d(FirebaseManager.TAG, "${FirebaseManager.mOrderItems.size}") //true = 0
//            Log.d(FirebaseManager.TAG, "${FirebaseManager.orderDocumentSnapshot == null}") // true

//            var res: NutritionInfo? = FirebaseManager.getNutrition("hotdog")
//            print(res)

//            FirebaseManager.getData("testFoodOrders", object : MyCallback {
//                override fun onCallback(item: Map<String,Any>) {
//                    Log.d(TAG, "${item.toString()}")
//                }
//            })
//            Log.d(TAG, "mOrderItems are: ${this.mOrderItems}")

        }
    }

}