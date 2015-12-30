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

import android.content.Intent;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private int currentPage = 1;
    private Boolean currentlyFetchingMovies = true;

    private Boolean morePagesOfMoviesLeftToGet = true;
    private ArrayList<MoviesResponse.ResultsEntity> moviesResultsEntity;
    private MoviesAdapter moviesAdapter;

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

        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);

        GridView gridView = attachMoviesAdapterToGridView(rootView);
        setMovieItemClickListener(gridView);
        setUpMovieGridviewEndlessScrolling(gridView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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


    private void fetchMovies(final int page) {

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


}
