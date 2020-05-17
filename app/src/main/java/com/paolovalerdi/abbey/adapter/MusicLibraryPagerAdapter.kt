package com.paolovalerdi.abbey.adapter

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.fragment.app.*
import com.paolovalerdi.abbey.model.CategoryInfo
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.*
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import java.lang.ref.WeakReference
import java.util.*

class MusicLibraryPagerAdapter(
    private val mContext: Context,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private val fragments = SparseArray<WeakReference<Fragment>>()

    private val holderList = arrayListOf<Holder>()

    init {
        setCategories(PreferenceUtil.libraryCategories)
    }

    override fun getItemPosition(fragment: Any): Int {
        holderList.forEachIndexed { index, holder ->
            if (holder.mClassName == fragment.javaClass.name) {
                return index
            }
        }
        return POSITION_NONE
    }

   /* override fun getItemId(position: Int) = MusicFragments.of(
        getFragment(position).javaClass
    ).ordinal.toLong()*/


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mFragment = super.instantiateItem(container, position) as Fragment
        val mWeakFragment = fragments.get(position)
        mWeakFragment?.clear()
        fragments.put(position, WeakReference(mFragment))
        return mFragment
    }

    override fun getItem(position: Int): Fragment {
        val mCurrentHolder = holderList[position]
        return FragmentFactory.loadFragmentClass(
            mContext.classLoader,
            mCurrentHolder.mClassName
        ).newInstance()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        val mWeakFragment = fragments.get(position)
        mWeakFragment?.clear()
    }

    override fun getCount(): Int {
        return holderList.size
    }

    fun setCategories(categories: List<CategoryInfo>) {
        holderList.clear()

        categories.forEach { category ->
            if (category.visible) {
                val fragment = MusicFragments.valueOf(category.category.toString())
                val holder = Holder(
                    fragment.fragmentClass.name,
                    title = mContext.resources.getString(category.category.stringRes)
                )
                holderList.add(holder)
            }
        }

        alignCache()
        notifyDataSetChanged()
    }

    fun getFragment(position: Int): Fragment {
        val mWeakFragment = fragments.get(position)
        return mWeakFragment?.get() ?: getItem(position)
    }

    private fun alignCache() {
        if (fragments.size() == 0) return

        val mappings = HashMap<String, WeakReference<Fragment>>(fragments.size())
        fragments.forEach { _, fragment ->
            fragment.get()?.let {
                mappings[it.javaClass.name] = fragment
            }
        }
        holderList.forEachIndexed { index, holder ->
            val ref = mappings[holder.mClassName]
            if (ref != null) {
                fragments.put(index, ref)
            } else {
                fragments.remove(index)
            }
        }
    }

    enum class MusicFragments(
        val fragmentClass: Class<out Fragment>
    ) {

        HOME(HomeFragment::class.java),
        SONGS(SongsFragment::class.java),
        ALBUMS(AlbumsFragment::class.java),
        ARTISTS(ArtistsFragment::class.java),
        GENRES(GenresFragment::class.java),
        PLAYLISTS(PlaylistFragment::class.java);

        private object All {

            val FRAGMENTS = values()

        }

        companion object {

            fun of(cl: Class<*>): MusicFragments {
                All.FRAGMENTS.forEach { fragment ->
                    if (cl == fragment.fragmentClass) return fragment
                }
                throw IllegalArgumentException("Unknown music fragment $cl")
            }

        }

    }

    private data class Holder(
        val mClassName: String,
        val mParams: Bundle? = null,
        val title: String
    )

}