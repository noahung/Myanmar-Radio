package com.noahaung.myanmarradio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin

class SoundWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val barWidth = 4f
    private val minSpacing = 2f
    private val amplitude = 80f
    private var phase = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val centerY = height / 2

        val totalBarWidth = barWidth + minSpacing
        val maxBars = (width / totalBarWidth).toInt()
        val barsToDraw = maxBars.coerceAtLeast(10) // Ensure at least 10 bars

        for (i in 0 until barsToDraw) {
            val x = i * totalBarWidth
            if (x > width) break

            val barHeight = amplitude * (sin(phase + i * 0.5f) + 1) / 2
            val top = centerY - barHeight / 2
            val bottom = centerY + barHeight / 2

            canvas.drawRect(x, top, x + barWidth, bottom, paint)
        }

        phase += 0.1f
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (amplitude * 2).toInt()
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec), // Use full parent width
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }
}