package com.example.android.popularmovies.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

        trailersListView = (ListView) rootView.findViewById(R.id.listview_movie_trailer);

        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(
                    TrailerEntry.buildTrailerWithMovieId(String.valueOf(movie.getId())),
                    null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
        }

        TrailersCursorAdapter tca = new TrailersCursorAdapter(getActivity(), cursor, 0);
        trailersListView.setAdapter(tca);

        trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = getActivity()
                        .getPackageManager()
                        .getLaunchIntentForPackage("com.google.android.youtube");

                TextView sourceView = (TextView) view.findViewById(
                        R.id.list_item_movie_trailer_url_textview);

                String source = sourceView.getText().toString() ;
                watchYoutubeVideo(source);

            }
        });
    }


    private void watchYoutubeVideo(String source){

        try{

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + source));
            startActivity(intent);

        }catch (ActivityNotFoundException e){

            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+source));

            startActivity(intent);
        }
    }
    private void showMovieReviews(View rootView) {

        reviewsListView = (ListView) rootView.findViewById(R.id.listview_movie_review);

        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(
                    ReviewEntry.buildReviewWithMovieId(String.valueOf(movie.getId())),
                    null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
        }

        ReviewsCursorAdapter rca = new ReviewsCursorAdapter(getActivity(), cursor, 0);
        reviewsListView.setAdapter(rca);

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
                .setText(getString(R.string.movie_details_vote_average) + movie.getVote_average() + "/10");
    }

    private void showMovieReleaseDate(View rootView) {
        ((TextView) rootView.findViewById(R.id.details_release_date))
                .setText(getString(R.string.movie_details_release_date) + movie.getRelease_date());
    }

    private void showScrollingMovieOverview(View rootView) {
        TextView tv = ((TextView) rootView.findViewById(R.id.details_overview));
 //       tv.setMovementMethod(new ScrollingMovementMethod());
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


}
