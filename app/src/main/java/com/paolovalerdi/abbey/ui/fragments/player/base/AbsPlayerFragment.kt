package com.paolovalerdi.abbey.ui.fragments.player.base

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.song.QueueAdapter
import com.paolovalerdi.abbey.ui.dialogs.*
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.interfaces.NowPlayingCallback
import com.paolovalerdi.abbey.interfaces.NowPlayingColorCallback
import com.paolovalerdi.abbey.interfaces.PaletteColorHolder
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.model.lyrics.Lyrics
import com.paolovalerdi.abbey.ui.activities.base.AbsMusicServiceActivity
import com.paolovalerdi.abbey.ui.activities.tageditor.AbsTagEditorActivity
import com.paolovalerdi.abbey.ui.activities.tageditor.SongTagEditorActivity
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.util.ImageUtil
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.doOnApplyWindowInsets
import com.paolovalerdi.abbey.util.extensions.getColorControlNormal
import com.paolovalerdi.abbey.util.extensions.isCollapsed
import com.paolovalerdi.abbey.util.extensions.isExpanded
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.paolovalerdi.abbey.util.preferences.SHOW_VOLUME_SLIDER
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


/**
 * @author Paolo Valerdi
 */
abstract class AbsPlayerFragment<P : AbsPlaybackControlsFragment, C : AbsPlayerAlbumCoverFragment> : Fragment()
    , NowPlayingColorCallback
    , PaletteColorHolder
    , Toolbar.OnMenuItemClickListener
    , SlidingUpPanelLayout.PanelSlideListener
    , SharedPreferences.OnSharedPreferenceChangeListener {

    private var callBacks: NowPlayingCallback? = null
    protected var playerToolbar: Toolbar? = null
    protected var lyrics: Lyrics? = null
    protected var navigationBarHeight: Int = -1

    protected lateinit var playbackControls: P
    private lateinit var coverControls: C
    protected lateinit var playerSlidingPanelLayout: SlidingUpPanelLayout

    private var playingQueueAdapter: QueueAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var queueRecyclerView: RecyclerView? = null
    private var upNextTextView: TextView? = null
    private var wasExpanded = false
    private var latestPosition = -1
    private var currentSong = Song.EMPTY_SONG

    abstract val layoutRes: Int

    abstract val navigationBarColor: Int

    abstract var lastColor: Int

    abstract fun onFavoriteUpdated(drawable: Drawable)

    abstract fun setUpPanelHeight()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callBacks = context as NowPlayingCallback
        } catch (e: ClassCastException) {
            throw RuntimeException("${context.javaClass.simpleName} must implement ${NowPlayingCallback::class.java.simpleName}")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callBacks = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getPlayingQueue().observe(
            viewLifecycleOwner,
            Observer<List<Song>> { playingQueue ->
                onQueueChanged(playingQueue)
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentQueuePosition().observe(
            viewLifecycleOwner,
            Observer<Int> { position ->
                latestPosition = position
                playingQueueAdapter?.setCurrent(latestPosition)
                if (playerSlidingPanelLayout.isCollapsed) {
                    resetToCurrentPosition()
                }
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentLyrics().observe(
            viewLifecycleOwner,
            Observer {
                lyrics = it
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentSong().observe(
            viewLifecycleOwner,
            Observer<Song> { currentSong ->
                onCurrentSongChanged(currentSong)
            }
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutRes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.doOnApplyWindowInsets { _, windowInsets ->
            navigationBarHeight = windowInsets.systemWindowInsetBottom
        }
        bindViews(view)
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroyView() {
        playerSlidingPanelLayout.removePanelSlideListener(this)
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
        super.onDestroyView()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_show_lyrics -> {
                LyricsDialog.create(lyrics).show(childFragmentManager, null)
                return true
            }
            R.id.action_sleep_timer -> {
                SleepTimerDialog().show(childFragmentManager, null)
                return true
            }
            R.id.action_toggle_favorite -> {
                toggleFavorite()
                return true
            }
            R.id.action_share -> {
                SongShareDialog.create(currentSong).show(childFragmentManager, null)
                return true
            }
            R.id.action_equalizer -> {
                NavigationUtil.openEqualizer(activity!!)
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistSheet.create(currentSong).show(childFragmentManager, null)
                return true
            }
            R.id.action_clear_playing_queue -> {
                MusicPlayerRemote.clearQueue()
                return true
            }
            R.id.action_save_playing_queue -> {
                CreatePlaylistDialogKt.create(MusicPlayerRemote.getPlayingQueue()).show(childFragmentManager, null)
                return true
            }
            R.id.action_tag_editor -> {
                val intent = Intent(activity, SongTagEditorActivity::class.java)
                intent.putExtra(AbsTagEditorActivity.EXTRA_ID, currentSong.id)
                startActivity(intent)
                return true
            }
            R.id.action_details -> {
                SongDetailDialog.create(currentSong).show(childFragmentManager, "SONG_DETAIL")
                return true
            }
            R.id.action_go_to_album -> {
                NavigationUtil.goToAlbum(activity as AppCompatActivity, currentSong.albumId)
                return true
            }
            R.id.action_go_to_artist -> {
                NavigationUtil.goToArtist(activity as AppCompatActivity, currentSong.artistId)
                return true
            }
        }
        return false
    }

    override fun onPanelStateChanged(
        panel: View?,
        previousState: SlidingUpPanelLayout.PanelState?,
        newState: SlidingUpPanelLayout.PanelState?
    ) {
        when (newState) {
            SlidingUpPanelLayout.PanelState.COLLAPSED -> resetToCurrentPosition()
            SlidingUpPanelLayout.PanelState.ANCHORED -> playerSlidingPanelLayout.isCollapsed = true
            else -> {
            }
        }
    }

    override fun onPanelSlide(p0: View?, offset: Float) {}

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == SHOW_VOLUME_SLIDER) {
            playerSlidingPanelLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    playerSlidingPanelLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    setUpPanelHeight()
                }
            })
        }
    }

    override fun onColorChanged(color: Int, isTopLight: Boolean) {
        lastColor = color
        playbackControls.setColors(color)
        callBacks?.onPaletteColorChanged()
        loadIsFavorite(isTopLight)
    }


    override fun getPaletteColor(): Int = lastColor

    open fun onShow() {
    }

    open fun onHide() {

    }

    fun onBackPressed(): Boolean {
        wasExpanded = playerSlidingPanelLayout.isExpanded
        playerSlidingPanelLayout.isCollapsed = true
        return wasExpanded
    }

    protected open fun onQueueChanged(playingQueue: List<Song>) {
        playingQueueAdapter?.swapDataSet(playingQueue)
        playingQueueAdapter?.setCurrent(latestPosition)
    }

    protected open fun onCurrentSongChanged(latestSong: Song) {
        currentSong = latestSong
    }

    private fun loadIsFavorite(light: Boolean) {
        lifecycleScope.launch(Main) {
            val isFavorite = MusicUtil.isFavorite(requireContext(), currentSong)
            val iconColor = requireContext().getColorControlNormal(light)
            val resID = if (isFavorite) R.drawable.ic_favorite_white_24dp else R.drawable.ic_favorite_border_white_24dp
            val drawable = ImageUtil.getTintedVectorDrawable(requireContext(), resID, iconColor)
            drawable?.let { onFavoriteUpdated(it) }
        }
    }

    protected fun toggleFavorite() {
        MusicUtil.toggleFavorite(requireContext(), currentSong)
        loadIsFavorite(true)
    }

    private fun bindViews(view: View) {
        playerSlidingPanelLayout = view.findViewById(R.id.player_sliding_layout)
        queueRecyclerView = view.findViewById(R.id.player_queue_recycler_view)
        playerToolbar = view.findViewById(R.id.player_toolbar)
        upNextTextView = view.findViewById(R.id.player_queue_sub_header)

        queueRecyclerView?.run { setUpRecyclerView(this) }
        playerToolbar?.run {
            inflateMenu(R.menu.menu_player)
            setNavigationIcon(R.drawable.ic_arrow_down_24dp)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
            setOnMenuItemClickListener(this@AbsPlayerFragment)

        }
        setUpSubFragment()

        playerSlidingPanelLayout.apply {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    playerSlidingPanelLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    setUpPanelHeight()
                }
            })
            addPanelSlideListener(this@AbsPlayerFragment)
            setScrollableView(queueRecyclerView)
        }

    }

    private fun setUpSubFragment() {
        playbackControls = childFragmentManager.findFragmentById(R.id.playback_controls_fragment) as P
        coverControls = childFragmentManager.findFragmentById(R.id.player_album_cover_fragment) as C
        coverControls.setCallbacks(this)
    }

    private fun resetToCurrentPosition() {
        queueRecyclerView?.stopScroll()
        linearLayoutManager?.scrollToPositionWithOffset(latestPosition + 1, 0)
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView) {
        val withOnSurfaceColor = PreferenceUtil.nowPlayingScreen == NowPlayingScreen.CARD || PreferenceUtil.nowPlayingScreen == NowPlayingScreen.ABBEY || PreferenceUtil.nowPlayingScreen == NowPlayingScreen.FLAT
        playingQueueAdapter = QueueAdapter(current = 0, withOnSurfaceColor = withOnSurfaceColor, isBlurTheme = PreferenceUtil.nowPlayingScreen == NowPlayingScreen.BLUR)
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = playingQueueAdapter
        linearLayoutManager?.scrollToPositionWithOffset(MusicPlayerRemote.getPosition() + 1, 0)
    }

}