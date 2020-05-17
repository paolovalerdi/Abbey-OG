package com.paolovalerdi.abbey.ui.activities.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.carousel
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.epoxy.CardAlbumModel_
import com.paolovalerdi.abbey.adapter.epoxy.WikiModel_
import com.paolovalerdi.abbey.adapter.epoxy.songDetail
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.MediaDetailsWrapper
import com.paolovalerdi.abbey.ui.activities.base.AbsSlidingMusicPanelActivity
import com.paolovalerdi.abbey.ui.viewmodel.MediaDetailsViewModel
import com.paolovalerdi.abbey.ui.viewmodel.MediaDetailsViewModel.Companion.TYPE_ARTIST
import com.paolovalerdi.abbey.util.extensions.*
import kotlinx.android.synthetic.main.activity_media_detail.*

/**
 * @author Paolo valerdi
 */
@Suppress("UNCHECKED_CAST")
abstract class MediaDetailsActivity<T : Any> : AbsSlidingMusicPanelActivity() {

    companion object {

        const val EXTRA_MEDIA_TYPE = "media_type"
        const val EXTRA_MEDIA_ID = "media_id"

    }

    protected var controller: EpoxyController? = null
    private lateinit var mediaViewModel: MediaDetailsViewModel
    protected lateinit var mediaItem: T
    protected lateinit var content: MediaDetailsWrapper

    private val mediaType by lazy { intent?.extras?.getInt(EXTRA_MEDIA_TYPE)!! }
    private val mediaID by lazy {
        if (mediaType == TYPE_ARTIST) {
            intent?.extras?.getString(EXTRA_MEDIA_TYPE)!!
        } else {
            intent?.extras?.getInt(EXTRA_MEDIA_TYPE)!!
        }
    }

    abstract var backgroundColor: Int

    abstract fun loadMediaDetails(model: T)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideBottomNavigationBar(true)
        mediaViewModel = ViewModelProviders.of(this).get(MediaDetailsViewModel::class.java)
        addMusicServiceEventListener(mediaViewModel)
        mediaViewModel.setUpMedia(mediaType, mediaID)

        mediaViewModel.getMediaModel().observe(this,
            Observer<Any> { model ->
                mediaItem = model as T
                loadMediaDetails(mediaItem)
            }
        )

        mediaViewModel.getData().observe(this,
            Observer<MediaDetailsWrapper> { content ->
                this.content = content
                bindController()
            }
        )

        setUpToolbar()
        detailsMotionLayout?.doOnApplyWindowInsets { v, insets ->
            (v as MotionLayout).run {
                constraintSetIds.forEach { id ->
                    getConstraintSet(id).run {
                        constrainHeight(R.id.statusBar, insets.systemWindowInsetTop)
                    }
                }
                rebuildScene()
            }
        }
    }

    override fun createContentView(): View = wrapInSlidingMusicPanel(R.layout.activity_media_detail)

    protected fun setMediaTitle(title: String) {
        detailsTitle.text = title
        toolbarTitle.text = title
    }

    protected fun setColors(backgroundColor: Int, accentColor: Int) {
        this.backgroundColor = backgroundColor
        detailsMotionLayout.setBackgroundColor(backgroundColor)
        detailsRecyclerView.setBackgroundColor(backgroundColor)
        ViewCompat.setBackgroundTintList(detailsGradient, ColorStateList.valueOf(backgroundColor))
        detailsTitle.primaryTextColorFor(backgroundColor)
        toolbarTitle.primaryTextColorFor(backgroundColor)
        detailsText.secondaryTextColorFor(backgroundColor)
        toolbarContainer.setBackgroundColor(backgroundColor)
        toolbar.tintContentColorFor(backgroundColor)
        fill_space.setBackgroundColor(backgroundColor)
        setNavigationBarColor(backgroundColor)
        statusBar.setBackgroundColor(backgroundColor)
        setLightStatusBar(backgroundColor.isLight)
        getMiniPlayer().setColors(backgroundColor, accentColor)
        detailsButton.tintWith(accentColor)
    }

    protected fun bindController() {
        detailsRecyclerView.withModels {

            WikiModel_()
                .id("wiki")
                .content(content.headerText)
                .backgroundColor(backgroundColor)
                .addIf({
                    val shouldShow = content.headerText != null
                    if (shouldShow) detailsRecyclerView.smoothScrollToPosition(0)
                    shouldShow
                }, this)

            content.albums?.let { albums ->
                carousel {
                    id("albums")
                    numViewsToShowOnScreen(3f)
                    padding(Carousel.Padding(
                        resources.convertDpToPixels(if (content.headerText == null) 16f else 8f).toInt(),
                        resources.convertDpToPixels(8f).toInt()
                    ))
                    withModelsFrom(albums) { album ->
                        CardAlbumModel_()
                            .id(album.id)
                            .album(album)
                    }
                }
            }

            content.songs.forEachIndexed { index, song ->
                songDetail {
                    id(index)
                    song(song)
                    backgroundColor(backgroundColor)
                    onClickListener { _ -> MusicPlayerRemote.openQueue(ArrayList(content.songs), index, true) }
                }
            }

            controller = this
        }
    }

    protected open fun setSwipeAndDrag(controller: EpoxyController?) {}

    private fun setUpToolbar() {
        detailsButton.setOnClickListener { MusicPlayerRemote.openAndShuffleQueue(ArrayList(content.songs), true) }
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}