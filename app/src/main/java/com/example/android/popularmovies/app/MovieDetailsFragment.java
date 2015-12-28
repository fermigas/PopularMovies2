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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    ArrayAdapter<String> movieReviewsAdapter;
    private Movie movie;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMovieReviewsTask movieReviews = new FetchMovieReviewsTask(getActivity(), movieReviewsAdapter);
        movieReviews.execute(movie.id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.details_fragment, container, false);


        getMovieFromParcelableExtra();
        showAllMovieData(rootView);

        showMovieReviews(rootView);

        return rootView;
    }

    private void showMovieReviews(View rootView) {

        // List<String> allFakeReviews = new ArrayList<>(Arrays.asList(fakeReviews));
        movieReviewsAdapter =
                new ArrayAdapter<>(
                        getActivity(),
                        R.layout.list_item_movie_review,
                        R.id.list_item_movie_review_textview,
                        new ArrayList<String>() );
        ListView listView = (ListView) rootView.findViewById(R.id.listview_movie_review);
        listView.setAdapter(movieReviewsAdapter);

    }

    private void showFakeMovieReviews(View rootView) {

     // *** TODO Replace Fake Data ***  //
        String[] fakeReviews = {
                "There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "maybe too much. There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?",
                "so fun, bumkin . There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?There is a lot.   And a lot more, too.  And even more.  What is going to happen with all this data?"
        };

        List<String> allFakeReviews = new ArrayList<>(Arrays.asList(fakeReviews));
        movieReviewsAdapter =
                new ArrayAdapter<>(
                        getActivity(),
                        R.layout.list_item_movie_review,
                        R.id.list_item_movie_review_textview,
                        allFakeReviews);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_movie_review);
        listView.setAdapter(movieReviewsAdapter);
    }

    private void getMovieFromParcelableExtra() {

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie"))
            movie = intent.getParcelableExtra("movie");

    }

    private void showAllMovieData(View rootView) {

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
        tv.setMovementMethod(new ScrollingMovementMethod());
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
