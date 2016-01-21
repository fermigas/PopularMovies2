package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public class Reviews {


        Context mContext;
        int mMovieId;
        private MovieReviewsResponse reviewsResponse;

        public Reviews(Context mContext, int mMovieId) {
            this.mContext = mContext;
            this.mMovieId = mMovieId;
        }


        public void getReviewsFromWebAndInsertThemInDb() {

            String url = getMovieReviewsUrl(Integer.toString(mMovieId));

            // TODO  MAybe OKHTTP is faster?
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mContext, url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String responsestr = new String(responseBody);
                    Gson gson = new Gson();
                    reviewsResponse = gson.fromJson(responsestr, MovieReviewsResponse.class);
                    insertReviews(reviewsResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                      Throwable error) {   }
            });

        }

        private String getMovieReviewsUrl(String movieId) {

            Uri uri = Uri.parse(getMovieReviewsBaseURL(movieId));
            Uri.Builder builder = uri.buildUpon();
            builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
            uri = builder.build();
            return uri.toString();
        }


        private String getMovieReviewsBaseURL(String movieID) {
            return "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?";
        }


        private void insertReviews(MovieReviewsResponse mrr) {

            if (!mrr.getResults().isEmpty()) {
                for (MovieReviewsResponse.ResultsEntity mr : mrr.getResults()) {
                    addReviewToDb(mrr, mr);
                }
            }
        }


        private void addReviewToDb(MovieReviewsResponse mrr,
                                    MovieReviewsResponse.ResultsEntity mr) {

            // TODO:  Sanitize Data
            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, mrr.getId());

            if(mr.getId() == null )
                return;  // If there's no review ID, don't try to insert it
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, mr.getId());

            if(mr.getAuthor() == null) // Don't keep null values
                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, "");
            else
                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, mr.getAuthor());

            if(mr.getContent() == null )
                return;  // If there's no content, don't try to insert it
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, mr.getContent());

            if(mr.getAuthor() == null) // Don't keep null values
                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_URL, "");
            else
                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_URL, mr.getUrl());

            // TODO  Convert to Bulk Insert
            mContext.getContentResolver().insert(MoviesContract.ReviewEntry.CONTENT_URI,
                    reviewValues);

        }


}
