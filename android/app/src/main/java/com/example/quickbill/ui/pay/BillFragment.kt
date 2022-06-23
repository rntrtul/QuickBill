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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

/**
 * A fragment representing a list of Items.
 */
class BillFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val locationId = requireArguments().get("location_id")
        val tableNumber = requireArguments().get("table_number")


        return ComposeView(requireContext()).apply {
            setContent {
                val orderList = remember { getOrderItems().toMutableList() }
                val totalCost: MutableState<Double> = remember { mutableStateOf(0.0) }

                Column {
                    Text(text = "table #$tableNumber at restaurant $locationId")
                    Text(text = "Pay $${totalCost.value}")
                    Box(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        BillList(
                            orderList
                        ) { item, selected ->
                            if (selected) totalCost.value += item.cost
                            else totalCost.value -= item.cost
                        }
                    }
                }
            }
        }
    }
}

data class OrderItem(
    val name: String,
    val cost: Double,
    val alreadyPayed: Boolean,
)

@Composable
fun BillItem(
    item: OrderItem,
    enabled: Boolean = true,
    onSelectItem: (OrderItem, Boolean) -> Unit
) {
    val isChecked = remember { mutableStateOf(false) }
    val textColor: Color = if (enabled) Color.Black else Color.LightGray

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
                checked = isChecked.value,
                onCheckedChange = {
                    isChecked.value = !isChecked.value
                    onSelectItem(item, isChecked.value)
                },
                enabled = enabled
            )
            Text(
                text = item.name,
                color = textColor,
                fontSize = 16.sp,
            )
        }
        Text(
            text = "$${item.cost}",
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp),
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
            BillItem(item, onSelectItem = onSelectItem)
        }
    }
}

private fun getOrderItems() = List(15) { i ->
    OrderItem("$i", i.toDouble(), true)
}