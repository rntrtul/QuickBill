package com.example.quickbill.ui.pay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                Text(text = "table #$tableNumber at $locationId")
                Box(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        for (i in 1..5) {
                            BillItem()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BillItem(name: String = "apple", cost: Double = 3.25) {
    val isChecked = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = { isChecked.value = !isChecked.value })
        Text(text = name)
        Text(text = "$$cost")

    }

}