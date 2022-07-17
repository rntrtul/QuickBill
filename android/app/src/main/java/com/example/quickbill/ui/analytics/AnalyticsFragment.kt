package com.example.quickbill.ui.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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

@Composable
fun AnalyticsContent() {
    // May want to use a recycler view if we show a table

    val analyticsViewModel: AnalyticsViewModel = viewModel()
    QuickBillTheme {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            Text(
                text = "Spending Analysis",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            BarChart()
            BarChart(
                ChartInfo(
                    title = "Calories Consumed",
                    yAxisName = "Calories (Kcal)",
                    colours = listOf(
                        Color.Cyan, Color.Gray, Color.Green
                    )
                ),
                yAxisData = listOf(4f, 3f, 2f, 7f, 6f, 1f, 5f)
            )
            LineChart()
        }
    }
}



