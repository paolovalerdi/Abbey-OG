package com.paolovalerdi.abbey.util.extensions

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.animation.PathInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnStart
import androidx.core.view.forEach
import androidx.core.view.isGone
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.kabouzeid.appthemehelper.util.MaterialValueHelper
import com.sothree.slidinguppanel.SlidingUpPanelLayout

const val COLOR_TRANSITION_DURATION = 300L

fun View.setAlphaAndHide(newAlpha: Float) {
    alpha = newAlpha
    isGone = alpha == 0f
}

//  Taken from: https://medium.com/androiddevelopers/windowinsets-listeners-to-layouts-8f9ccc8fa4d1
fun View.doOnApplyWindowInsets(block: (View, WindowInsets) -> Unit) {
    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets)
        insets
    }
    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun Toolbar.tintContentColorFor(background: Int) {
    val color = if (background.isLight) context.getColorControlNormal(true) else Color.WHITE
    navigationIcon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    overflowIcon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    menu?.forEach { item ->
        item.icon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}

fun Toolbar.tintContentColorFor(light: Boolean) {
    val color = if (light) Color.WHITE else Color.BLACK
    tintContentColorFor(color)
}

fun FloatingActionButton.applyColor(color: Int) {
    backgroundTintList = ColorStateList.valueOf(color)
    val iconColor = getPrimaryTextColor(color.isLight)
    drawable.setTint(iconColor)
}

fun MaterialButton.applyAccentColor() {
    val textColor = MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(ThemeStore.accentColor(context)))
    backgroundTintList = ColorStateList.valueOf(ThemeStore.accentColor(context))
    setTextColor(textColor)
    icon?.mutate()?.setTint(textColor)
}

fun MaterialButton.tintWith(color: Int) {
    backgroundTintList = ColorStateList.valueOf(color)
    primaryTextColorFor(color)
}

fun AppCompatSeekBar.tintWith(
    thumbColor: Int,
    progressBackgroundColor: Int = context.getColorControlDisabled(thumbColor.isLight),
    tintProgressBackground: Boolean = false
) {
    thumbTintList = ColorStateList.valueOf(thumbColor)
    progressTintList = ColorStateList.valueOf(thumbColor)
    if (tintProgressBackground) {
        progressBackgroundTintList = ColorStateList.valueOf(progressBackgroundColor)
    }

}

fun AppCompatSeekBar.tintFor(backgroundColor: Int) {
    tintWith(
        context.getColorControlNormal(backgroundColor.isLight),
        getSecondaryTextColor(backgroundColor.isLight),
        true
    )
}

fun ImageView.setTintFor(color: Int) {
    setColorFilter(MaterialValueHelper.getSecondaryTextColor(
        context,
        ColorUtil.isColorLight(color)),
        PorterDuff.Mode.SRC_IN
    )
}

fun ImageView.setTint(color: Int) {
    setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

fun TextView.primaryTextColorFor(background: Int) {
    setTextColor(getPrimaryTextColor(background.isLight))
}

fun TextView.secondaryTextColorFor(background: Int) {
    setTextColor(getSecondaryTextColor(background.isLight))
}

inline var SlidingUpPanelLayout.isCollapsed: Boolean
    get() = panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
    set(value) {
        panelState = if (value) SlidingUpPanelLayout.PanelState.COLLAPSED else SlidingUpPanelLayout.PanelState.EXPANDED
    }

inline var SlidingUpPanelLayout.isExpanded: Boolean
    get() = panelState == SlidingUpPanelLayout.PanelState.EXPANDED
    set(value) {
        panelState = if (value) SlidingUpPanelLayout.PanelState.EXPANDED else SlidingUpPanelLayout.PanelState.COLLAPSED
    }

fun View.dpToPx(dp: Float): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp,
    resources.displayMetrics
).toInt()

@SuppressLint("ObjectAnimatorBinding")
fun FloatingActionButton.animateColoChanging(@ColorInt from: Int, @ColorInt to: Int) {
    ObjectAnimator.ofInt(this, "backgroundTint", from, to).apply {
        duration = COLOR_TRANSITION_DURATION
        setEvaluator(ArgbEvaluator())
        interpolator = PathInterpolator(0.4f, 0f, 1f, 1f)
        addUpdateListener { animation ->

            val animatedValue = animation.animatedValue as Int
            backgroundTintList = ColorStateList.valueOf(animatedValue)
        }
        doOnStart {
            drawable?.run {
                setColorFilter(getPrimaryTextColor(to.isLight))
            }
        }
    }.start()
}

@SuppressLint("ObjectAnimatorBinding")
fun AppCompatSeekBar.animateColorChanging(@ColorInt from: Int, @ColorInt to: Int, thumbOnly: Boolean = false) {
    ObjectAnimator.ofInt(this, "thumbTint", from, to).apply {
        duration = COLOR_TRANSITION_DURATION
        setEvaluator(ArgbEvaluator())
        interpolator = PathInterpolator(0.4f, 0f, 1f, 1f)
        addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            tintWith(animatedValue)
        }
    }.start()
}

fun AppCompatTextView.animateColorChanging(@ColorInt from: Int, @ColorInt to: Int) {
    ObjectAnimator.ofArgb(this, "textColor", from, to).apply {
        duration = COLOR_TRANSITION_DURATION
        interpolator = PathInterpolator(0.4f, 0f, 1f, 1f)
    }.start()
}
