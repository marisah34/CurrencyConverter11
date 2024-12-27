package com.myapp.currencyconverter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintLine = Paint().apply {
        color = Color.BLUE
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    private val paintPoint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    // Sample data (replace with dynamic data)
    private var dataPoints: List<Pair<Float, Float>> = emptyList()

    fun setData(points: List<Pair<Float, Float>>) {
        dataPoints = points
        invalidate() // Refresh view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return

        // Calculate scaling factors
        val padding = 50f
        val width = width - padding * 2
        val height = height - padding * 2

        val maxX = dataPoints.maxOf { it.first }
        val maxY = dataPoints.maxOf { it.second }

        // Scale data points to fit in view
        val scaledPoints = dataPoints.map { point ->
            val x = padding + (point.first / maxX) * width
            val y = padding + height - (point.second / maxY) * height
            x to y
        }

        // Draw lines between points
        for (i in 0 until scaledPoints.size - 1) {
            val (x1, y1) = scaledPoints[i]
            val (x2, y2) = scaledPoints[i + 1]
            canvas.drawLine(x1, y1, x2, y2, paintLine)
        }

        // Draw points
        scaledPoints.forEach { (x, y) ->
            canvas.drawCircle(x, y, 10f, paintPoint)
        }

        // Draw labels for each point
        scaledPoints.forEachIndexed { index, (x, y) ->
            val label = "(${dataPoints[index].first}, ${dataPoints[index].second})"
            canvas.drawText(label, x, y - 10f, paintText)
        }
    }
}
