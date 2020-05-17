package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.epoxy.*
import com.paolovalerdi.abbey.model.HomeContentWrapper
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerFragment
import com.paolovalerdi.abbey.ui.viewmodel.LibraryViewModel.Companion.HOME_CONTENT
import com.paolovalerdi.abbey.ui.viewmodel.MediaDetailsViewModel.Companion.SMART_PLAYLIST_HISTORY_ID
import com.paolovalerdi.abbey.ui.viewmodel.MediaDetailsViewModel.Companion.SMART_PLAYLIST_LAST_ADDED_ID
import com.paolovalerdi.abbey.ui.viewmodel.MediaDetailsViewModel.Companion.SMART_PLAYLIST_TOP_TRACKS_ID
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.convertDpToPixels
import com.paolovalerdi.abbey.util.extensions.withModelsFrom
import com.paolovalerdi.abbey.util.preferences.LAST_ADDED_CUTOFF
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author Paolo Valerdi
 */
class HomeFragment : AbsLibraryPagerFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val carouselPadding by lazy {
        Carousel.Padding(
            resources.convertDpToPixels(20f).toInt(),
            resources.convertDpToPixels(16f).toInt(),
            resources.convertDpToPixels(20f).toInt(),
            resources.convertDpToPixels(16f).toInt(),
            resources.convertDpToPixels(9f).toInt()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        libraryFragment.viewModel.getHomeContent().observe(viewLifecycleOwner,
            Observer<HomeContentWrapper> { homeContent ->

                homeRecyclerView.withModels {

                    if (homeContent.topArtist.isNotEmpty()) {

                        sectionHeader {
                            id("top_tracks_header_section")
                            title(getString(R.string.my_top_tracks))
                            clickListener { _ -> NavigationUtil.goToSmartPlaylist(requireActivity(), SMART_PLAYLIST_TOP_TRACKS_ID) }
                        }

                        mostPlayedArtist {
                            id("most_played_artist")
                            artist(homeContent.topArtist.first())
                        }

                        homeContent.topArtist.subList(1, homeContent.topArtist.size).forEachIndexed { index, artist ->
                            topArtist {
                                id(artist.id)
                                artist(artist)
                                position(index + 2)
                            }
                        }

                    }

                    if (homeContent.lastAddedAlbums.isNotEmpty()) {

                        SectionHeaderModel_()
                            .id("last_added_header")
                            .title(resources.getString(R.string.last_added))
                            .description(PreferenceUtil.lastAddedCutoffText)
                            .clickListener { _ -> NavigationUtil.goToSmartPlaylist(requireActivity(), SMART_PLAYLIST_LAST_ADDED_ID) }
                            .addIf(homeContent.lastAddedAlbums.isNotEmpty(), this)


                        carousel {
                            id("last_added")
                            numViewsToShowOnScreen(1.05f)
                            padding(Carousel.Padding(
                                resources.convertDpToPixels(20f).toInt(),
                                resources.convertDpToPixels(16f).toInt(),
                                resources.convertDpToPixels(20f).toInt(),
                                resources.convertDpToPixels(16f).toInt(),
                                resources.convertDpToPixels(16f).toInt()
                            ))
                            withModelsFrom(homeContent.lastAddedAlbums) {
                                LastAddedArtistModel_()
                                    .id(it.id)
                                    .artist(it)
                            }
                        }

                    }

                    if (homeContent.recentlyPlayed.isNotEmpty()) {

                        SectionHeaderModel_()
                            .id("recently_played_header")
                            .title(resources.getString(R.string.history))
                            .clickListener { _ -> NavigationUtil.goToSmartPlaylist(requireActivity(), SMART_PLAYLIST_HISTORY_ID) }
                            .addIf(homeContent.recentlyPlayed.isNotEmpty(), this)

                        carousel {
                            id("recently_played")
                            numViewsToShowOnScreen(3.8f)
                            padding(carouselPadding)
                            withModelsFrom(homeContent.recentlyPlayed) {
                                AlbumViewModel_()
                                    .id(it.id)
                                    .album(it)
                            }
                        }

                    }

                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == LAST_ADDED_CUTOFF) {
            libraryFragment.viewModel.forceLoad(HOME_CONTENT)
        }
    }

}