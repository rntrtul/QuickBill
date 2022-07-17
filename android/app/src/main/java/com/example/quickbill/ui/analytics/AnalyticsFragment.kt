package com.example.quickbill.ui.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickbill.ui.theme.QuickBillTheme

class AnalyticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AnalyticsContent()
            }
        }
    }
}

@Preview
@Composable
fun AnalyticsContent() {
    // May want to use a recycler view if we show a table
    val analyticsViewModel: AnalyticsViewModel = viewModel()
    var selectedTabIndex by remember { mutableStateOf(0) }

    QuickBillTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                analyticsViewModel.analyticCategories.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> SpendingTab()
                1 -> NutritionTab()
            }
        }
    }
}

@Preview
@Composable
fun SpendingTab() {
//    get all info here
    Column {
        BarChart()
        BarChart(
            ChartInfo(
                title = "Calories Consumed",
                yAxisName = "Calories (Kcal)",
                colours = listOf(
                    Color.Cyan, Color.Gray, Color.Green, Color.Yellow
                )
            ),
            yAxisData = listOf(4f, 3f, 2f, 7f, 6f, 1f, 5f)
        )
    }
}

@Preview
@Composable
fun NutritionTab() {
    LineChart()
}



