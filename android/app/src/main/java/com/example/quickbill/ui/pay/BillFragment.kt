package com.example.quickbill.ui.pay

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

@Preview
@Composable
fun BillView(billViewModel: BillViewModel = viewModel()) {
    val tableNum = API.instance.tableNum ?: 9
    val restaurantName: String = API.instance.restaurantName ?: "KRUST"

    QuickBillTheme {
        Column {
            RestaurantInfo(restaurantName, tableNum)

            Box(Modifier.fillMaxWidth()) {
                BillList(
                    list = billViewModel.items,
                    onSelectItem = { item, selected ->
                        billViewModel.itemSelected(
                            item,
                            selected
                        )
                    })
            }
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp),
                text = "Total: ${centsToDisplayedAmount(billViewModel.billTotal())}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Box(modifier = Modifier.align(Alignment.End)) {
                PayBillButton(billViewModel)
            }
        }
    }
}

@Preview
@Composable
fun RestaurantInfo(
    restaurantName: String = "Krust Krab",
    tableNum: Int = 1
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, top = 24.dp)
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
            BillItem(
                itemName = item.name,
                itemCost = item.totalMoney,
                itemSelected = item.selected,
                itemAlreadyPaid = item.alreadyPaid,
                onSelectItem = { checked -> onSelectItem(item, checked) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BillItem(
    itemName: String = "Sushi",
    itemCost: Money = Money(2005, "CAD"),
    itemSelected: Boolean = false,
    itemAlreadyPaid: Boolean = false,
    onSelectItem: (Boolean) -> Unit = {}
) {
//    style = TextStyle(textDecoration = TextDecoration.LineThrough)
    val textColor: Color =
        if (itemAlreadyPaid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground

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
                    checked = itemSelected,
                    onCheckedChange = onSelectItem,
                    enabled = !(itemAlreadyPaid)
                )
                Text(
                    text = itemName,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Text(
                text = centsToDisplayedAmount(itemCost.amount),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun PayBillButton(billViewModel: BillViewModel = viewModel()) {
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .padding(8.dp, top = 16.dp),
        enabled = billViewModel.paymentTotal() != 0,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = {
            API.instance.amountToPay = billViewModel.paymentTotal()
            CardEntry.startCardEntryActivity(
                context.getActivity()!!, true,
                DEFAULT_CARD_ENTRY_REQUEST_CODE
            )
        }) {
        Text(
            text = "Pay ${centsToDisplayedAmount(billViewModel.paymentTotal())}",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
