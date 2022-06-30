package com.example.quickbill.ui.pay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.quickbill.R
import com.example.quickbill.Screen
import com.example.quickbill.api.API
import com.example.quickbill.ui.theme.QuickBillTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class PayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
//                PayContent()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (API.instance.isQrCodeScanned()) {
//            API.instance.callBill() // Need to make call in case bill changes
//            findNavController(this).navigate(R.id.action_navigation_pay_to_billFragment)
//        }
    }
}

@Composable
fun PayContent(navController: NavController) {
    val payViewModel: PayViewModel = viewModel()
    val context = LocalContext.current

    // https://stackoverflow.com/a/71840545
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            payViewModel.processQrResult(result)
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
    )

    QuickBillTheme {
        Column(
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
