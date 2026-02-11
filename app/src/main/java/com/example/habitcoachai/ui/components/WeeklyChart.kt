package com.example.habitcoachai.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.DayOfWeek

@Composable
fun WeeklyBarChartView(stats: Map<DayOfWeek, Int>) {

    val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    val values = days.mapIndexed { index, _ ->
        val day = DayOfWeek.of(index + 1)
        BarEntry(index.toFloat(), (stats[day] ?: 0).toFloat())
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        factory = { context ->

        BarChart(context).apply {

            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false

            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)

            axisRight.isEnabled = false
            axisLeft.textColor = AndroidColor.WHITE
            xAxis.textColor = AndroidColor.WHITE

            axisLeft.axisMaximum = 0f
            axisLeft.granularity = 1f

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(days)
            xAxis.setDrawGridLines(false)

            setFitBars(true)
            animateY(1200)

            val dataSet = BarDataSet(values, "Weekly").apply {
                color = AndroidColor.parseColor("#38BDF8")
                valueTextColor = AndroidColor.WHITE
                valueTextSize = 14f

                highLightAlpha = 0
            }

            data = BarData(dataSet).apply {
                barWidth = 0.8f
            }
        }

    }, update = { chart ->

        val dataSet = BarDataSet(values, "Weekly").apply {
            color = AndroidColor.parseColor("#38BDF8")
            valueTextColor = AndroidColor.WHITE
        }

        chart.data = BarData(dataSet).apply{
            barWidth = 0.8f
        }

        chart.invalidate()
    })
}
