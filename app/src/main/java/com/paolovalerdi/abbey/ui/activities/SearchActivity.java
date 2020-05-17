package com.paolovalerdi.abbey.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.adapter.SearchAdapter;
import com.paolovalerdi.abbey.interfaces.LoaderIds;
import com.paolovalerdi.abbey.misc.WrappedAsyncTaskLoader;
import com.paolovalerdi.abbey.repository.AlbumRepository;
import com.paolovalerdi.abbey.repository.ArtistRepository;
import com.paolovalerdi.abbey.repository.SongRepository;
import com.paolovalerdi.abbey.ui.activities.base.AbsMusicServiceActivity;
import com.paolovalerdi.abbey.util.Util;
import com.paolovalerdi.abbey.views.StatusBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AbsMusicServiceActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<Object>> {

    public static final String QUERY = "query";
    private static final int LOADER_ID = LoaderIds.SEARCH_ACTIVITY;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.empty)
    ConstraintLayout empty;
    @BindView(R.id.status_bar)
    StatusBarView statusBarView;

    SearchView searchView;

    private SearchAdapter adapter;
    private String query;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        allowDrawUnderStatusBar();
        setLightNavigationBar(ColorUtil.isColorLight(ThemeStore.primaryColor(this)));
        ButterKnife.bind(this);

        setLightStatusBar(ColorUtil.isColorLight(ThemeStore.primaryColor(this)));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this, Collections.emptyList());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                empty.setVisibility(adapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });

        setUpToolBar();

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY);
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY, query);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchItem.expandActionView();
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return false;
            }
        });

        searchView.setQuery(query, false);
        searchView.post(() -> searchView.setOnQueryTextListener(SearchActivity.this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(@NonNull String query) {
        this.query = query;
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onMediaStoreChanged() {
        super.onMediaStoreChanged();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        hideSoftKeyboard();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return false;
    }

    private void hideSoftKeyboard() {
        Util.hideSoftKeyboard(SearchActivity.this);
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        return new AsyncSearchResultLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        adapter.swapDataSet(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        adapter.swapDataSet(Collections.emptyList());
    }

    private static class AsyncSearchResultLoader extends WrappedAsyncTaskLoader<List<Object>> {
        private final String query;

        public AsyncSearchResultLoader(Context context, String query) {
            super(context);
            this.query = query;
        }

        @Override
        public List<Object> loadInBackground() {
            List<Object> results = new ArrayList<>();
            if (!TextUtils.isEmpty(query)) {
                List songs = SongRepository.getSongs(getContext(), query.trim());
                if (!songs.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.songs));
                    results.addAll(songs);
                }

                List artists = ArtistRepository.getArtists(getContext(), query.trim());
                if (!artists.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.artists));
                    results.addAll(artists);
                }

                List albums = AlbumRepository.getAlbums(getContext(), query.trim());
                if (!albums.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.albums));
                    results.addAll(albums);
                }
            }
            return results;
        }
    }
}
