package com.example.quickbill.ui.pay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import sqip.*
import sqip.CardEntry.DEFAULT_CARD_ENTRY_REQUEST_CODE
import com.example.quickbill.api.API
import java.time.temporal.TemporalAmount
import java.util.*


/**
 * A fragment representing a list of Items.
 */
class BillFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val billViewModel: BillViewModel = viewModel()
                val tableNum = API.instance.tableNum
                val locationId = API.instance.locationId

                Column {
                    Text(text = "table #$tableNum at restaurant $locationId")
                    Text(text = "Pay $${billViewModel.totalCost}")
                    Button(onClick = {
                        API.instance.amountToPay = billViewModel.totalCost
                        CardEntry.startCardEntryActivity( requireActivity(), true,
                            DEFAULT_CARD_ENTRY_REQUEST_CODE)
                    }) {
                        Text(text = "Pay now")
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        BillList(
                            billViewModel.items,
                            onSelectItem = { item, selected ->
                                billViewModel.itemSelected(
                                    item,
                                    selected
                                )
                            })
                    }
                }
            }
        }
    }
}

data class Bill(
    val id: String,
    val locationId: String,
    val lineItems: ArrayList<OrderItem>,
    val totalMoney: Money
)

//todo: change amount to int every
data class Money(
    val amount: String,
    val currency: String
)

data class OrderItem(
    val name: String,
    val quantity: String,
    val variationName: String,
    val totalMoney: Money,
    val alreadyPayed: Boolean = false,
    var selected: Boolean = false
)

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

@Composable
fun BillItem(
    item: OrderItem,
    onSelectItem: (OrderItem, Boolean) -> Unit
) {
    val textColor: Color = if (item.alreadyPayed) Color.LightGray else Color.Black

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
                fontSize = 16.sp,
            )
        }
        Text(
            text = "$${item.totalMoney.amount}",
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp),
        )
    }
}
