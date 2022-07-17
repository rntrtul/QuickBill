package com.example.quickbill.ui.analytics

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.quickbill.ui.theme.QuickBillTheme
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

data class ChartInfo(
    val title: String = "Spending per Day",
    val yAxisName: String = "Spending($)",
    val xAxisName: String = "Days of the week",
    val colours: List<Color> = listOf(),
    val interact: Boolean = false,
)

@Composable
fun <T : View> ChartContainer(
    chartInfo: ChartInfo,
    chartFactory: (Context) -> T
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = chartInfo.title,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = chartInfo.yAxisName,
                modifier = Modifier
                    .vertical()
                    .rotate(-90f)
                    .padding(4.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall
            )

            AndroidView(
                factory = chartFactory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            )
        }
        Text(
            text = chartInfo.xAxisName,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview
@Composable
fun BarChart(
    chartInfo: ChartInfo = ChartInfo(),
    xAxisData: List<Float> = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f),
    yAxisData: List<Float> = listOf(1f, 2f, 3f, 4f, 5f, 6f, 7f),
    xAxisBarLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    dataLabel: String = "foobar"
) {
    val entries = ArrayList<BarEntry>()
    for (i in xAxisData.indices) {
        entries.add(BarEntry(xAxisData[i], yAxisData[i]))
    }
    val dataSet = BarDataSet(entries, dataLabel)
    dataSet.setDrawValues(false)

    QuickBillTheme {
        if (chartInfo.colours.isEmpty()) {
            dataSet.color = MaterialTheme.colorScheme.tertiary.toArgb()
        } else {
            dataSet.colors = chartInfo.colours.map { color -> color.toArgb() }
        }
        val barData = BarData(dataSet)

        val textColour = MaterialTheme.colorScheme.onBackground.toArgb()

        ChartContainer(
            chartInfo = chartInfo,
            chartFactory = { context ->
                val chart = com.github.mikephil.charting.charts.BarChart(context)
                chart.data = barData
                chart.setTouchEnabled(chartInfo.interact)

                chart.axisRight.isEnabled = false
                chart.axisLeft.setDrawGridLines(false)
                chart.axisLeft.granularity = 1f
                chart.axisLeft.textColor = textColour
                chart.axisLeft.axisLineColor = textColour

                chart.xAxis.setDrawGridLines(false)
                chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                chart.xAxis.granularity = 1f
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisBarLabels)
                chart.xAxis.textColor = textColour

                chart.description.isEnabled = false
                chart.legend.isEnabled = false

                chart.invalidate()
                chart
            }
        )
    }
}

@Preview
@Composable
fun LineChart(
    chartInfo: ChartInfo = ChartInfo(),
    xAxisData: List<Float> = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f),
    yAxisData: List<Float> = listOf(1f, 2f, 3f, 4f, 5f, 6f, 7f),
    xAxisBarLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    dataLabel: String = "foobar"
) {
    val entries = ArrayList<Entry>()
    for (i in xAxisData.indices) {
        entries.add(Entry(xAxisData[i], yAxisData[i]))
    }
    val dataSet = LineDataSet(entries, dataLabel)
    dataSet.setDrawValues(false)

    QuickBillTheme {
        if (chartInfo.colours.isEmpty()) {
            dataSet.circleColors = listOf(MaterialTheme.colorScheme.tertiary.toArgb())
        } else {
            dataSet.circleColors = chartInfo.colours.map { color -> color.toArgb() }
        }

        val lineData = LineData(dataSet)
        val textColour = MaterialTheme.colorScheme.onBackground.toArgb()
        dataSet.color = textColour

        ChartContainer(
            chartInfo = chartInfo,
            chartFactory = { context ->
                val chart = com.github.mikephil.charting.charts.LineChart(context)
                chart.data = lineData
                chart.setTouchEnabled(chartInfo.interact)

                chart.axisRight.isEnabled = false
                chart.axisLeft.setDrawGridLines(false)
                chart.axisLeft.granularity = 1f
                chart.axisLeft.axisLineWidth = 2f
                chart.axisLeft.axisLineColor = textColour
                chart.axisLeft.textColor = textColour

                chart.xAxis.setDrawGridLines(false)
                chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                chart.xAxis.granularity = 1f
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisBarLabels)
                chart.xAxis.textColor = textColour

                chart.description.isEnabled = false
                chart.legend.isEnabled = false


                chart.invalidate()
                chart
            }
        )

    }
}

@Preview
@Composable
fun PieChart() {

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
