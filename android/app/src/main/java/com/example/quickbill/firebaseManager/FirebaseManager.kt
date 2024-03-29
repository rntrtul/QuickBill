package com.example.quickbill.firebaseManager

import android.util.Log
import com.example.quickbill.ui.pay.Money
import com.example.quickbill.ui.pay.OrderItem
import com.example.quickbill.ui.pay.Payment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class NutritionInfo(
    val sugar: Double = 0.0,
    val fiber: Double = 0.0,
    val serving_size: Double = 0.0,
    val sodium: Double = 0.0,
    val name: String = "",
    val potassium: Double = 0.0,
    val saturated_fat: Double = 0.0,
    val fat: Double = 0.0,
    val calories: Double = 0.0,
    val cholesterol: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val userId: String = "",
    var date: Date = Date(),
) {
    operator fun plus(other: NutritionInfo): NutritionInfo {
        return NutritionInfo(
            sugar = this.sugar + other.sugar,
            fiber = this.fiber + other.fiber,
            serving_size = this.serving_size + other.serving_size,
            sodium = this.sodium + other.sodium,
            potassium = this.potassium + other.potassium,
            saturated_fat = this.saturated_fat + other.saturated_fat,
            fat = this.fat + other.fat,
            calories = this.calories + other.calories,
            cholesterol = this.cholesterol + other.cholesterol,
            protein = this.protein + other.protein,
            carbohydrates = this.carbohydrates + other.carbohydrates,
        )
    }

    operator fun div(num: Int): NutritionInfo {
        return NutritionInfo(
            sugar = this.sugar / num,
            fiber = this.fiber / num,
            serving_size = this.serving_size / num,
            sodium = this.sodium / num,
            potassium = this.potassium / num,
            saturated_fat = this.saturated_fat / num,
            fat = this.fat / num,
            calories = this.calories / num,
            cholesterol = this.cholesterol / num,
            protein = this.protein / num,
            carbohydrates = this.carbohydrates / num,
        )
    }
}

data class FirebaseOrderItem(
    val orderId: String,
    val cost: Money = Money(0, "CAD"),
    val date: Date = Date()
)


class FirebaseManager {


    // sample: https://api.nal.usda.gov/fdc/v1/foods/search?query=apple&pageSize=2&api_key=redacted
    // https://api.calorieninjas.com/v1/nutrition?query=

    interface MyCallback {
        fun onCallback(items: List<Map<String, Any>>)
    }

    companion object {
        private var db: FirebaseFirestore? = null
        private var TAG = "FirebaseManager"
        private val baseURL = "https://api.calorieninjas.com/v1/"
        private var api_key = "RGnaYobkRoru061sUEV0cg==VR5GXMOyhcDU5kD7"
        private var mOrderItems: ArrayList<FirebaseOrderItem> = ArrayList()
        private var mNutritionItems: ArrayList<Map<String, Any>> = ArrayList()
        private var orderDocumentSnapshot: DocumentSnapshot? = null
        private var nutritionDocumentSnapshot: DocumentSnapshot? = null
        private var auth: FirebaseAuth = Firebase.auth

        // to reference currently signed user - auth.currentUser

        // Operational
        fun addItemToOrderItems(item: Map<String, Any>, money: Money? = null) {
            var fb_item: FirebaseOrderItem = FirebaseOrderItem(
                orderId = item.get("orderId").toString(),
            )

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
                var items = arrayListOf<Map<String, Any>>()
                for (document: QueryDocumentSnapshot in task.result) {
                    var item: Map<String, Any>
//                    Log.d(TAG, "document is {${document.getData()}")
                    item = document.getData()
                    if (collectionName == "testFoodOrders") {
                        this.addItemToOrderItems(item)
                        items.add(item)
                    } else if (collectionName == "testNutrition") {
                        this.mNutritionItems.add(item)
                        items.add(item)
                    }
                }
                myCallback.onCallback(items)
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

        // TODO : make a separate listener that listens for changes. Return the arraylist only
        // Operational
        fun getData(collectionName: String, myCallback: MyCallback, foodName: String? = null) {

            var snapshot: DocumentSnapshot? = null
            if (collectionName == "testFoodOrders") {
                Log.d(TAG, collectionName)
                snapshot = this.orderDocumentSnapshot
            } else if (collectionName == "testNutrition") {
//                assert((foodName != "None"))
                snapshot = this.nutritionDocumentSnapshot
            }
//            Log.d(TAG,"Passed the snapshot section")

            if (snapshot != null) {
                db!!.collection(collectionName)
                    .whereEqualTo(
                        "userId",
                        auth.getCurrentUser()?.getUid().toString()
                    )
                    .startAfter(snapshot)
                    .get()
                    .addOnCompleteListener { task ->
                        parseTask(collectionName, task, myCallback)
                    }
            } else {
                db!!.collection(collectionName)
                    .whereEqualTo(
                        "userId",
                        auth.getCurrentUser()?.getUid().toString()
                    )
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
            val query_url = baseURL + "nutrition?query=" + name;
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
            }

            return null
        }


        fun addOrderToFirebase(infoDeser: Payment): Boolean {
//            var infoDeser = Gson().fromJson(info.body.string(), Payment::class.java)
            var lineItems = infoDeser.lineItems
            var foodNames = ArrayList<Pair<String, String>>()
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
//            val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
//            order.put("date", LocalDate.parse(infoDeser.createdAt, DateTimeFormatter.ISO_INSTANT))
            order.put("date", infoDeser.createdAt)
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
                    } catch (e: Exception) {
                        foodItem.put("foodVariantName", "")
                    }
                    var nutritionInfo: NutritionInfo? = getNutrition(item.name.lowercase())
                    // TODO: instead of using item.name, use item
                    foodItem.put("calories", nutritionInfo?.calories.toString())
                    foodItem.put("serving_size", nutritionInfo?.serving_size.toString())
                    foodItem.put("fat", nutritionInfo?.fat.toString())
                    foodItem.put("saturated_fat", nutritionInfo?.saturated_fat.toString())
                    foodItem.put("carbohydrates", nutritionInfo?.carbohydrates.toString())
                    foodItem.put("protein", nutritionInfo?.protein.toString())
                    foodItem.put("sodium", nutritionInfo?.sodium.toString())
                    foodItem.put("sugar", nutritionInfo?.sugar.toString())
                    foodItem.put("fiber", nutritionInfo?.fiber.toString())
                    foodItem.put("cholesterol", nutritionInfo?.cholesterol.toString())

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
            return failed == 0
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