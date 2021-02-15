package com.klmn.napp.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.*
import com.klmn.napp.R

/*
* a container class that can be animated to hide / reveal children
* */
class CollapsingContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    /* whether the view is expanded / collapsed */
    var isExpanded: Boolean

    /* the animation duration of hiding / revealing content */
    var expandDuration: Long
        get() = animator.duration
        set(value) { animator.duration = value }

    private var animator: ValueAnimator

    /* toggle the view state (expanded / collapsed) */
    fun toggleExpanded() {
        isExpanded = !isExpanded

        if (isExpanded) animator.start()
        else animator.reverse()
    }

    init {
        animator = ValueAnimator().apply {
            addUpdateListener {
                setHeight(animatedValue as Int)
            }
        }

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CollapsingContainerView,
            defStyleAttr,
            0
        ).apply {
            try {
                isExpanded = getBoolean(R.styleable.CollapsingContainerView_expanded, false)
                expandDuration = getInt(R.styleable.CollapsingContainerView_expand_duration, 200).toLong()
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // update the expanded height on layout changes that aren't caused by the animation
        if (!animator.isRunning && measuredHeight > 0) {
            animator.setIntValues(0, measuredHeight)

            // if the view should be collapsed set height to 0
            if (!isExpanded) {
                setHeight(0)
                setMeasuredDimension(measuredWidth, 0)
            }
        }
    }

    private fun setHeight(height: Int) {
        layoutParams = layoutParams.apply {
            this.height = height
        }
        isVisible = height > 0
    }

    companion object {
        private const val KEY_EXPANDED = "collapsingContainerView.expanded"
        private const val KEY_SUPER = "collapsingContainerView.super"
    }

    override fun onSaveInstanceState() = bundleOf(
        KEY_EXPANDED to isExpanded,
        KEY_SUPER to super.onSaveInstanceState()
    )
    override fun onRestoreInstanceState(state: Parcelable?) = super.onRestoreInstanceState(
        if (state is Bundle) {
            isExpanded = state.getBoolean(KEY_EXPANDED)
            state.getParcelable(KEY_SUPER)
        }
        else state
    )
}