package com.example.android.popularmovies.app;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.app.MoviesContract.ReviewEntry;
import static com.example.android.popularmovies.app.MoviesContract.TrailerEntry;

// TODO Organize movie details fragment layout to support
//         App Bar
//         Favorites Button
//         Shrunken Overview
//         Trailers section
//         Reviews Section
// TODO Add setting to chose number of movie columns shown (1,2,3,4)?  Tablet could be a problem

public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private MoviesResponse.ResultsEntity movie;
    private ListView trailersListView;
    private ListView reviewsListView;
    boolean favoriteState;


    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null) {
            movie = arguments.getParcelable("movie");
            favoriteState = arguments.getBoolean("favorite_state");
        }
        else
            getMovieFromParcelableExtra();


        View rootView = inflater.inflate(R.layout.details_fragment, container, false);

        // on startup, no movie has been selected yet
        if(movie == null)
            return rootView;

        setFavoritesToggleButtonInitialState(rootView);
        showMovieData(rootView);
        showMovieTrailers(rootView);
        showMovieReviews(rootView);

        setToggleButtonHandler(rootView);

        return rootView;
    }

    private void setToggleButtonHandler(View rootView) {
        ToggleButton toggle = (ToggleButton) rootView.findViewById(R.id.toggle_button);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFavoriteState(!getFavoriteState());
                if (isChecked) {
                    updateFavoriteInDb(1);
                } else {
                    updateFavoriteInDb(0);
                }
            }
        });
    }

    private void updateFavoriteInDb(int favoriteState) {

        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, favoriteState);

        int rowsUpdated = getContext().getContentResolver()
                .update(MoviesContract.MovieEntry.CONTENT_URI,
                        movieValues,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[] {String.valueOf(movie.getId())}
                );

    }


    private void showMovieTrailers(View rootView) {

        trailersListView = (ListView) rootView.findViewById(R.id.listview_movie_trailer);
        Trailers trailers = new Trailers(getActivity(), trailersListView, movie.getId());
        trailers.setMovieTrailers();

    }


    private void showMovieReviews(View rootView) {

        reviewsListView = (ListView) rootView.findViewById(R.id.listview_movie_review);
        Reviews reviews = new Reviews(getActivity(), reviewsListView,  movie.getId());
        reviews.setMovieReviews();

    }

    private void getMovieFromParcelableExtra() {

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie"))
            movie = intent.getParcelableExtra("movie");

    }

    private void setFavoritesToggleButtonInitialState(View rootView) {

        ToggleButton toggle = (ToggleButton) rootView.findViewById(R.id.toggle_button);
        setFavoriteState(getFavoriteStateExtra());
        toggle.setChecked(getFavoriteState());

    }

    private boolean getFavoriteStateExtra() {

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("favorite_state"))
            return intent.getBooleanExtra("favorite_state", false);
        else
            return false;
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
        setFavoritesToggleButtonInitialState(rootView);
    }

    private void showMovieReleaseDate(View rootView) {
        ((TextView) rootView.findViewById(R.id.details_release_date))
                .setText(movie.getRelease_date().substring(0,4));
    }

    private void showMovieVoteAverage(View rootView) {
        ((TextView) rootView.findViewById(R.id.details_rating))
                .setText(movie.getVote_average() + "/10");
    }

    private void showScrollingMovieOverview(View rootView) {
        TextView tv = ((TextView) rootView.findViewById(R.id.details_overview));
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(movie.getOverview());
    }

    private void showMovieTitle(View rootView) {
        ((TextView) rootView.findViewById(R.id.details_title)).setText(movie.getTitle());
    }

    private void showMoviePoster(View rootView) {
        String fullPosterPath =
                getContext().getString(R.string.tmdb_base_image_url) +
                        getContext().getString(R.string.tmdb_image_size_342) +
                        movie.getPoster_path();
        ImageView imageView = (ImageView) rootView.findViewById(R.id.details_movie_poster);
        Picasso.with(getContext()).load(fullPosterPath).into(imageView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_fragment_menu, menu);

    }


    public void setFavoriteState(boolean favoriteState) {
        this.favoriteState = favoriteState;
    }

    public boolean getFavoriteState() {
        return favoriteState;
    }
}
