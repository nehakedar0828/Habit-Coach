package com.example.habitcoachai.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun WeeklyLineChartView(stats: Map<String, List<Int>>) {

    val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    val colors = listOf(
        "#38BDF8", "#22C55E", "#F59E0B",
        "#A78BFA", "#F472B6", "#FB7185"
    )

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),

        factory = { context ->

            LineChart(context).apply {

                setTouchEnabled(false)
                description.isEnabled = false
                legend.isEnabled = true      // show habit names
                setDrawGridBackground(false)

                axisRight.isEnabled = false

                axisLeft.apply {
                    axisMinimum = 0f
                    axisMaximum = 1.2f   // habits are 0 or 1
                    granularity = 1f
                    textColor = AndroidColor.WHITE
                    setDrawGridLines(false)
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = IndexAxisValueFormatter(days)
                    textColor = AndroidColor.WHITE
                    setDrawGridLines(false)
                }

                val dataSets = stats.entries.mapIndexed { index, entry ->

                    val habitName = entry.key
                    val values = entry.value

                    val lineEntries = values.mapIndexed { i, v ->
                        Entry(i.toFloat(), v.toFloat())
                    }

                    LineDataSet(lineEntries, habitName).apply {
                        color = AndroidColor.parseColor(colors[index % colors.size])
                        setCircleColor(color)
                        lineWidth = 3f
                        circleRadius = 5f
                        setDrawValues(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }
                }

                data = LineData(dataSets)
                animateX(1200)
            }

        },

        update = { chart ->

            val dataSets = stats.entries.mapIndexed { index, entry ->

                val habitName = entry.key
                val values = entry.value

                val lineEntries = values.mapIndexed { i, v ->
                    Entry(i.toFloat(), v.toFloat())
                }

                LineDataSet(lineEntries, habitName).apply {
                    color = AndroidColor.parseColor(colors[index % colors.size])
                    setCircleColor(color)
                    lineWidth = 3f
                    circleRadius = 5f
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }
            }

            chart.data = LineData(dataSets)
            chart.invalidate()
        }
    )
}
