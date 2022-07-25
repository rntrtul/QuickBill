package com.example.quickbill

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.quickbill.databinding.ActivityMainBinding
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.ui.analytics.AnalyticsContent
import com.example.quickbill.ui.pay.BillState
import com.example.quickbill.ui.pay.BillView
import com.example.quickbill.ui.pay.PaymentConfirmationContent
import com.example.quickbill.ui.pay.Order
import com.example.quickbill.ui.pay.PayContent
import com.example.quickbill.ui.qr_code_manager.QRCodeCreatorContent
import com.example.quickbill.ui.settings.SettingsContent
import com.example.quickbill.ui.theme.QuickBillTheme
import com.example.quickbill.util.centsToDisplayedAmount
import com.example.quickbill.ui.pay.*
import com.example.quickbill.util.handleCardEntryResult
import com.example.quickbill.util.isCardEntryRequestCode
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

        // Code needed to be able to show the generated QR code file without doing any unnecessary work.
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        ) // Ignore the fact that it's deprecated.

        if (isCardEntryRequestCode(requestCode)) {
            CardEntry.handleActivityResult(data, object : sqip.Callback<CardEntryActivityResult> {
                override fun onResult(result: CardEntryActivityResult) {
                    Log.d("NETWORK LOG", "Card Entry Result: $result")
                    setContent {
                        handleCardEntryResult( result)
                    }
                }
            })
        }
    }
}

//todo: add filled vs outline icons
sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val filledIconId: Int?,
    @DrawableRes val outlinedIconId: Int?,
    val iconContentDescription: String?
) {
    object Analytics :
        Screen(
            "analytics",
            R.string.title_analytics,
            R.drawable.ic_baseline_analytics_24,
            R.drawable.ic_outline_analytics_24,
            iconContentDescription = "Analytics chart icon"
        )

    object PayBill :
        Screen(
            "payBill",
            R.string.title_pay,
            R.drawable.ic_baseline_qr_code_scanner_24,
            R.drawable.ic_outline_qr_code_24,
            iconContentDescription = "QR Code scan icon"
        )

    object Settings :
        Screen(
            "settings",
            R.string.title_settings,
            R.drawable.ic_baseline_settings_24,
            R.drawable.ic_outline_settings_24,
            iconContentDescription = "Settings gear icon"
        )

    object BillView : Screen("billView", R.string.title_bill, null, null, null)
    object QRCodeCreatorView : Screen("qrCodeCreatorView", R.string.qr_code_creator, null, null, null)
    object PaymentConfirmation : Screen("paymentConfirmation", R.string.title_bill, null, null, null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(paymentDone: Boolean = false) {
    val navController = rememberNavController()
    var startDestination = Screen.PayBill.route
    if (paymentDone) {
        startDestination = Screen.PaymentConfirmation.route
    }
    Scaffold(
        bottomBar = { NavBar(navController = navController) },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Analytics.route) {
                AnalyticsContent()
            }
            composable(Screen.PayBill.route) {
                PayContent(navController)
            }
            composable(Screen.Settings.route) {
                SettingsContent(navController)
            }
            composable(Screen.BillView.route) {
                BillView()
            }
            composable(Screen.QRCodeCreatorView.route) {
                QRCodeCreatorContent(navController)
            }
            composable(Screen.PaymentConfirmation.route) {
                PaymentConfirmationContent(navController)
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
    QuickBillTheme {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach() { screen ->
                val selected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (selected) screen.filledIconId!! else screen.outlinedIconId!!),
                            contentDescription = screen.iconContentDescription,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = { Text(stringResource(id = screen.resourceId)) },
                    selected = selected,
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

fun Context.logOut() {
    when (this) {
        is AppCompatActivity -> {
            FirebaseManager.getAuth().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        };
        is ContextWrapper -> baseContext.logOut()
        else -> null
    }
}

