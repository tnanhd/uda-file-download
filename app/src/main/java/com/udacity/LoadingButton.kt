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
    private var textColor = 0
    private var textSize = 0.0f
    private var text = ""

    private val screenHeight = context.resources.displayMetrics.heightPixels

    private val textPosition: PointF = PointF(0.0f, 0.0f)

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            textSize = convertSpToPixels(getString(R.styleable.LoadingButton_textSize) ?: "0sp")
            text = getString(R.styleable.LoadingButton_text) ?: ""
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = backgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = textColor
        paint.textSize = textSize
        textPosition.computeXYForText()
        canvas?.drawText(text, textPosition.x, textPosition.y, paint)
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

    private fun PointF.computeXYForText() {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val textHeight = bounds.height()

        x = (widthSize / 2).toFloat()
        y = ((heightSize + textHeight) / 2).toFloat()
    }

    private fun convertSpToPixels(sp: String): Float {
        val modifiedString = if (sp.length > 2) {
            sp.substring(0, sp.length - 2)
        } else {
            sp
        }
        return try {
            modifiedString.toFloat() * context.resources.displayMetrics.scaledDensity
        } catch (e : NumberFormatException) {
            0.0f
        }

    }
}