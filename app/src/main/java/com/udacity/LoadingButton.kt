package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    // Custom attributes
    private var backgroundColor = 0
    private var progressBarColor = 0
    private var progressCircularColor = 0
    private var textColor = 0
    private var textSize = 0f
    private var text = ""

    private var currentProgressPercentage = 0f
    private var textWidth = 0f

    private val textPosition: PointF = PointF(0f, 0f)
    private val circularProgressPosition: PointF = PointF(0f, 0f)
    private val circularProgressRadius = 36f // TODO: Can be extracted to attrs

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            progressBarColor = getColor(R.styleable.LoadingButton_progressBarColor, 0)
            progressCircularColor = getColor(R.styleable.LoadingButton_progressCircularColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            textSize = convertSpToPixels(getString(R.styleable.LoadingButton_textSize) ?: "0sp")
            text = getString(R.styleable.LoadingButton_text) ?: ""
        }

        isClickable = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = backgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = progressBarColor
        val currentProgressButtonPosition = currentProgressPercentage / 100 * widthSize
        canvas?.drawRect(0f, 0f, currentProgressButtonPosition, heightSize.toFloat(), paint)

        paint.color = textColor
        paint.textSize = textSize
        textPosition.computeXYForText()
        canvas?.drawText(text, textPosition.x, textPosition.y, paint)

        paint.color = progressCircularColor
        val sweepAngle = currentProgressPercentage / 100 * 360
        circularProgressPosition.computeXYForCircularProgress()
        canvas?.drawArc(
            circularProgressPosition.x - circularProgressRadius,
            circularProgressPosition.y - circularProgressRadius,
            circularProgressPosition.x + circularProgressRadius,
            circularProgressPosition.y + circularProgressRadius,
            -90f, sweepAngle, true, paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun startLoadingAnimation() {
        // Set text to loading
        text = context.getString(R.string.button_loading)

        // Start animation
        valueAnimator.apply {
            setFloatValues(0f, 100f)
            duration = 2000
            addUpdateListener {
                currentProgressPercentage = it.animatedValue as Float
                invalidate()
            }
        }
        valueAnimator.start()
    }

    private fun PointF.computeXYForText() {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val textHeight = bounds.height()
        textWidth = bounds.width().toFloat()

        x = (widthSize / 2).toFloat()
        y = ((heightSize + textHeight) / 2).toFloat()
    }

    private fun PointF.computeXYForCircularProgress() {
        x = (widthSize / 2) + (textWidth / 2) + circularProgressRadius
        y = (heightSize / 2).toFloat()
    }

    private fun convertSpToPixels(sp: String): Float {
        val modifiedString = if (sp.length > 2) {
            sp.substring(0, sp.length - 2)
        } else {
            sp
        }
        return try {
            modifiedString.toFloat() * context.resources.displayMetrics.scaledDensity
        } catch (e: NumberFormatException) {
            0.0f
        }

    }
}