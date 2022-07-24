package com.example.quickbill.ui.pay

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quickbill.Screen
import com.example.quickbill.api.API
import com.example.quickbill.ui.theme.QuickBillTheme
import com.example.quickbill.util.centsToDisplayedAmount
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.lang.Exception

@Preview
@Composable
fun PaymentConfirmationContent(navController: NavController = rememberNavController()) {
    val context = LocalContext.current
    //        val alertDialog = AlertDialog.Builder(this)
//        alertDialog.setTitle("Payment Successful")
//        val order: Order? = API.instance.order
    val amountPaid = BillState.instance.amountToPay
//        alertDialog.setMessage("Paid ${centsToDisplayedAmount(amountPaid)}!")
//        alertDialog.setPositiveButton("Done") { dialog, _ ->
//            dialog.dismiss()
//        }
//        alertDialog.show()

    QuickBillTheme {
        Text(text = "Payment Succesful")
        Text(text = "Paid ${centsToDisplayedAmount(amountPaid)}!")
        Button(onClick = {
            try {navController.navigate(Screen.BillView.route)}
            catch (e: Exception) {
                println(e.message)
            }

        }) {

        }
    }
}
