package com.klmn.napp.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
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
    var expandDuration: Long = 0L
        set(value) {
            field = value
            if (::animator.isInitialized) animator.duration = value
        }

    private lateinit var animator: ValueAnimator
    private var expandedHeight = 0

    /* toggle the view state (expanded / collapsed) */
    fun toggleExpanded() {
        isExpanded = !isExpanded

        if (isExpanded) animator.apply {
            setIntValues(0, expandedHeight)
            start()
        }
        else {
            expandedHeight = measuredHeight
            animator.reverse()
        }
    }

    init {
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

        doOnLayout {
            expandedHeight = measuredHeight
            animator = ValueAnimator.ofInt(0, expandedHeight).apply {
                duration = expandDuration
                addUpdateListener {
                    setHeight(animatedValue as Int)
                }
            }
            if (!isExpanded) setHeight(0)
        }
    }

    private fun setHeight(height: Int) {
        layoutParams = layoutParams.apply {
            this.height = height
        }
        isVisible = height > 0
    }

    companion object {
        private const val TAG = "CollapsingContainerView"

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