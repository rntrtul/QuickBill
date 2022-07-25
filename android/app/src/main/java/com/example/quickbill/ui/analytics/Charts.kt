package com.example.quickbill.ui.analytics

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.background
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
import com.example.quickbill.util.centsToDisplayedAmount
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import kotlin.random.Random


data class ChartInfo(
    val title: String = "Spending per Day",
    val yAxisName: String = "Spending($)",
    val xAxisName: String = "Days of the week",
    val colours: List<Color> = listOf(),
    val draggable: Boolean = true,
    val zoomable: Boolean = true,
)

@Composable
fun <T : View> ChartContainer(
    chartInfo: ChartInfo,
    chartFactory: (Context) -> T,
    chartUpdate: (T) -> Unit = {}
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
                update = chartUpdate,
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

//todo: move to util or own file
class ChartListener : OnChartGestureListener {
    var chart: BarChart? = null
    var onTranslate: (Float, Float) -> Unit = { _, _ -> }

    override fun onChartGestureStart(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {
    }

    override fun onChartGestureEnd(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {
    }

    override fun onChartLongPressed(me: MotionEvent?) {}

    override fun onChartDoubleTapped(me: MotionEvent?) {}

    override fun onChartSingleTapped(me: MotionEvent?) {}

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {
    }

    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}

    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        onTranslate(chart?.lowestVisibleX!!, chart?.highestVisibleX!!)
    }
}

class MoneyValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return centsToDisplayedAmount(value.toInt())
    }
}

@Preview
@Composable
fun BarChart(
    chartInfo: ChartInfo = ChartInfo(),
    xAxisData: List<Float> = (0..10).map { num -> num.toFloat() },
    yAxisData: List<Float> = (0..10).map { Random.nextFloat() * 30 },
    xAxisBarLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    viewRange: ViewRange = ViewRange.WEEK,
    dataLabel: String = "foobar",
    onTranslate: (Float, Float) -> Unit = { _, _ -> }
) {
    val entries = ArrayList<BarEntry>()
    for (i in xAxisData.indices) {
        entries.add(BarEntry(xAxisData[i].toFloat(), yAxisData[i]))
    }
    val dataSet = BarDataSet(entries, dataLabel)
    dataSet.setDrawValues(false)

    QuickBillTheme {
        val defaultColour = MaterialTheme.colorScheme.tertiary.toArgb()

        if (chartInfo.colours.isEmpty()) {
            dataSet.color = defaultColour
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
                chart.setPinchZoom(chartInfo.zoomable)
                chart.isDragEnabled = chartInfo.draggable
                chart.isDoubleTapToZoomEnabled = false
                chart.setVisibleXRangeMaximum(viewRange.barsShown)
                chart.setVisibleXRangeMinimum(viewRange.barsShown)
                chart.moveViewToX(xAxisData.size.toFloat())

                chart.axisRight.isEnabled = false
                chart.axisLeft.setDrawGridLines(false)
                chart.axisLeft.granularity = 1f
                chart.axisLeft.textColor = textColour
                chart.axisLeft.axisLineColor = textColour
                chart.axisLeft.axisMinimum = 0f
                chart.axisLeft.valueFormatter = MoneyValueFormatter()

                chart.xAxis.setDrawGridLines(false)
                chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                chart.xAxis.granularity = 1f
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisBarLabels)
                chart.xAxis.textColor = textColour

                chart.description.isEnabled = false
                chart.legend.isEnabled = false

                val chartListener = ChartListener()
                chartListener.chart = chart
                chartListener.onTranslate = onTranslate
                chart.onChartGestureListener = chartListener

                onTranslate(chart.lowestVisibleX, chart.highestVisibleX)

                chart.invalidate()
                chart
            },
            chartUpdate = { chart ->
                chart.setVisibleXRangeMaximum(viewRange.barsShown)
                chart.setVisibleXRangeMinimum(viewRange.barsShown)
                if (viewRange == ViewRange.WEEK) {
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisBarLabels)
                } else {
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter()
                }

                chart.invalidate()
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
                chart.setTouchEnabled(chartInfo.draggable)

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
fun PieChart(
    title: String = "MacroNutrients",
    data: List<Float> = listOf(30.8f, 18.5f, 26.7f, 24.0f),
    labels: List<String> = listOf("Protein", "Carbs", "Sugar", "Fats"),
    colours: List<Color> = listOf(
        Color(239, 100, 97),
        Color(228, 179, 99),
        Color(62, 120, 178),
        Color(115, 186, 155),
        Color(76, 46, 5),
    )
) {
    val entries = ArrayList<PieEntry>()
    for (i in data.indices) {
        entries.add(PieEntry(data[i], ""))
    }

    val pieDataSet = PieDataSet(entries, "MacroNutrients")
    pieDataSet.colors = colours.map { color -> color.toArgb() }

    val pieData = PieData(pieDataSet)

    QuickBillTheme {
        val backgroundColour = MaterialTheme.colorScheme.background.toArgb()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(160.dp)) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    factory = { context ->
                        val chart = com.github.mikephil.charting.charts.PieChart(context)
                        chart.data = pieData

                        chart.holeRadius = 70.dp.value
                        chart.setHoleColor(backgroundColour)

                        chart.legend.isEnabled = false
                        chart.description.isEnabled = false
                        chart.isRotationEnabled = false
                        chart.data.setDrawValues(false)
                        chart.setUsePercentValues(false)

                        chart.invalidate()
                        chart
                    })
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )
                for (i in data.indices) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(colours[i])
                                .padding(4.dp)
                        )
                        Text(
                            text = "${labels[i]} (${"%.2f".format(data[i])}g)",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
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
