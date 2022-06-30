package com.example.quickbill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quickbill.api.API
import com.example.quickbill.databinding.ActivityMainBinding
import com.example.quickbill.ui.analytics.AnalyticsContent
import com.example.quickbill.ui.pay.Bill
import com.example.quickbill.ui.pay.BillView
import com.example.quickbill.ui.pay.PayContent
import com.example.quickbill.ui.settings.SettingsContent
import com.example.quickbill.ui.theme.QuickBillTheme
import sqip.Card
import sqip.CardDetails
import sqip.CardEntry
import sqip.CardEntry.setCardNonceBackgroundHandler
import sqip.CardEntryActivityResult


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuickBillTheme {
                MainContent()
            }
        }

        val cardHandler = CardEntryBackgroundHandler()
        setCardNonceBackgroundHandler(cardHandler)
    }

    // TODO: move to utils
    fun centsToDisplayedAmnt(amnt: Int): String {
        val dollars = amnt / 100
        val cents = amnt % 100
        if (cents < 10) {
            return "$${dollars}.0${cents}"
        } else {
            return "$${dollars}.${cents}"
        }
    }

    // TODO: move to utils - should really have a separate screen (this is only for demo)
    fun handleShowPaymentSuccessful() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Payment Successful")
        val bill: Bill? = API.instance.bill
        val amountPaid = bill?.totalMoney?.amount!!.toInt()
        alertDialog.setMessage("Paid ${centsToDisplayedAmnt(amountPaid)}!")
        alertDialog.setPositiveButton("Done") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        ) // Ignore the fact that it's deprecated.

        if (requestCode == 51789) {
            CardEntry.handleActivityResult(data, object : sqip.Callback<CardEntryActivityResult> {
                override fun onResult(result: CardEntryActivityResult) {
                    if (result.isSuccess()) {
                        Log.d("NOPE", "---------------------------------------")
                        Log.d("NOPE", "-------------- SUCCESS ----------------")
                        Log.d("NOPE", "---------------------------------------")
                        val cardResult: CardDetails = result.getSuccessValue()
                        val card: Card = cardResult.card
                        val nonce = cardResult.nonce
                        handleShowPaymentSuccessful()
                    } else if (result.isCanceled()) {
                        Log.d("NOPE", "---------------------------------------")
                        Log.d("NOPE", "------------ NOT ALLOWED --------------")
                        Log.d("NOPE", "---------------------------------------")
                    }
                }
            })
        }
    }
}

//todo: add icons
sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val iconId: Int?
) {
    object Analytics : Screen("analytics", R.string.title_analytics, R.drawable.ic_baseline_analytics_24)
    object PayBill :
        Screen("payBill", R.string.title_pay, R.drawable.ic_baseline_qr_code_scanner_24)

    object Settings :
        Screen("settings", R.string.title_settings, R.drawable.ic_baseline_settings_24)

    object BillView : Screen("billView", R.string.title_bill, null)

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainContent() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { NavBar(navController = navController) },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.PayBill.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Analytics.route) {
                AnalyticsContent()
            }
            composable(Screen.PayBill.route) {
                PayContent(navController)
            }
            composable(Screen.Settings.route) {
                SettingsContent()
            }
            composable(Screen.BillView.route) {
                BillView()
            }
        }
    }
}

@Preview
@Composable
fun NavBar(navController: NavController = rememberNavController()) {
    val items = listOf(
        Screen.Analytics,
        Screen.PayBill,
        Screen.Settings
    )
    val icons = listOf(
        Icons.Outlined.ShoppingCart,
        Icons.Outlined.Refresh,
        Icons.Outlined.Settings,
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconId!!),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                label = { Text(stringResource(id = screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

//todo: might not be needed?
@Preview
@Composable
fun Header(navController: NavController = rememberNavController()) {
    QuickBillTheme {
        SmallTopAppBar(
            title = { Text(text = "asd") },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

