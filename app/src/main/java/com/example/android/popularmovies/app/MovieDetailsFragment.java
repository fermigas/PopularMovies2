package com.example.android.popularmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

// TODO Organize movie details fragment layout to support
//         App Bar
//         Favorites Button
//         Shrunken Overview
//         Trailers section
//         Reviews Section
// TODO Add setting to chose number of movie columns shown (1,2,3,4)?  Tablet could be a problem

public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private Movie movie;
    private MovieTrailersResponse trailersResponse;
    private MovieTrailersCustomAdapter movieTrailersCustomAdapter;
    private ListView trailersListView;
    private ListView reviewsListView;
    private MovieReviewsResponse reviewsResponse;
    private MovieReviewsCustomAdapter movieReviewsCustomAdapter;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.details_fragment, container, false);

        getMovieFromParcelableExtra();

        showMovieData(rootView);
        showMovieTrailers(rootView);
        showMovieReviews(rootView);

        return rootView;
    }


    private void showMovieTrailers(View rootView) {

        String url = getMovieTrailersUrl(movie.id);
        trailersListView = (ListView) rootView.findViewById(R.id.listview_movie_trailer);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responsestr = new String(responseBody);
                Gson gson = new Gson();
                trailersResponse = gson.fromJson(responsestr, MovieTrailersResponse.class);
                movieTrailersCustomAdapter = new MovieTrailersCustomAdapter(getActivity(), trailersResponse.getYoutube() );
                trailersListView.setAdapter(movieTrailersCustomAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private void showMovieReviews(View rootView) {

        String url = getMovieReviewsUrl(movie.id);
        reviewsListView = (ListView) rootView.findViewById(R.id.listview_movie_review);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responsestr = new String(responseBody);
                Gson gson = new Gson();
                reviewsResponse = gson.fromJson(responsestr, MovieReviewsResponse.class);
                movieReviewsCustomAdapter = new MovieReviewsCustomAdapter(getActivity(), reviewsResponse.getResults() );
                reviewsListView.setAdapter(movieReviewsCustomAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }


    @NonNull
    private String getMovieReviewsUrl(String movieId)  {

        Uri uri = Uri.parse(getMovieReviewsBaseURL(movieId));
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        uri = builder.build();
        return uri.toString();
    }

    private String getMovieTrailersUrl(String movieId)  {

        Uri uri = Uri.parse(getMovieTrailersBaseURL(movieId));
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        uri = builder.build();
        return uri.toString();
    }

    private String getMovieReviewsBaseURL(String movieID){
        return "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?";
    }

    private String getMovieTrailersBaseURL(String movieID){
        return "http://api.themoviedb.org/3/movie/" + movieID + "/trailers?";
    }

    private void getMovieFromParcelableExtra() {

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie"))
            movie = intent.getParcelableExtra("movie");

    }

    private void showMovieData(View rootView) {

        if (movie != null )
            showMovieDetails(rootView);

    }

    private void showMovieDetails(View rootView) {
        showMoviePoster(rootView);
        showMovieTitle(rootView);
        showScrollingMovieOverview(rootView);
        showMovieReleaseDate(rootView);
        showMovieVoteAverage(rootView);
    }

    private void showMovieVoteAverage(View rootView) {
        ((TextView) rootView.findViewById(R.id.details_rating))
                .setText(getString(R.string.movie_details_vote_average) + movie.voteAverage + "/10");
    }

    private void showMovieReleaseDate(View rootView) {
        ((TextView) rootView.findViewById(R.id.details_release_date))
                .setText(getString(R.string.movie_details_release_date) + movie.releaseDate);
    }

    private void showScrollingMovieOverview(View rootView) {
        TextView tv = ((TextView) rootView.findViewById(R.id.details_overview));
 //       tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(movie.overview);
    }

    private void showMovieTitle(View rootView) {
        ((TextView) rootView.findViewById(R.id.details_title)).setText(movie.title);
    }

    private void showMoviePoster(View rootView) {
        String fullPosterPath =
                getContext().getString(R.string.tmdb_base_image_url) +
                        getContext().getString(R.string.tmdb_image_size_342) +
                        movie.posterPath;
        ImageView imageView = (ImageView) rootView.findViewById(R.id.details_movie_poster);
        Picasso.with(getContext()).load(fullPosterPath).into(imageView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_fragment_menu, menu);

    }


}
