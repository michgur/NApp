package com.klmn.napp.ui.components

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.os.bundleOf
import com.klmn.napp.R
import kotlin.math.abs
import kotlin.math.ceil

class RectProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val path = Path()
    private var lastPathPoint = PointF()

    private var padding = 0f
    private var paddedWidth = 0f
    private var paddedHeight = 0f

    private var progress = 0f

    private val animator = ValueAnimator.ofFloat(0f, 2f).apply {
        duration = 4000L
        repeatCount = INFINITE
        addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
    }
    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    init {
        setWillNotDraw(false)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RectProgressBar,
            defStyleAttr,
            0
        ).apply {
            try {
                pathPaint.color = getColor(R.styleable.RectProgressBar_strokeColor, Color.BLACK)
                pathPaint.strokeWidth = getDimension(R.styleable.RectProgressBar_strokeWidth, 1f)
                padding = pathPaint.strokeWidth / 2
                getDimension(R.styleable.RectProgressBar_cornerRadius, 0f).takeIf {
                    it > 0
                }?.let { cornerRadius ->
                    pathPaint.pathEffect = CornerPathEffect(cornerRadius)
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) {
            if (!animator.isStarted) animator.start()
        } else if (animator.isStarted) animator.end()
        println("visibility changed (visible=${visibility == VISIBLE})")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        paddedWidth = measuredWidth.toFloat()
        paddedHeight = measuredHeight.toFloat()

        val paddingCeil = ceil(padding).toInt()
        setMeasuredDimension(
            measuredWidth + paddingCeil,
            measuredHeight + paddingCeil
        )
    }

    /*
    * based on google's mlkit showcase app
    * https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/barcodedetection/BarcodeLoadingGraphic.kt
    * */
    override fun onDraw(canvas: Canvas) {
        path.reset()
        val perimeter = (paddedWidth + paddedHeight) * 2
        var startOffset = perimeter * progress % perimeter

        var i = 0
        while (i < 4) {
            val edgeLength = if (i % 2 == 0) paddedWidth else paddedHeight
            if (startOffset <= edgeLength) {
                getCoordinates(i, startOffset).let { xy ->
                    lastPathPoint.set(xy.first, xy.second)
                    path.moveTo(lastPathPoint.x, lastPathPoint.y)
                }
                break
            }

            startOffset -= edgeLength
            i++
        }

        var pathLength = perimeter * abs(progress - 1f) * .8f
        for (j in 0..3) {
            val edgeIndex = (i + j) % 4
            val nextEdgeIndex = (edgeIndex + 1) % 4
            val edgeLength = getCoordinates(nextEdgeIndex).let { xy ->
                abs(xy.first - lastPathPoint.x) + abs(xy.second - lastPathPoint.y)
            }

            if (edgeLength >= pathLength) {
                getCoordinates(edgeIndex, lastPathPoint, pathLength).let { xy ->
                    path.lineTo(xy.first, xy.second)
                }
                break
            }

            getCoordinates(nextEdgeIndex).let { xy ->
                lastPathPoint.set(xy.first, xy.second)
                path.lineTo(lastPathPoint.x, lastPathPoint.y)
            }
            pathLength -= edgeLength
        }

        canvas.drawPath(path, pathPaint)
    }

    private fun getCoordinates(
        edgeIndex: Int,
        offset: Float = 0f
    ) = when (edgeIndex) {
        0 -> offset + padding to padding
        1 -> paddedWidth - padding to offset + padding
        2 -> paddedWidth - padding - offset to paddedHeight - padding
        3 -> padding to paddedHeight - padding - offset
        else -> throw IllegalArgumentException()
    }

    private fun getCoordinates(
        edgeIndex: Int,
        point: PointF,
        offset: Float = 0f
    ) = when (edgeIndex) {
        0 -> point.x + offset to point.y
        1 -> point.x to point.y + offset
        2 -> point.x - offset to point.y
        3 -> point.x to point.y - offset
        else -> throw IllegalArgumentException()
    }

    companion object {
        private const val KEY_PROGRESS = "barcodeLoadingView.progress"
        private const val KEY_SUPER = "barcodeLoadingView.super"
    }

    override fun onSaveInstanceState() = bundleOf(
        KEY_PROGRESS to progress,
        KEY_SUPER to super.onSaveInstanceState()
    )

    override fun onRestoreInstanceState(state: Parcelable?) = super.onRestoreInstanceState(
        if (state is Bundle) {
            progress = state.getFloat(KEY_PROGRESS)
            animator.setCurrentFraction(progress / 2f)
            state.getParcelable(KEY_SUPER)
        } else state
    )
}