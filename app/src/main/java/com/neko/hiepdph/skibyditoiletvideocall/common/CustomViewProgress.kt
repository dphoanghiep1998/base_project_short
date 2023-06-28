package com.neko.hiepdph.skibyditoiletvideocall.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import android.graphics.PathMeasure
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.picker.TextFontCache


class CustomViewProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var firstInit = false
    private var length = 0f
    private var path = Path()
    private var timeTextPath = Path()

    private val progressPaint = Paint(1).apply {
        strokeWidth = 10f
        color = context.getColor(R.color.progress_call)
        style = Paint.Style.STROKE
    }
    private val borderoutSidePaint = Paint(1).apply {
        strokeWidth = 10f
        color = context.getColor(R.color.progress_call_inactive)
        style = Paint.Style.STROKE
    }
    private val borderInsidePaint = Paint(1).apply {
        strokeWidth = 10f
        color = context.getColor(R.color.white)
        style = Paint.Style.STROKE
    }
    private val textPaint = Paint(1).apply {
        color = context.getColor(R.color.white)
        textSize = 100f
        style = Paint.Style.FILL
        typeface = TextFontCache.getTypeface("app_font_600.ttf", context)

    }
    private var rectOutSide = RectF()
    private var rectInSide = RectF()
    private var timeText = "00:00"

    var intervals = floatArrayOf(0f, 0f)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomViewProgress)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, 400)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectOutSide = RectF(10f, 10f, w.toFloat() - 10, h.toFloat() - 10)
        rectInSide = RectF(60f, 60f, w.toFloat() - 60, h.toFloat() - 60)
        rectOutSide.inset(borderoutSidePaint.strokeWidth, borderoutSidePaint.strokeWidth)
        createRoundedRectWithCustomStartPoint()
        timeTextPath.addRect(rectInSide,Path.Direction.CW)

//        path.addRoundRect(rectOutSide, 40f, 40f, Path.Direction.CW)
        length = PathMeasure(path, false).length
        intervals = floatArrayOf(length, length)
        val effect: PathEffect = DashPathEffect(intervals, length)
        progressPaint.pathEffect = effect
    }

    private fun createRoundedRectWithCustomStartPoint() {
        val radius = 40f
        path.apply {
            moveTo(width / 2f, 20f)
            lineTo(width - 20f - radius * 2, 20f)
            arcTo(width - 20f - radius * 2, 20f, width - 20f, 20f + radius * 2, -90F, 90F, false)
            lineTo(width - 20f, height - 20f - radius * 2)
            arcTo(
                width - 20f - 2 * radius,
                height - 20f - radius * 2,
                width - 20f,
                height - 20f,
                0F,
                90F,
                false
            )
            lineTo(20f + radius * 2, height - 20f)
            arcTo(
                20f, height - 20f - radius * 2, 20f + radius * 2, height - 20f, 90F, 90F, false
            )
            lineTo(20f, 20f + radius * 2)
            arcTo(
                20f, 20f, 20f + 2 * radius, 20f + 2 * radius, 180F, 90F, false
            )
            lineTo(width / 2f, 20f)

        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRoundRect(rectOutSide, 40f, 40f, borderoutSidePaint)
        canvas?.drawRoundRect(rectInSide, 40f, 40f, borderInsidePaint)

        canvas?.drawPath(path, progressPaint)
        val r = Rect()
        textPaint.getTextBounds(timeText, 0, timeText.length, r)

        canvas?.drawText(
            timeText, width / 2f - r.width() / 2, height / 2f + r.height()/2, textPaint
        )


    }

    fun setProgress(progress: Int) {
        val effect: PathEffect = DashPathEffect(intervals, length - length * progress / 100)
        progressPaint.pathEffect = effect
        Log.d("TAG", "setProgress: " + length)
        invalidate()
    }

    fun setTime(time:Long){

    }

}