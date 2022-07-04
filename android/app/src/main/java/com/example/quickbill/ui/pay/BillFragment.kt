package com.example.quickbill.ui.pay

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickbill.api.API
import com.example.quickbill.ui.theme.QuickBillTheme
import com.example.quickbill.util.centsToDisplayedAmount
import com.example.quickbill.util.getActivity
import sqip.CardEntry
import sqip.CardEntry.DEFAULT_CARD_ENTRY_REQUEST_CODE
import java.util.*
import kotlin.collections.ArrayList
@Preview
@Composable
fun BillView() {
    val tableNum = API.instance.tableNum!!
    val restaurantName: String = API.instance.restaurantName!!

    val billViewModel: BillViewModel = viewModel()
    val context = LocalContext.current

    QuickBillTheme {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, top = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = restaurantName,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = "Table #$tableNum",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
            ) {
                billViewModel.items?.let {
                    BillList(
                        it,
                        onSelectItem = { item, selected ->
                            billViewModel.itemSelected(
                                item,
                                selected
                            )
                        })
                }
            }
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp),
                text = "Total: ${centsToDisplayedAmount(billViewModel.billTotal())}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp, top = 16.dp),
                enabled = billViewModel.totalCost != 0,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = {
                    API.instance.amountToPay = billViewModel.totalCost
                    CardEntry.startCardEntryActivity(
                        context.getActivity()!!, true,
                        DEFAULT_CARD_ENTRY_REQUEST_CODE
                    )
                }) {
                Text(
                    text = "Pay ${centsToDisplayedAmount(billViewModel.totalCost)}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun BillList(
    list: List<OrderItem>, onSelectItem: (OrderItem, Boolean) -> Unit
) {
    Column(
        Modifier.verticalScroll(
            rememberScrollState()
        )
    ) {
        for (item in list) {
            BillItem(item, onSelectItem)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BillItem(
    item: OrderItem = OrderItem("Sushi", "2", "", Money(2005, "CAD")),
    onSelectItem: (OrderItem, Boolean) -> Unit = { _, _ -> }
) {
    val textColor: Color =
        if (item.alreadyPayed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground

    QuickBillTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp, bottom = 4.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = item.selected,
                    onCheckedChange = { onSelectItem(item, it) },
                    enabled = !(item.alreadyPayed)
                )
                Text(
                    text = item.name,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Text(
                text = centsToDisplayedAmount(item.totalMoney.amount),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )

        }
    }
}
