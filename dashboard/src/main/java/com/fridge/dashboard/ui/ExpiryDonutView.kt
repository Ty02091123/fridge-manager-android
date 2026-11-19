package com.fridge.dashboard.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.fridge.dashboard.R
import kotlin.math.min

class ExpiryDonutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val density = resources.displayMetrics.density
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 24 * density
        strokeCap = Paint.Cap.ROUND
    }
    private val totalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.on_surface)
        textSize = sp(30f)
        isFakeBoldText = true
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.on_surface_variant)
        textSize = sp(13f)
    }
    private val arcBounds = RectF()
    private var freshCount = 0
    private var expiringCount = 0
    private var expiredCount = 0

    private fun sp(value: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        value,
        resources.displayMetrics
    )

    fun setData(fresh: Int, expiring: Int, expired: Int) {
        freshCount = fresh.coerceAtLeast(0)
        expiringCount = expiring.coerceAtLeast(0)
        expiredCount = expired.coerceAtLeast(0)
        contentDescription = "$freshCount 新鲜，$expiringCount 临期，$expiredCount 过期"
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2f - ringPaint.strokeWidth
        arcBounds.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        val total = freshCount + expiringCount + expiredCount
        ringPaint.color = ContextCompat.getColor(context, R.color.chart_track)
        canvas.drawArc(arcBounds, 0f, 360f, false, ringPaint)

        if (total > 0) {
            var startAngle = -90f
            listOf(
                freshCount to R.color.status_fresh,
                expiringCount to R.color.status_expiring,
                expiredCount to R.color.status_expired
            ).forEach { (count, color) ->
                if (count == 0) return@forEach
                val sweep = count.toFloat() / total * 360f
                ringPaint.color = ContextCompat.getColor(context, color)
                val gap = if (sweep > 5f) 2.5f else 0f
                canvas.drawArc(arcBounds, startAngle + gap, sweep - gap * 2, false, ringPaint)
                startAngle += sweep
            }
        }

        canvas.drawText(
            total.toString(),
            centerX,
            centerY + totalPaint.textSize / 3f,
            totalPaint
        )
        canvas.drawText(
            context.getString(R.string.chart_total_label),
            centerX,
            centerY + totalPaint.textSize + labelPaint.textSize,
            labelPaint
        )
    }
}
