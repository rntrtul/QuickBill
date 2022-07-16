package com.example.quickbill.ui.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickbill.ui.theme.QuickBillTheme
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

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
            Chart()
            Chart(
                title = "Calories Consumed",
                yAxisName = "Calories (Kcal)",
                xAxisData = listOf(4f, 3f, 2f, 7f, 6f, 1f, 5f)
            )
        }
    }
}

@Preview
@Composable
fun Chart(
    title: String = "Spending per Day",
    yAxisName: String = "Spending ($)",
    xAxisName: String = "Days of the week",
    xAxisData: List<Float> = listOf(1f, 2f, 3f, 4f, 5f, 6f, 7f)
) {
    // todo: figure out proper mapping
    val daysOfWeek = listOf("Tmp", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val entries = ArrayList<BarEntry>()
    for (i in xAxisData) {
        entries.add(BarEntry(i, i))
    }
    val dataSet = BarDataSet(entries, "foobar")
    dataSet.setDrawValues(false)
    val lineData = BarData(dataSet)

    QuickBillTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title, modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = yAxisName, modifier = Modifier
                        .vertical()
                        .rotate(-90f)
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )

                AndroidView(
                    factory = { context ->
                        val chart = BarChart(context)
                        chart.data = lineData

                        chart.axisRight.isEnabled = false
                        chart.axisLeft.setDrawGridLines(false)
                        chart.axisLeft.granularity = 1f
                        chart.axisLeft.axisLineWidth = 2f

                        chart.xAxis.setDrawGridLines(false)
                        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                        chart.xAxis.granularity = 1f
                        chart.xAxis.valueFormatter = IndexAxisValueFormatter(daysOfWeek)

                        chart.description.isEnabled = false
                        chart.legend.isEnabled = false

                        chart.invalidate()
                        chart
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                )
            }
            Text(
                text = xAxisName,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// https://stackoverflow.com/questions/70057396/how-to-show-vertical-text-with-proper-size-layout-in-jetpack-compose
fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
