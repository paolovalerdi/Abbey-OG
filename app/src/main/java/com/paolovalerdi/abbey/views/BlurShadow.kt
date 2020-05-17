package com.paolovalerdi.abbey.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.widget.ImageView
import com.paolovalerdi.abbey.App

object BlurShadow {

    private var renderScript: RenderScript? = null

    private const val DOWNSCALE_FACTOR = 0.25f

    init {
        if (renderScript == null) {
            renderScript = RenderScript.create(App.staticContext)
        }
    }

    fun getBlurredBitmap(view: ImageView, width: Int, height: Int, radius: Float): Bitmap? {

        val src = getBitmapForView(view, DOWNSCALE_FACTOR, width, height) ?: return null
        val input = Allocation.createFromBitmap(renderScript, src)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.apply {
            setRadius(radius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(src)
        output.destroy()
        input.destroy()
        script.destroy()
        return src
    }

    private fun getBitmapForView(
        view: ImageView,
        downscaleFactor: Float,
        width: Int,
        height: Int
    ): Bitmap? {
        try {
            val bitmap = Bitmap.createBitmap(
                (width * downscaleFactor).toInt(),
                (height * downscaleFactor).toInt(),
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            val matrix = Matrix()
            matrix.preScale(downscaleFactor, downscaleFactor)
            canvas.matrix = matrix
            view.draw(canvas)

            return bitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            Log.e("BLUR", "Couldn't create getBlurredBitmap for image")
            return null
        }
    }
}