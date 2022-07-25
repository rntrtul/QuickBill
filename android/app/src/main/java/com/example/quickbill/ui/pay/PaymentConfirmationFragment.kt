package com.example.quickbill.ui.pay

import android.app.ListActivity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.quickbill.R
import com.example.quickbill.Screen
import com.example.quickbill.api.API
import com.example.quickbill.ui.theme.QuickBillTheme
import com.example.quickbill.ui.theme.md_theme_dark_tertiaryContainer
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
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Text(
                    text = "Payment Succesful",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(14.dp),
                )
            Icon(
                Icons.Filled.CheckCircle, "",
                tint = md_theme_dark_tertiaryContainer,
                modifier = Modifier
                    .size(100.dp)
                    .padding(14.dp)
            )
            Text(
                    text = "Payment of ${centsToDisplayedAmount(amountPaid)} has been received",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 28.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { navController.navigate(Screen.BillView.route) },
                    modifier = Modifier
                        .padding(top = 14.dp),
                ) {
                    Text(text = "View Bill", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
        }
    }
}
