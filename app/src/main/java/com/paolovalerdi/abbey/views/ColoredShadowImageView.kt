package com.paolovalerdi.abbey.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.util.extensions.dpToPx
import kotlin.properties.Delegates

class ColoredShadowImageView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(
    context,
    attributes,
    defStyleAttr
) {

    companion object {
        private const val DEFAULT_RADIUS = 0.5f
        private const val BRIGHTNESS = -25f
        private const val SATURATION = 1.3f
        private const val TOP_OFFSET = 2f
    }

    var radiusOffset by Delegates.vetoable(DEFAULT_RADIUS, { _, _, newValue ->
        newValue > 0F || newValue <= 1
    })

    private var topOffset: Float = TOP_OFFSET

    init {
        cropToPadding = true
        scaleType = ScaleType.CENTER_CROP
        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.ColoredShadowImageView, 0, 0)
        radiusOffset = typedArray.getFloat(R.styleable.ColoredShadowImageView_radiusOffset, DEFAULT_RADIUS)
        topOffset = typedArray.getFloat(R.styleable.ColoredShadowImageView_topOffSet, TOP_OFFSET)
        typedArray.recycle()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        setBlurShadow { super.setImageDrawable(BitmapDrawable(resources, bm)) }
    }

    override fun setImageResource(resId: Int) {
        setBlurShadow { super.setImageDrawable(ContextCompat.getDrawable(context, resId)) }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        setBlurShadow { super.setImageDrawable(drawable) }
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(ScaleType.CENTER_CROP)
    }

    private fun setBlurShadow(setImage: () -> Unit = {}) {
        background = null
        if (height != 0 || measuredHeight != 0) {
            setImage()
            makeBlurShadow()
        }
    }

    private fun makeBlurShadow() {
        var radius = resources.getInteger(R.integer.radius).toFloat()
        radius *= 2 * radiusOffset
        val blurredImage = BlurShadow.getBlurredBitmap(this, width, height - dpToPx(topOffset), radius)
        val colorMatrix = ColorMatrix(floatArrayOf(
            1f, 0f, 0f, 0f, BRIGHTNESS,
            0f, 1f, 0f, 0f, BRIGHTNESS,
            0f, 0f, 1f, 0f, BRIGHTNESS,
            0f, 0f, 0f, 1f, 0f)).apply { setSaturation(SATURATION) }

        background = BitmapDrawable(resources, blurredImage).apply {
            this.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
    }

}