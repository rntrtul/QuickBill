package com.example.quickbill.ui.pay

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickbill.R
import com.example.quickbill.ui.theme.QuickBillTheme
import com.example.quickbill.util.centsToDisplayedAmount
import com.example.quickbill.util.getActivity
import com.example.quickbill.util.parseMoney
import com.example.quickbill.util.validateMoneyAmount
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import sqip.CardEntry
import sqip.CardEntry.DEFAULT_CARD_ENTRY_REQUEST_CODE
import kotlin.math.max
import kotlin.math.min

@Preview
@Composable
fun BillView(billViewModel: BillViewModel = viewModel()) {
    val tableNum = BillState.instance.tableNum!!
    var restaurantName: String? = BillState.instance.restaurantName
    BillState.instance.billViewModel = billViewModel
    if (restaurantName == null) restaurantName = BillState.instance.locationId

    val isRefreshing by billViewModel.isRefreshing.collectAsState()
    val count = billViewModel.counter

    QuickBillTheme {
        Box {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { billViewModel.refreshBill() }
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    RestaurantInfo(restaurantName!!, tableNum)

                    Box(Modifier.fillMaxWidth()) {
                        BillList(
                            list = billViewModel.items,
                            onSelectItem = { item, selected ->
                                billViewModel.itemSelected(item, selected)
                            },
                            onQuantityChange = { item, quantitySelected ->
                                billViewModel.itemQuantityChosen(item, quantitySelected)
                            },
                            onAmountPayingChange = { item, amount ->
                                billViewModel.itemAmountPayingChange(item, amount)
                            }
                        )
                    }
                    Text(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(8.dp, end = 32.dp),
                        text = "Total: ${centsToDisplayedAmount(billViewModel.billTotal())}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Box(modifier = Modifier.align(Alignment.End)) {
                        PayBillButton(billViewModel.paymentTotal)
                    }
                }
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
    QuickBillTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, top = 24.dp),
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
}

@Composable
fun BillList(
    list: List<BillItem>,
    onSelectItem: (BillItem, Boolean) -> Unit,
    onQuantityChange: (BillItem, Int) -> Unit,
    onAmountPayingChange: (BillItem, Int) -> Unit,
) {
    Column {
        for (item in list) {
            LineItem(
                itemName = item.order.name,
                itemCost = item.order.basePriceMoney,
                itemSelected = item.selected,
                itemAmountPaid = item.amountPaid,
                itemAlreadyPaid = item.alreadyPaid,
                itemQuantity = item.order.quantity,
                quantitySelected = item.quantitySelected,
                lineCost = item.order.totalMoney,
                amountPaying = item.amountPaying,
                onSelectItem = { checked -> onSelectItem(item, checked) },
                onQuantityChange = { quantity -> onQuantityChange(item, quantity) },
                onAmountPayingChange = { amount -> onAmountPayingChange(item, amount) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LineItem(
    itemName: String = "Sushi",
    itemCost: Money = Money(2005, "CAD"),
    itemAmountPaid: Money = Money(1200, "CAD"),
    itemSelected: Boolean = false,
    itemAlreadyPaid: Boolean = false,
    itemQuantity: Int = 2,
    quantitySelected: Int = 1,
    lineCost: Money = Money(4010, "CAD"),
    amountPaying: Money = Money(3500, "CAD"),
    onSelectItem: (Boolean) -> Unit = {},
    onQuantityChange: (Int) -> Unit = {},
    onAmountPayingChange: (Int) -> Unit = {}
) {
    var showDetail by remember { mutableStateOf(false) }
    val crossedOut =
        if (itemAlreadyPaid) TextStyle(textDecoration = TextDecoration.LineThrough) else null

    QuickBillTheme {
        Column(modifier = Modifier.padding(bottom = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
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
                        style = MaterialTheme.typography.bodyLarge.merge(crossedOut),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = amountPaying.displayAmount(),
                        style = MaterialTheme.typography.bodyMedium.merge(crossedOut),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Box(modifier = Modifier.width(32.dp)) {
                        if (itemQuantity > 1) {
                            val rotation: Float by animateFloatAsState(if (showDetail) 180f else 0f)

                            IconButton(
                                onClick = { showDetail = !showDetail },
                                modifier = Modifier.rotate(rotation)
                            ) {
                                Icon(Icons.Filled.ArrowDropDown, "show detail for item")
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = showDetail) {
                LineItemDetails(
                    itemQuantity,
                    lineCost,
                    itemCost,
                    amountPaying,
                    quantitySelected,
                    onQuantityChange,
                    onAmountPayingChange
                )
            }
        }
    }
}

@Preview
@Composable
fun LineItemDetails(
    itemQuantity: Int = 2,
    lineCost: Money = Money(4010, "CAD"),
    itemCost: Money = Money(2005, "CAD"),
    amountPaying: Money = Money(3500, "CAD"),
    quantitySelected: Int = 1,
    onQuantityChange: (Int) -> Unit = {},
    onAmountPayingChange: (Int) -> Unit = {}
) {
    // todo: add details on how much is paid for
    var shownText by remember { mutableStateOf(amountPaying.displayAmount()) }
    var isError by rememberSaveable { mutableStateOf(false) }


    val focusManager = LocalFocusManager.current
    QuickBillTheme {
        Column(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 56.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${lineCost.displayAmount()} = $itemQuantity x ${itemCost.displayAmount()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Box(modifier = Modifier.padding(end = 32.dp)) {
                    QuantitySelector(
                        itemQuantity = itemQuantity,
                        quantitySelected = quantitySelected,
                        onQuantityChange = onQuantityChange
                    )
                }
            }
            TextField(
                value = shownText,
                onValueChange = {
                    shownText = it
                    isError = !validateMoneyAmount(shownText, lineCost.amount)
                    if (!isError) {
                        onAmountPayingChange(parseMoney(shownText))
                        Log.d("Input", "$amountPaying")
                    }
                },
                isError = isError,
                label = { Text(text = "Amount Paying") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {
                    if (!isError) {
                        shownText = amountPaying.displayAmount()
                    }
                }),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 32.dp)
                    .width(180.dp)
            )
        }
    }
}

@Preview
@Composable
fun PayBillButton(
    paymentTotal: Int = 100,
    billViewModel: BillViewModel = viewModel()
) {
    val context = LocalContext.current
    QuickBillTheme {
        Button(
            modifier = Modifier
                .padding(8.dp, top = 16.dp),
            enabled = paymentTotal != 0,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = {
                BillState.instance.amountToPay = paymentTotal
                CardEntry.startCardEntryActivity(
                    context.getActivity()!!, true,
                    DEFAULT_CARD_ENTRY_REQUEST_CODE
                )
            }) {
            Text(
                text = "Pay ${centsToDisplayedAmount(paymentTotal)}",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun QuantitySelector(
    itemQuantity: Int = 2,
    quantitySelected: Int = 100,
    onQuantityChange: (Int) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    QuickBillTheme {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = {
                        expanded = false
                        onQuantityChange(max(quantitySelected - 1, 1))
                    },
                    modifier = Modifier.width(22.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_remove_24),
                        contentDescription = "remove item to pay for",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                TextButton(
                    onClick = { expanded = true },
                    modifier = Modifier.width(54.dp)
                ) {
                    Text(text = quantitySelected.toString())
                }
                IconButton(
                    onClick = {
                        expanded = false
                        onQuantityChange(min(quantitySelected + 1, itemQuantity))
                    },
                    modifier = Modifier.width(22.dp)
                ) {
                    Icon(Icons.Filled.Add, "Add item to pay for")
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                for (i in 1..itemQuantity) {
                    DropdownMenuItem(
                        text = { Text(text = "$i") },
                        onClick = {
                            onQuantityChange(i)
                            expanded = false
                        })
                }
            }
        }
    }
}