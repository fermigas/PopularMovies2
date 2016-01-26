/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.app.MoviesContract.MovieEntry;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private int currentPage = 1;
    private Boolean currentlyFetchingMovies = true;
    private Boolean morePagesOfMoviesLeftToGet = true;
    private MoviesCursorAdapter moviesCursorAdapter;
    SharedPreferences prefs = null;
    int savedPosition = 0;
    private View rootView;
    private GridView mGridView;
    private MovieReviewsResponse reviewsResponse;
    final Set<Target> targetHashSet = new HashSet<>();
    private boolean mPrefsChangedOrAppStarted = false;
    private Map<String, ?> oldPrefs;
    private String savedPrefs;
    private boolean mAppJustStarted = false;
    private boolean mJustRotated = false;
    private String mState = "";

    public interface Callback {
        public void onItemSelected(MoviesResponse.ResultsEntity movie, boolean favoriteState);
    }

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);


        oldPrefs = prefs.getAll();

        outstate.putString("saved_prefs", oldPrefs.toString());
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "***  Entering onCreate()");


        prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);


        Cursor cursor = getCursorWithCurrentPreferences();
        moviesCursorAdapter = new MoviesCursorAdapter(getActivity(), cursor, 0);
        mGridView.setAdapter(moviesCursorAdapter);
        setMovieItemClickListener(mGridView);
        setUpMovieGridviewEndlessScrolling(mGridView);
        return rootView;
    }


    @Override
    public void onResume() {
            super.onResume();

        Log.v(LOG_TAG, "***  Entering onResume()");


            if (getDataSource().equals("network")) {
                initializeMoviesGridFromNetwork();
            } else {  // Cache  or Favorites
                initializeMoviesGridFromCache();
            }

        }

    private void initializeMoviesGridFromCache() {

        switch (mState){

            case "app_just_started":
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, App Just Started, reatached, set first position");
                break;

            case "rotated":  // Do nothing; no new selection
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, rotated, did nothing");
                break;

            case "prefs_changed":
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, prefs changed, reatached, set first position");
                break;

            case "prefs_didnt_change":  // Do nothing; no new selection
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, prefs didn't change, did nothing.");
                break;

        }

    }

    private void initializeMoviesGridFromNetwork() {

        switch (mState){

            case "app_just_started":
                getFirstPageOfMovies();  // This will select the first movie
                Log.v(LOG_TAG, "***  Network, App Just Started, Got First Page of Movies");
                break;

            case "rotated":  // Do nothing; no new selection
                getFirstPageOfMovies(); // This will select the first movie
                Log.v(LOG_TAG, "***  Network, rotated, did nothing");
                break;

            case "prefs_changed":
                getFirstPageOfMovies(); // This will select the first movie
                Log.v(LOG_TAG, "***  Network, prefs changed, Got First Page of Movies.");
                break;

            case "prefs_didnt_change":  // Do nothing; no new selection
                getFirstPageOfMovies(); // This will select the first movie
                Log.v(LOG_TAG, "***  Network, prefs didn't change, did nothing.");
                break;

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v(LOG_TAG, "***  Entering onActivityCreated() ");

        // Did the App just start?
        if(didAppJustStart(savedInstanceState)) {
            mState = "app_just_started";

            oldPrefs = prefs.getAll();

        } else { // Ok, we just rotated
            mState = "rotated";
            savedPrefs = savedInstanceState.getString("saved_prefs");
            Log.v(LOG_TAG, "***  onActivityCreated().  savedPrefs ==   " + savedPrefs);
        }

    }

    private boolean didAppJustStart(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return true;
        else return false;
    }

    public void getFirstPageOfMovies() {
        morePagesOfMoviesLeftToGet = true;  // New data set on startup and on prefs changing
        currentPage = 1;        // This is only called on startup, or when prefs change
        fetchMoviesFromWeb(currentPage);
        currentPage += 1;
    }

    // This should only be called if prefs have changed or when the program opens

    private void reattachGridViewCursorAdapter() {
        Cursor cursor = getCursorWithCurrentPreferences();
        if (cursor != null) {
            cursor.moveToLast();
            cursor.moveToFirst();
        }
        moviesCursorAdapter.changeCursor(cursor);
        setMovieItemClickListener(mGridView);
        setUpMovieGridviewEndlessScrolling(mGridView);
        currentlyFetchingMovies = false;
    }

    private void selectFirstMovieToFillOutDetail() {

        if (mGridView.getNumColumns() == -1)
            return;

        if(mGridView.getNumColumns() % 2 == 0)  // Tablets use odd #s
            return;  // Don't select unless we're using master/detail

        Cursor cursor = moviesCursorAdapter.getCursor();
        cursor.moveToFirst();

        MoviesResponse.ResultsEntity movie = getMovieFromCursor();
        boolean favoriteState = getFavoritesStateFromCursor();
        mGridView.smoothScrollToPosition(0);
        mGridView.setSelection(0);
        ((Callback) getActivity()).onItemSelected(movie,  favoriteState);
    }

    private Cursor getCursorWithCurrentPreferences() {

        Uri uri = getUriFromPreferences();
        Cursor cursor;
        try {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToLast();
//                String cursorContents = DatabaseUtils.dumpCurrentRowToString(cursor);
//                Log.v(LOG_TAG, cursorContents);
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            return null;
        }

        return cursor;
    }


    private Uri getUriFromPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String[] keys = {"data_source", "vote_count", "time_period", "genre_ids", "sort_order"};

        String dataSource = preferences.getString(getActivity().getString(R.string.pref_data_source_key), "network");
        String voteCount = preferences.getString(getActivity().getString(R.string.pref_vote_count_key), "0");
        String timePeriods = preferences.getString(getActivity().getString(R.string.pref_period_key), "all");
        String genreIds = getGenresAsCommaSeparatedNumbers(preferences);
        String sortOrder = preferences.getString(getActivity().getString(R.string.pref_sort_order_key), "none");

        String[] values = {dataSource, voteCount, timePeriods, genreIds, sortOrder};

        Uri uri = MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                MovieEntry.CONTENT_URI, keys, values
        );

        return uri;
    }

    private String getGenresAsCommaSeparatedNumbers(SharedPreferences preferences) {

        String genres;
        Set<String> genresSet = preferences.getStringSet("genre_ids", null);
        if (genresSet != null && !genresSet.isEmpty())
            genres = genresSet.toString().replaceAll("\\s+", "").replace("[", "").replace("]", "");
        else
            genres = "";

        return genres;
    }

    private void setMovieItemClickListener(GridView gridView) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(currentlyFetchingMovies)
                    return;

                savedPosition = position;
                ((Callback) getActivity())
                        .onItemSelected(getMovieFromCursor(),
                                getFavoritesStateFromCursor());
            }
        });
    }

    private void setUpMovieGridviewEndlessScrolling(GridView gridView) {
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (getDataSource().equals("network"))
                    getAnotherPageOfMoviesIfNeeded(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    private void getAnotherPageOfMoviesIfNeeded(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (shouldWeFetchMoreMovies(firstVisibleItem, visibleItemCount, totalItemCount)) {
            fetchMoviesFromWeb(currentPage);
            currentPage += 1;
        }
    }

    private boolean shouldWeFetchMoreMovies(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int last = firstVisibleItem + visibleItemCount;

        if (currentPage > 1 &&
                visibleItemCount != 0
                && ((visibleItemCount + firstVisibleItem) == totalItemCount
                && totalItemCount == last
                && last == visibleItemCount))
            return false;

        return (last == totalItemCount) &&   // We've scrolled past the end of the grid view
                !currentlyFetchingMovies &&        // We're not trying to grab movies already with
                // another HTTPAsync client
                morePagesOfMoviesLeftToGet;  // We're not out of pages of movies yet

    }


    private MoviesResponse.ResultsEntity getMovieFromCursor() {

        Cursor cursor = moviesCursorAdapter.getCursor();

        MoviesResponse.ResultsEntity movie = new MoviesResponse.ResultsEntity(
                false,  // adult
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP_PATH)),
                getGenreIdsAsInegerArrayList(
                        cursor.getString(cursor.getColumnIndex(
                                MovieEntry.COLUMN_GENRE_IDS))),
                cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_LANGUAGE)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)),
                cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_POPULARITY)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE)),
                true, // video
                cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)),
                cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT))
        );

        return movie;
    }

    private boolean getFavoritesStateFromCursor() {

        Cursor cursor = moviesCursorAdapter.getCursor();

        return ( (cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE)) == 1)
                ? true : false );
    }

    public ArrayList<Integer> getGenreIdsAsInegerArrayList(String genreIds) {

        if (genreIds != null) {
            String[] strArray = genreIds
                    .replace("[", "").replace("]", "").replaceAll("\\s+", "")
                    .split(",");
            ArrayList<Integer> intArrayList = new ArrayList<Integer>(strArray.length);
            for (String i : strArray) {
                if (i != null && !i.isEmpty())
                    intArrayList.add(Integer.parseInt(i));
            }

            return intArrayList;
        } else
            return null;
    }

    @NonNull
    private String getDataSource() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return preferences.getString(
                getActivity().getString(R.string.pref_data_source_key), "network");
    }


    private void updateMovie(MoviesResponse.ResultsEntity mr, boolean isLastResult) {

        // These values change at least daily, especially for popular movies
        final ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_POPULARITY, mr.getPopularity());
        movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, mr.getVote_count());
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, mr.getVote_average());

        String selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(mr.getId())};

        getActivity().getContentResolver().update(
                MovieEntry.CONTENT_URI,
                movieValues,
                selection, selectionArgs
        );

        ifFullPageOfMoviesHasBeenInsertedOrUpdatedChangeCursor(isLastResult);

    }

    private boolean isMovieAlreadyInDb(MoviesResponse.ResultsEntity mr) {

        Cursor cursor = null;

        try {

            String selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(mr.getId())};

            cursor = getActivity().getContentResolver().query(
                    MovieEntry.CONTENT_URI,
                    null, selection, selectionArgs, null
            );

            if (cursor != null && cursor.getCount() == 1) {
                return true;
            } else {
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }


    private void fetchMoviesFromWeb(final int page) {

        TmdbApiParameters apiParams = new TmdbApiParameters(getActivity(), page);
        String url = apiParams.buildMoviesUri().toString();

        currentlyFetchingMovies = true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int numberOfMovies = insertOrUpdateMovies(getMoviesResponse(responseBody));
                // Don't allow more grid view scrolling when we're out of results
                morePagesOfMoviesLeftToGet = numberOfMovies >= 20;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });

    }


    private int insertOrUpdateMovies(MoviesResponse moviesResponse) {
        int totalResults = 0;  // Assume no results left to get
        if (!moviesResponse.getResults().isEmpty()) {
            totalResults = moviesResponse.getResults().size();
            int resultsProcessed = 0;
            for (MoviesResponse.ResultsEntity mr : moviesResponse.getResults()) {
                resultsProcessed++;
                if (isMovieAlreadyInDb(mr)) {
                    updateMovie(mr, resultsProcessed == totalResults);
                } else {
                    addMovieToDb(mr, resultsProcessed == totalResults);
                }
            }
        }

        return totalResults;
    }


    private MoviesResponse getMoviesResponse(byte[] responseBody) {
        String responsestr = new String(responseBody);
        Gson gson = new Gson();
        return gson.fromJson(responsestr, MoviesResponse.class);
    }

    private void addMovieToDb(final MoviesResponse.ResultsEntity mr, boolean isLastResult) {

        String fullPosterPathUrl =
                getActivity().getString(R.string.tmdb_base_image_url) +
                        getActivity().getString(R.string.tmdb_image_size_185) +
                        mr.getPoster_path();

        Target bitmapTarget = new BitmapTarget(mr, isLastResult);
        targetHashSet.add(bitmapTarget);
        Picasso.with(getActivity()).load(fullPosterPathUrl).into(bitmapTarget);

    }


    class BitmapTarget implements Target {

        MoviesResponse.ResultsEntity mMr;
        boolean mIsLastResult;

        public BitmapTarget(MoviesResponse.ResultsEntity mMr, boolean isLastResult) {
            this.mMr = mMr;
            this.mIsLastResult = isLastResult;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {

            byte[] imageBytes = getBitmapAsByteArray(bitmap);
            insertMovieIntoDB(imageBytes, mMr);
            ifFullPageOfMoviesHasBeenInsertedOrUpdatedChangeCursor(mIsLastResult);
            targetHashSet.remove(this);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            targetHashSet.remove(this);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
        }
    }

    private void ifFullPageOfMoviesHasBeenInsertedOrUpdatedChangeCursor(boolean isLastResult) {

        if (isLastResult) { // Change cursor after last result from a page has been downloaded
            reattachGridViewCursorAdapter();
            selectFirstMovieToFillOutDetail();

        }

    }

    private void insertMovieIntoDB(byte[] imageBytes, MoviesResponse.ResultsEntity mr) {

        final ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, mr.getPoster_path());
        movieValues.put(MovieEntry.COLUMN_ADULT, mr.isAdult());
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, mr.getOverview());
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, mr.getRelease_date());
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, mr.getId());
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, mr.getOriginal_title());
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, mr.getOriginal_language());
        movieValues.put(MovieEntry.COLUMN_TITLE, mr.getTitle());
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, mr.getBackdrop_path());
        movieValues.put(MovieEntry.COLUMN_POPULARITY, mr.getPopularity());
        movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, mr.getVote_count());
        movieValues.put(MovieEntry.COLUMN_VIDEO, mr.isVideo());
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, mr.getVote_average());
        movieValues.put(MovieEntry.COLUMN_GENRE_IDS, mr.getGenre_ids().toString());
        /*  These are initial values, all set to zero  */
        movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);
        movieValues.put(MovieEntry.COLUMN_WATCHED, 0);
        movieValues.put(MovieEntry.COLUMN_WATCH_ME, 0);
        movieValues.put(MovieEntry.COLUMN_POSTER_BITMAP, imageBytes);

        getActivity().getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);

    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }


}



