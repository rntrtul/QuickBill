package com.example.quickbill.ui.settings

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quickbill.MainActivity
import com.example.quickbill.Screen
import com.example.quickbill.logOut
import com.example.quickbill.ui.theme.QuickBillTheme

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsContent()
            }
        }
    }


}


@Composable
fun SettingsContent(navController: NavController = rememberNavController()) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val text by settingsViewModel.text.observeAsState()
    val context = LocalContext.current

    QuickBillTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            androidx.compose.material3.Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = { navController.navigate(Screen.QRCodeCreatorView.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            ) {
                androidx.compose.material3.Text(
                    text = "Generate my QR codes (for restaurant owners)",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            androidx.compose.material3.Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = {
                    context.logOut()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            ) {
                Text(
                    text = "Log out",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}



