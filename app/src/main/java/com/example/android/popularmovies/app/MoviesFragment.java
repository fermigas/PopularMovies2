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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;

public class MoviesFragment extends Fragment {


    private int currentPage = 1;
    private Boolean fetchingMore = true;
    private Boolean noMoreResults = false;

    private GridView gridView;
    private ArrayList<MoviesResponse.ResultsEntity> moviesResultsEntity;


    private MoviesCustomAdapter moviesCustomAdapter;
    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(savedInstanceState == null || !savedInstanceState.containsKey("movies"))
//            movieArray = new ArrayList<Movie>();
            moviesResultsEntity = new ArrayList<MoviesResponse.ResultsEntity>();
//        else
//            movieArray = savedInstanceState.getParcelableArrayList("movies");

        setHasOptionsMenu(true);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outstate){
//        outstate.putParcelableArrayList("movies", movieArray);
//        super.onSaveInstanceState(outstate);
//    }

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

//        movieAdapter =  new MovieAdapter( getActivity(), movieArray);
        moviesCustomAdapter =  new MoviesCustomAdapter( getActivity(), moviesResultsEntity);

        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview_movie);

        // gridView.setAdapter(movieAdapter);
        gridView.setAdapter(moviesCustomAdapter);

        // ****  TODO  fix intent & parcelable code    ***/
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Movie movie = movieAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
//                intent.putExtra("movie", movie);
//                startActivity(intent);
//            }
//        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int last = firstVisibleItem + visibleItemCount;
                if((last == totalItemCount) && !fetchingMore   && !noMoreResults  ){
                    showMovies(currentPage);
                    currentPage += 1;
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    public void updateMovies() {
        currentPage = 1;  // This is only called on startup, or when prefs change
        noMoreResults = false;  // New data set on startup and on prefs changing
        moviesCustomAdapter.clear();

        showMovies(currentPage);

        currentPage += 1;

    }


    private void showMovies(final int thisPage) {

        TmdbApiParameters apiParams = new TmdbApiParameters(getActivity(), thisPage);
        String url = apiParams.buildMoviesUri().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        fetchingMore = true;
        client.get(getActivity(), url, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responsestr = new String(responseBody);
                Gson gson = new Gson();
                MoviesResponse moviesResponse = gson.fromJson(responsestr, MoviesResponse.class);
                if(!moviesResponse.getResults().isEmpty()) {
                    for (MoviesResponse.ResultsEntity  result : moviesResponse.getResults() )
                        moviesCustomAdapter.add(result);
                    fetchingMore = false;
                }

                if(moviesCustomAdapter.getCount() < 20)
                    noMoreResults = true;
                else
                    noMoreResults = false;
                Log.v(LOG_TAG, "Movie Data string: " + moviesResponse.getResults().toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }


}
