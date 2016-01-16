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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.example.android.popularmovies.app.MoviesContract.MovieEntry;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Set;

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private int currentPage = 1;
    private Boolean currentlyFetchingMovies = true;

    private Boolean morePagesOfMoviesLeftToGet = true;
    private ArrayList<MoviesResponse.ResultsEntity> moviesResultsEntity;
    private MoviesAdapter moviesAdapter;

    private MoviesCursorAdapter moviesCursorAdapter;
    SharedPreferences prefs;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies"))
            moviesResultsEntity = new ArrayList<MoviesResponse.ResultsEntity>();
        else
            moviesResultsEntity = savedInstanceState.getParcelableArrayList("movies");


        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate){
        outstate.putParcelableArrayList("movies", moviesResultsEntity);
        super.onSaveInstanceState(outstate);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);

        String dataSource = getDataSource();
        if(dataSource.equals("network")) {
            GridView gridView = attachMoviesAdapterToGridView(rootView);
            setMovieItemClickListener(gridView);
            setUpMovieGridviewEndlessScrolling(gridView);
        } else {
            GridView gridView = attachMoviesCursorAdapterToGridView(rootView);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDataSource().equals("network"))
            getFirstPageOfMovies();
    }


    public void getFirstPageOfMovies() {
        morePagesOfMoviesLeftToGet = true;  // New data set on startup and on prefs changing
        moviesAdapter.clear();  // Must do this when prefs change

        currentPage = 1;        // This is only called on startup, or when prefs change
        fetchMovies(currentPage);
        currentPage += 1;

    }

    @NonNull
    private GridView attachMoviesAdapterToGridView(View rootView) {
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        moviesAdapter =  new MoviesAdapter( getActivity(), moviesResultsEntity);
        gridView.setAdapter(moviesAdapter);
        return gridView;
    }

    private GridView attachMoviesCursorAdapterToGridView(View rootView) {

        /* Form Uri from current prefs */
        Uri uri = getUriFromPreferences();

        /* Get cursor from Uri   */
        Cursor cursor = getActivity().getContentResolver().query(
                uri,
                null, null, null, null );

        /* Create new MovieCursorAdapter, passing it the cursor */
        moviesCursorAdapter = new MoviesCursorAdapter(getActivity(), cursor, 0);
        /* Attach  the gridview to the  new MovieCursorAdapter */

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        moviesAdapter =  new MoviesAdapter( getActivity(), moviesResultsEntity);
        gridView.setAdapter(moviesCursorAdapter);
        return gridView;
    }

    private Uri getUriFromPreferences() {

        String[] keys = {"data_source", "vote_count", "time_period", "genre_ids", "sort_order"};

        String dataSource = prefs.getString(getActivity().getString(R.string.pref_data_source_key), "network");
        String voteCount = prefs.getString(getActivity().getString(R.string.pref_vote_count_key), "0");
        String timePeriods = prefs.getString(getActivity().getString(R.string.pref_period_key), "all");
        String genreIds = getGenresAsCommaSeparatedNumbers();
                // prefs.getString(getActivity().getString(R.string.pref_genre_ids_key), "");
        String sortOrder = prefs.getString(getActivity().getString(R.string.pref_sort_order_key), "none");

        String[] values = {
             dataSource, voteCount, timePeriods, genreIds, sortOrder
        };


        Uri uri = MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                MovieEntry.CONTENT_URI,
                keys,
                values
        );

        return uri;
    }

    private String getGenresAsCommaSeparatedNumbers() {

        String genres;
        Set<String> genresSet = prefs.getStringSet("genre_ids", null);
        if ( genresSet != null && !genresSet.isEmpty()  )
            genres = genresSet.toString().replaceAll("\\s+", "").replace("[", "").replace("]", "");
        else
            genres = "";

        return genres;
    }

    private void setMovieItemClickListener(GridView gridView) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startMovieDetailsActivity(position);
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
                getAnotherPageOfMoviesIfNeeded(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    private void getAnotherPageOfMoviesIfNeeded(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if(shouldWeFetchMoreMovies(firstVisibleItem, visibleItemCount, totalItemCount)){
            fetchMovies(currentPage);
            currentPage += 1;
        }
    }

    private void startMovieDetailsActivity(int position) {
        MoviesResponse.ResultsEntity movie = moviesAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    private boolean shouldWeFetchMoreMovies(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int last = firstVisibleItem + visibleItemCount;

//        Log.v(LOG_TAG,
//                "firstVisibleItem: " + firstVisibleItem + ",  " +
//                "visibleItemCount: " + visibleItemCount + ",  " +
//                "totalItemCount: " + totalItemCount + ",  " +
//                "last: " + last + ",  "
//                );
//

        return (last == totalItemCount) &&   // We've scrolled past the end of the grid view
                !currentlyFetchingMovies &&        // We're not trying to grab movies already with
                                                   // another HTTPAsync client
                morePagesOfMoviesLeftToGet;  // We're not out of pages of movies yet

    }

    private void fetchMovies(int currentPage) {

        fetchMoviesFromWeb(currentPage);
        stashMoviesInDatabase();

    }

    @NonNull
    private String getDataSource() {
        return prefs.getString(
                getActivity().getString(R.string.pref_data_source_key), "network");
    }

    private void stashMoviesInDatabase() {
        if(!moviesResultsEntity.isEmpty())
            for (MoviesResponse.ResultsEntity mr : moviesResultsEntity)
                addMovieToDb(mr);
    }


    private void fetchMoviesFromWeb(final int page) {

        TmdbApiParameters apiParams = new TmdbApiParameters(getActivity(), page);
        String url = apiParams.buildMoviesUri().toString();

        currentlyFetchingMovies = true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                addMoviesToAdapter(getMoviesResponse(responseBody));
                // Don't allow more grid view scrolling when we're out of results
                morePagesOfMoviesLeftToGet = moviesAdapter.getCount() >= 20;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });

    }

    private void addMoviesToAdapter(MoviesResponse moviesResponse) {
        if(!moviesResponse.getResults().isEmpty()) {
            for (MoviesResponse.ResultsEntity  result : moviesResponse.getResults() )
                moviesAdapter.add(result);
            currentlyFetchingMovies = false;  // This Async process is done, we can get more now if needed
        }
    }

    private MoviesResponse getMoviesResponse(byte[] responseBody) {
        String responsestr = new String(responseBody);
        Gson gson = new Gson();
        return gson.fromJson(responsestr, MoviesResponse.class);
    }

    private void addMovieToDb(MoviesResponse.ResultsEntity mr){

        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, mr.getPoster_path() );
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

        getContext().getContentResolver().insert(
                MovieEntry.CONTENT_URI, movieValues
        );


        return;
    }

}
