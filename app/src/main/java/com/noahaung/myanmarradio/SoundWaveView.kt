package com.noahaung.myanmarradio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class SoundWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val barCount = 20
    private var barWidth: Float = 0f
    private var maxAmplitude: Float = 100f
    private var isPlaying = false
    private val amplitudes = FloatArray(barCount) { Random.nextFloat() * maxAmplitude }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        barWidth = w.toFloat() / barCount
        maxAmplitude = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f

        for (i in 0 until barCount) {
            val left = i * barWidth
            val amplitude = if (isPlaying) Random.nextFloat() * maxAmplitude else amplitudes[i] / 2

            canvas.drawRect(
                left,
                centerY - amplitude,
                left + barWidth * 0.8f,
                centerY + amplitude,
                paint
            )
        }

        if (isPlaying) {
            for (i in amplitudes.indices) {
                amplitudes[i] = Random.nextFloat() * maxAmplitude
            }
            postInvalidateDelayed(100)
        }
    }

    fun setPlaying(playing: Boolean) {
        isPlaying = playing
        invalidate()
    }
}