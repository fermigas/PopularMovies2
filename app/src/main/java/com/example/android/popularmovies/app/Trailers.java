package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public class Trailers {

    Context mContext;
    int mMovieId;
    private MovieTrailersResponse trailersResponse;

    public Trailers(Context mContext, int mMovieId) {
        this.mContext = mContext;
        this.mMovieId = mMovieId;
    }


    public void getTrailersFromWebAndInsertThemInDb() {

        String url = getMovieTrailersUrl(Integer.toString(mMovieId));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mContext, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responsestr = new String(responseBody);
                Gson gson = new Gson();
                trailersResponse = gson.fromJson(responsestr, MovieTrailersResponse.class);
                insertTrailers(trailersResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {   }
        });

    }

    private String getMovieTrailersUrl(String movieId) {

        Uri uri = Uri.parse(getMovieTrailersBaseURL(movieId));
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        uri = builder.build();
        return uri.toString();
    }


    private String getMovieTrailersBaseURL(String movieID) {
        return "http://api.themoviedb.org/3/movie/" + movieID + "/trailers?";
    }


    private void insertTrailers(MovieTrailersResponse mtr) {

        if (!mtr.getYoutube().isEmpty()) {
            for (MovieTrailersResponse.YoutubeEntity yte : mtr.getYoutube()) {
                addTrailerToDb(mtr, yte);
            }
        }
    }


    private void addTrailerToDb(MovieTrailersResponse mtr,
                                MovieTrailersResponse.YoutubeEntity yte) {

        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, mtr.getId());
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_NAME, yte.getName());
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SIZE, yte.getSize());
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SOURCE, yte.getSource());
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TYPE, yte.getType());

        mContext.getContentResolver().insert(MoviesContract.TrailerEntry.CONTENT_URI,
                trailerValues);

    }

}
