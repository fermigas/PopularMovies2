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
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MoviesFragment extends Fragment {

    private ArrayList<Movie> movieArray;
    private MovieAdapter movieAdapter;
    private int currentPage;
    private Boolean fetchingMore = true;
    private GridView gridView;
    private Boolean noMoreResults = false;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore state if it exists
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")){
            movieArray = new ArrayList<Movie>();
        }
        else {
            movieArray = savedInstanceState.getParcelableArrayList("movies");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate){
        outstate.putParcelableArrayList("movies", movieArray);
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

        movieAdapter =  new MovieAdapter( getActivity(), movieArray);
        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int last = firstVisibleItem + visibleItemCount;
                if((last == totalItemCount) && !fetchingMore && !noMoreResults){
                        TmdbApiParameters apiParams = new TmdbApiParameters(getActivity(), currentPage);
                        new FetchMoviesTask().execute(apiParams);
                        currentPage += 1;
                }
            }
        });
        return rootView;
    }

    public void updateMovies() {
        currentPage = 1;  // This is only called on startup, or when prefs change
        noMoreResults = false;  // New data set on startup and on prefs changing
        movieAdapter.clear();

        FetchMoviesTask moviesTask = new FetchMoviesTask();

        TmdbApiParameters apiParams = new TmdbApiParameters(getActivity(), currentPage);
        moviesTask.execute(apiParams);
        currentPage += 1;
    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    public class FetchMoviesTask extends AsyncTask<TmdbApiParameters, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String popMoviesJsonStr )
                throws JSONException {

            JSONObject popMoviesJson = new JSONObject(popMoviesJsonStr);
            JSONArray popMoviesArray = popMoviesJson.getJSONArray(getString(R.string.tmdb_results));

            if(popMoviesArray.length() < 20)
                noMoreResults = true;
            else
                noMoreResults = false;

            Movie[] movies = new Movie[popMoviesArray.length()];

            for(int i = 0; i < popMoviesArray.length(); i++) {
                JSONObject movieObject = popMoviesArray.getJSONObject(i);
                JSONArray  genreIDs = movieObject.getJSONArray(getString(R.string.tmdb_genre_ids));
                int[] idList = new int[genreIDs.length()];
                for (int j = 0; j < genreIDs.length(); j++) {
                    idList[j] = genreIDs.getInt(j);
                }
                movies[i] = new Movie(
                        idList,
                        movieObject.getString(getString(R.string.tmdb_adult)),
                        movieObject.getString(getString(R.string.tmdb_original_language)),
                        movieObject.getString(getString(R.string.tmdb_poster_path)),
                        movieObject.getString(getString(R.string.tmdb_overview)),
                        movieObject.getString(getString(R.string.tmdb_release_date)),
                        movieObject.getString(getString(R.string.tmdb_id)),
                        movieObject.getString(getString(R.string.tmdb_title)),
                        movieObject.getString(getString(R.string.tmdb_video)),
                        movieObject.getInt(getString(R.string.tmdb_vote_count)),
                        movieObject.getDouble(getString(R.string.tmdb_vote_average))

                );
            }


            return movies;
        }



        @Override
        protected Movie[] doInBackground(TmdbApiParameters... params) {


            fetchingMore = true;

            HttpURLConnection movieUrlConnection = null;
            BufferedReader movieReader = null;
            String popMoviesJsonStr = null;

            try {

                Uri builtMoviesUri =  params[0].buildMoviesUri();
                URL movieURL = new URL(builtMoviesUri.toString());

                movieUrlConnection = (HttpURLConnection) movieURL.openConnection();
                movieUrlConnection.setRequestMethod("GET");
                movieUrlConnection.connect();

                InputStream movieInputStream = movieUrlConnection.getInputStream();
                StringBuffer movieBuffer = new StringBuffer();
                if (movieInputStream == null)
                    return null;

                movieReader = new BufferedReader(new InputStreamReader(movieInputStream));
                String line;
                while ((line = movieReader.readLine()) != null)
                    movieBuffer.append(line + "\n");

                if (movieBuffer.length() == 0)
                    return null;  // Don't parse of there's no data

                popMoviesJsonStr = movieBuffer.toString();

                Log.v(LOG_TAG, "Movie Data string: " + popMoviesJsonStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, getString(R.string.log_error), e);
                return null;
            } finally {
                if (movieUrlConnection != null) {
                    movieUrlConnection.disconnect();
                }
                if (movieReader != null) {
                    try {
                        movieReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, getString(R.string.log_stream_close_error), e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(popMoviesJsonStr );
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                int currentPosition = gridView.getFirstVisiblePosition();
                for(Movie movieStr : result) {
                    movieAdapter.add(movieStr);
                }
                gridView.setSelection(currentPosition+1);
                fetchingMore = false;
            }
        }
    }

}
