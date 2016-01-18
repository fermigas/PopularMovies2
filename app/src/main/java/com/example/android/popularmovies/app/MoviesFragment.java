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
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import java.util.Set;

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private int currentPage = 1;
    private Boolean currentlyFetchingMovies = true;

    private Boolean morePagesOfMoviesLeftToGet = true;
//    private ArrayList<MoviesResponse.ResultsEntity> moviesResultsEntity;
//    private MoviesAdapter moviesAdapter;

    private MoviesCursorAdapter moviesCursorAdapter;
    SharedPreferences prefs;
    private View rootView;
    private Cursor mCursor;
    private GridView mGridView;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "*** Entering OnCreate()");


        // TODO  -- this only saves network data;  we need to save cache/favorites, too?
           // Maybe not, it's just grabbed from the database
//        if(savedInstanceState == null || !savedInstanceState.containsKey("movies"))
//            moviesResultsEntity = new ArrayList<MoviesResponse.ResultsEntity>();
//        else
//            moviesResultsEntity = savedInstanceState.getParcelableArrayList("movies");


        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outstate){
//        outstate.putParcelableArrayList("movies", moviesResultsEntity);
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

        Log.v(LOG_TAG, "*** Entering OnCreateView()");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);

        // TODO  MAke Sure these don't fail when there's no data nd no network
//        moviesAdapter =  new MoviesAdapter( getActivity(), moviesResultsEntity);

        Cursor cursor = getCursorWithCurrentPreferences();
        if(cursor != null)
            mCursor = cursor;
        moviesCursorAdapter = new MoviesCursorAdapter(getActivity(), cursor , 0);

        //   Then, attach whichever one the prefs designate
//        String dataSource = getDataSource();
//        if(dataSource.equals("network")) {
//            mGridView.setAdapter(moviesAdapter);
//        } else {
//            mGridView.setAdapter(moviesCursorAdapter);
//        }

        mGridView.setAdapter(moviesCursorAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(LOG_TAG, "*** Entering OnStart()");


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v(LOG_TAG, "*** Entering OnResume()");


        if(getDataSource().equals("network")) {
            getFirstPageOfMovies();
//            mGridView.setAdapter(moviesAdapter);
//            closeCursorIfNecessary(mCursor);
//            moviesCursorAdapter.swapCursor(getCursorWithCurrentPreferences());
        }

//        else {  //
            closeCursorIfNecessary(mCursor);
            moviesCursorAdapter.swapCursor(getCursorWithCurrentPreferences());
            mGridView.setAdapter(moviesCursorAdapter);
 //       }
        setMovieItemClickListener(mGridView);
        setUpMovieGridviewEndlessScrolling(mGridView);

    }


    @Override
    public void onPause() {
        super.onPause();
        closeCursorIfNecessary(mCursor);

        Log.v(LOG_TAG, "*** In OnPause()");
    }


    public void getFirstPageOfMovies() {
        morePagesOfMoviesLeftToGet = true;  // New data set on startup and on prefs changing
//        moviesAdapter.clear();  // Must do this when prefs change

        currentPage = 1;        // This is only called on startup, or when prefs change
        fetchMovies(currentPage);
        currentPage += 1;

    }


    private Cursor getCursorWithCurrentPreferences() {

        Uri uri = getUriFromPreferences();

        Cursor cursor;

        try {

            cursor = getActivity().getContentResolver().query(
                    uri, null, null, null, null);

        String cursorContents = DatabaseUtils.dumpCursorToString(cursor);
        Log.v(LOG_TAG, cursorContents);
        }
        catch ( Exception e ){
            return null;
        }

        return cursor;
    }

    private void closeCursorIfNecessary(Cursor cursor) {
        if(cursor != null && !cursor.isClosed() )
            cursor.close();
    }

    private Uri getUriFromPreferences() {

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String[] keys = {"data_source", "vote_count", "time_period", "genre_ids", "sort_order"};

        String dataSource = prefs.getString(getActivity().getString(R.string.pref_data_source_key), "network");
        String voteCount = prefs.getString(getActivity().getString(R.string.pref_vote_count_key), "0");
        String timePeriods = prefs.getString(getActivity().getString(R.string.pref_period_key), "all");
        String genreIds = getGenresAsCommaSeparatedNumbers();
        String sortOrder = prefs.getString(getActivity().getString(R.string.pref_sort_order_key), "none");

        String[] values = {  dataSource, voteCount, timePeriods, genreIds, sortOrder };


        Uri uri = MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                MovieEntry.CONTENT_URI, keys, values
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
                if(getDataSource().equals("network"))
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

        MoviesResponse.ResultsEntity movie;
//        if(getDataSource().equals("network")) {
//            movie = moviesAdapter.getItem(position);
//        }
//        else {
            movie = getMovieFromCursor(position);
//        }
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    private MoviesResponse.ResultsEntity getMovieFromCursor(int position) {

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


//        cursor.close();   // Nope, still in use.


        return movie;
    }


    public ArrayList<Integer> getGenreIdsAsInegerArrayList(String genreIds) {

        if(genreIds != null) {
            String[] strArray = genreIds
                    .replace("[", "").replace("]", "").replaceAll("\\s+","")
                    .split(",");
            ArrayList<Integer> intArrayList = new ArrayList<Integer>(strArray.length);
            for (String i : strArray) {
                intArrayList.add( Integer.parseInt(i));
            }

            return intArrayList;
        }
        else
            return null;
    }


    private boolean shouldWeFetchMoreMovies(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int last = firstVisibleItem + visibleItemCount;

        if(currentPage > 1 &&
                visibleItemCount != 0
                && ( (visibleItemCount + firstVisibleItem) == totalItemCount
                && totalItemCount == last
                && last == visibleItemCount ))
            return false;


        Log.v(LOG_TAG,
                "firstVisibleItem: " + firstVisibleItem + ",  " +
                "visibleItemCount: " + visibleItemCount + ",  " +
                "totalItemCount: " + totalItemCount + ",  " +
                "last: " + last + ",  "
                );


        return (last == totalItemCount) &&   // We've scrolled past the end of the grid view
                !currentlyFetchingMovies &&        // We're not trying to grab movies already with
                                                   // another HTTPAsync client
                morePagesOfMoviesLeftToGet;  // We're not out of pages of movies yet

    }

    private void fetchMovies(int currentPage) {

        fetchMoviesFromWeb(currentPage);
//        insertOrUpdateMovies();

    }

    @NonNull
    private String getDataSource() {
        return prefs.getString(
                getActivity().getString(R.string.pref_data_source_key), "network");
    }

//    private void insertOrUpdateMovies() {
//        if(!moviesResultsEntity.isEmpty())
//            for (MoviesResponse.ResultsEntity mr : moviesResultsEntity) {
//                if(!isMovieAlreadyInDb(mr))
//                    addMovieToDb(mr);
//                else
//                    updateMovie(mr);
//            }
//    }

    private void updateMovie(MoviesResponse.ResultsEntity mr) {

        // These values change at least daily, especially for popular movies
        final ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_POPULARITY, mr.getPopularity());
        movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, mr.getVote_count());
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, mr.getVote_average());

        String selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(mr.getId())};

        getContext().getContentResolver().update(
                MovieEntry.CONTENT_URI,
                movieValues,
                selection, selectionArgs
        );

    }

    private boolean isMovieAlreadyInDb(MoviesResponse.ResultsEntity mr) {

        Cursor cursor = null;

        try {

            String selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(mr.getId())};

            cursor = getContext().getContentResolver().query(
                    MovieEntry.CONTENT_URI,
                    null, selection, selectionArgs, null
            );

            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                if (mr.getId() == cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)))
                    return true;
            }
         }
        catch (Exception e) {
            Log.v(LOG_TAG, " ***  Failed getting cursor when checking to see " +
                    "if the movies is already in the database." );
        }
        finally {
            if(cursor != null) {
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
                // TODO Remove movieAdapter:
                // addMoviesToAdapter(getMoviesResponse(responseBody));
                int numberOfMovies = insertOrUpdateMovies(getMoviesResponse(responseBody));
                // Don't allow more grid view scrolling when we're out of results
                morePagesOfMoviesLeftToGet = numberOfMovies >= 20;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });

    }

//    private void addMoviesToAdapter(MoviesResponse moviesResponse) {
//        if(!moviesResponse.getResults().isEmpty()) {
//            for (MoviesResponse.ResultsEntity  result : moviesResponse.getResults() )
//                moviesAdapter.add(result);
//            currentlyFetchingMovies = false;  // This Async process is done, we can get more now if needed
//        }
//    }

    private int insertOrUpdateMovies(MoviesResponse moviesResponse) {
        if(!moviesResponse.getResults().isEmpty())
            for (MoviesResponse.ResultsEntity mr : moviesResponse.getResults()) {
                if(!isMovieAlreadyInDb(mr))
                    addMovieToDb(mr);
                else
                    updateMovie(mr);
            }
        return moviesResponse.getResults().size();
    }


    private MoviesResponse getMoviesResponse(byte[] responseBody) {
        String responsestr = new String(responseBody);
        Gson gson = new Gson();
        return gson.fromJson(responsestr, MoviesResponse.class);
    }

    private void addMovieToDb(final MoviesResponse.ResultsEntity mr){

        final ContentValues movieValues = new ContentValues();

        String fullPosterPathUrl =
                getContext().getString(R.string.tmdb_base_image_url) + getContext().getString(R.string.tmdb_image_size_185) + mr.getPoster_path();

        // final Target target;
        Picasso.with(getActivity())
                .load(fullPosterPathUrl)
                .into(new Target() {

                @Override
                public void onBitmapLoaded (Bitmap bitmap, Picasso.LoadedFrom from){
                    byte[] imageBytes = getBitmapAsByteArray(bitmap);
                    insertMovieIntoDB(imageBytes, movieValues, mr);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    Log.v(LOG_TAG, "*** Failed to Download Bitmap");

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) { }
            }
            );

    }

    private void insertMovieIntoDB(byte[] imageBytes, ContentValues movieValues, MoviesResponse.ResultsEntity mr) {

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

        movieValues.put(MovieEntry.COLUMN_POSTER_BITMAP, imageBytes);

        getContext().getContentResolver().insert(
                MovieEntry.CONTENT_URI, movieValues);

//        Log.v(LOG_TAG, "*** Downloaded Bitmap Successfully and inserted it");
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }

}
