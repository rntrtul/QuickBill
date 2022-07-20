package com.example.quickbill.ui.qr_code_manager

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quickbill.R
import com.example.quickbill.logOut
import com.example.quickbill.ui.theme.QuickBillTheme
import android.widget.NumberPicker
import android.widget.Toast
import com.example.quickbill.Screen


@Preview
@Composable
fun QRCodeCreatorContent(navController: NavController = rememberNavController()) {
    val context = LocalContext.current
    val viewModel: QRCodeCreatorViewModel = viewModel()

    var squareLocationId by remember{ mutableStateOf("") }
    var restaurantName by remember{ mutableStateOf("") }
    var tableIndexStartFromStr by remember{ mutableStateOf("") }
    var numTables by remember{ mutableStateOf("") }

    QuickBillTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = squareLocationId,
                onValueChange = { squareLocationId = it },
                label = { androidx.compose.material3.Text("Square Location ID:") }
            )
            TextField(
                value = restaurantName,
                onValueChange = { restaurantName = it },
                label = { androidx.compose.material3.Text("Restaurant Name (Optional):") }
            )
            TextField(
                value = tableIndexStartFromStr,
                onValueChange = { tableIndexStartFromStr = it },
                label = { androidx.compose.material3.Text("Table Numbers Starting At:") }
            )
            TextField(
                value = numTables,
                onValueChange = { numTables = it },
                label = { androidx.compose.material3.Text("# of Tables:") }
            )
            Button(
                onClick = {
                    viewModel.setInput(squareLocationId, restaurantName, tableIndexStartFromStr, numTables)
                    val errorMsg = viewModel.checkForInputError()
                    if (errorMsg == null) { // No errors
                        viewModel.genQrPdf(context)
                        Toast.makeText(context, "Created QR code PDF", Toast.LENGTH_LONG).show()
                        //navController.navigate(Screen.Settings.route)
                    } else {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            ) {
                androidx.compose.material3.Text(
                    text = "GENERATE",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}