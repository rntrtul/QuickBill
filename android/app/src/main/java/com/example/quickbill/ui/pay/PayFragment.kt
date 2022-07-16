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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Preview
@Composable
fun PayContent(navController: NavController = rememberNavController()) {
    val payViewModel: PayViewModel = viewModel()
    val context = LocalContext.current

    // https://stackoverflow.com/a/71840545
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            payViewModel.processQrResult(result)

            if (!payViewModel.scanValid) {
                navController.navigate(Screen.PayBill.route)
            } else {
                if (payViewModel.scanSuccessful) {
                    navController.navigate(Screen.BillView.route)
                } else {
                    Toast.makeText(
                        context,
                        "QR code is invalid. Please contact the restaurant owner.",
                        Toast.LENGTH_LONG
                    ).show()
                    API.instance.invalidateLocationAndTableNum()
                    navController.navigate(Screen.PayBill.route)
                }
            }
        }
    )

    QuickBillTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = { scanLauncher.launch(ScanOptions()) }) {
                Text(
                    text = "SCAN QR CODE",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
