package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;


public class MovieDetails {

    private final Context mContext;
    private final MoviesResponse.ResultsEntity mMovie;
    private boolean mFavoriteState;
    private final View mRootView;

    public MovieDetails(Context context, MoviesResponse.ResultsEntity movie, boolean favoriteState, View rootView) {
        this.mContext = context;
        this.mMovie = movie;
        this.mFavoriteState = favoriteState;
        this.mRootView = rootView;

    }

    public void showMovieDetails() {
        showMoviePoster();
        showMovieTitle();
        showScrollingMovieOverview();
        showMovieReleaseDate();
        showMovieVoteAverage();
        setFavoritesToggleButtonInitialState();

        setToggleButtonHandler();

    }

    private void showMoviePoster() {
        String fullPosterPath =
                mContext.getString(R.string.tmdb_base_image_url) +
                        mContext.getString(R.string.tmdb_image_size_342) +
                        mMovie.getPoster_path();
        ImageView imageView = (ImageView) mRootView.findViewById(R.id.details_movie_poster);
        Picasso.with(mContext).load(fullPosterPath).into(imageView);

    }

    private void showMovieTitle() {
        ((TextView) mRootView.findViewById(R.id.details_title)).setText(mMovie.getTitle());
    }

    private void showScrollingMovieOverview() {
        TextView tv = ((TextView) mRootView.findViewById(R.id.details_overview));
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(mMovie.getOverview());
    }

    private void showMovieReleaseDate() {
        ((TextView) mRootView.findViewById(R.id.details_release_date))
                .setText(mMovie.getRelease_date().substring(0, 4));
    }

    private void showMovieVoteAverage() {
        ((TextView) mRootView.findViewById(R.id.details_rating))
                .setText(mMovie.getVote_average() + mContext.getString(R.string.out_of_ten));
    }

    private void setFavoritesToggleButtonInitialState() {
        ToggleButton toggle = (ToggleButton) mRootView.findViewById(R.id.toggle_button);
        toggle.setChecked(getFavoriteState());
    }

    private void setToggleButtonHandler() {
        ToggleButton toggle = (ToggleButton) mRootView.findViewById(R.id.toggle_button);
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

        int rowsUpdated = mContext.getContentResolver()
                .update(MoviesContract.MovieEntry.CONTENT_URI,
                        movieValues,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(mMovie.getId())}
                );

    }

    public void setFavoriteState(boolean favoriteState) {
        this.mFavoriteState = favoriteState;
    }

    public boolean getFavoriteState() {
        return mFavoriteState;
    }

}
