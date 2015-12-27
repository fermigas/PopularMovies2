package com.example.android.popularmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


// TODO  Generalize Api Code to support movies, reviews and trailers (and maybe more)
// TODO Add AsyncTask to retrieve trailers
//    TODO View List & List Item for trailers
// TODO Add AsyncTask to retrieve Reviews
//    TODO  View List and List Item for reviews
// TODO Organize movie details fragment layout to support
//         App Bar
//         Favorites Button
//         Shrunken Overview
//         Trailers section
//         Reviews Section
// TODO Add setting to chose number of movie columns shown (1,2,3,4)?  Tablet could be a problem

public class MovieDetailsFragment extends Fragment {

    private String mMovieStr;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.details_fragment, container, false);
        Intent intent = getActivity().getIntent();

        showAllMovieData(rootView, intent);

        return rootView;
    }

    private void showAllMovieData(View rootView, Intent intent) {
        if (intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");

            showMovieDetails(rootView, movie);

        }
    }

    private void showMovieDetails(View rootView, Movie movie) {
        showMoviePoster(rootView, movie);
        showMovieTitle(rootView, movie);
        showScrollingMovieOverview(rootView, movie);
        showMovieReleaseDate(rootView, movie);
        showMovieVoteAverage(rootView, movie);
    }

    private void showMovieVoteAverage(View rootView, Movie movie) {
        ((TextView) rootView.findViewById(R.id.details_rating))
                .setText(getString(R.string.movie_details_vote_average) + movie.voteAverage + "/10");
    }

    private void showMovieReleaseDate(View rootView, Movie movie) {
        ((TextView) rootView.findViewById(R.id.details_release_date))
                .setText(getString(R.string.movie_details_release_date) + movie.releaseDate);
    }

    private void showScrollingMovieOverview(View rootView, Movie movie) {
        TextView tv = ((TextView) rootView.findViewById(R.id.details_overview));
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(movie.overview);
    }

    private void showMovieTitle(View rootView, Movie movie) {
        ((TextView) rootView.findViewById(R.id.details_title)).setText(movie.title);
    }

    private void showMoviePoster(View rootView, Movie movie) {
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
