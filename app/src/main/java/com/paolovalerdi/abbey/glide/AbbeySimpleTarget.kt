package com.paolovalerdi.abbey.glide


import android.graphics.drawable.Drawable

import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.util.Util
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

open class AbbeySimpleTarget<T>
@JvmOverloads
constructor(
    private val width: Int = Target.SIZE_ORIGINAL,
    private val height: Int = Target.SIZE_ORIGINAL
) : Target<T> {

    // Since this class listens to lifeCycle events it seems (lmao) to be safe to scope the coroutine here.
    private val job = Job()
    private val scope = CoroutineScope(job + Main)

    private var request: Request? = null

    override fun setRequest(request: Request?) {
        this.request = request
    }

    override fun getRequest(): Request? = request

    override fun onLoadStarted(placeholder: Drawable?) {}

    override fun onLoadFailed(errorDrawable: Drawable?) {}

    override fun onResourceReady(resource: T, transition: Transition<in T>?) {}

    override fun onLoadCleared(placeholder: Drawable?) {}

    override fun removeCallback(cb: SizeReadyCallback) {}

    override fun onStart() {}


    override fun getSize(cb: SizeReadyCallback) {
        if (!Util.isValidDimensions(width, height)) {
            throw IllegalArgumentException(
                "Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given" + " width: "
                    + width + " and height: " + height + ", either provide dimensions in the constructor"
                    + " or call override()")
        }
        cb.onSizeReady(width, height)
    }

    override fun onStop() {
        job.cancel()
    }

    override fun onDestroy() {
        job.cancel()
    }

    protected fun launch(
        context: CoroutineDispatcher = Main,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

}
