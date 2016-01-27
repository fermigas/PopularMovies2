package com.example.android.popularmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private ListView trailersListView;
    private ListView reviewsListView;
    public static final String TRAILER_SHARE_HASHTAG = "#PopularMoviesTrailer";
    public ShareActionProvider mShareActionProvider;
    private View rootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.v(LOG_TAG, "***  Entering onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.v(LOG_TAG, "***  Entering onCreateView()");
        MoviesResponse.ResultsEntity movie = getPassedMovie();
        boolean favoriteState = getPassedFavoriteState();

        rootView = inflater.inflate(R.layout.details_fragment, container, false);
        trailersListView = (ListView) rootView.findViewById(R.id.listview_movie_trailer);

        // on startup, no movie has been selected yet in twoPane UIs
        if(movie == null)
            return rootView;

        MovieDetails movieDetails = new MovieDetails(getActivity(), movie, favoriteState, rootView);
        movieDetails.showMovieDetails();

        // Trailers are loaded in onCreateOptionsMenu due to
        //      ShareActionProvider concurrency considerations

        showMovieReviews(movie);

        return rootView;
    }

    private MoviesResponse.ResultsEntity getPassedMovie() {
        Bundle arguments = getArguments();
        if(arguments != null) {
            return arguments.getParcelable("movie");
        }
        else {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movie"))
                return intent.getParcelableExtra("movie");
        }

        return null;
    }

    private boolean getPassedFavoriteState() {
        Bundle arguments = getArguments();
        if(arguments != null) {
            return arguments.getBoolean("favorite_state");
        }
        else {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("favorite_state"))
                return intent.getBooleanExtra("favorite_state", false);
        }

        return false;
    }



    private void showMovieReviews(MoviesResponse.ResultsEntity movie) {

        reviewsListView = (ListView) rootView.findViewById(R.id.listview_movie_review);
        Reviews reviews = new Reviews(getActivity(), reviewsListView,  movie.getId());
        reviews.setMovieReviews();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_fragment_menu, menu);
//        Log.v(LOG_TAG, "***  Entering onCreateOptionsMenu()");

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareTrailerIntent(""));

        if(getPassedMovie() != null)
            showMovieTrailers(getPassedMovie());

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showMovieTrailers(MoviesResponse.ResultsEntity movie) {
        trailersListView = (ListView) rootView.findViewById(R.id.listview_movie_trailer);
        Trailers trailers = new Trailers(this, getActivity(), trailersListView, movie.getId());
        trailers.setMovieTrailers();
    }

    public Intent createShareTrailerIntent(String trailers) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailers + TRAILER_SHARE_HASHTAG);
        return shareIntent;
    }


}
