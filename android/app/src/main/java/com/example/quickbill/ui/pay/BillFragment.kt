package com.example.quickbill.ui.pay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickbill.api.API
import java.time.temporal.TemporalAmount
import java.util.*
import kotlin.collections.ArrayList

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
                val restaurantName = API.instance.restaurantName

                Column {
                    Text(text = "table #$tableNum at restaurant $restaurantName")
                    Text(text = "Pay $${billViewModel.totalCost}")
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
                }
            }
        }
    }
}

data class Bill(
    val id: String,
    val locationId: String,
    val lineItems: ArrayList<OrderItem>,
    val totalMoney: Money,
    val restaurantName: String
)


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
