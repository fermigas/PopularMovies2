package com.example.android.popularmovies.app;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.PendingIntent.getActivity;

public class FetchMovieReviewsTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

    private ArrayAdapter<String> movieReviewsAdapter;
    private final Context context;
    private Uri.Builder builder;


    public FetchMovieReviewsTask(Context context, ArrayAdapter<String> movieReviewsAdapter) {

        this.context = context;
        this.movieReviewsAdapter = movieReviewsAdapter;

    }

    @Override
    protected String[] doInBackground(String... params) {


        HttpURLConnection movieReviewsUrlConnection = null;
        BufferedReader movieReviewsReader = null;
        String movieReviewsJsonStr = null;

        try {

            Uri builtMovieReviewsUri = Uri.parse(getBaseURL(params[0]));
            builder = builtMovieReviewsUri.buildUpon();
            appendApiKey();

            builtMovieReviewsUri = builder.build();
            URL movieReviewsURL = new URL(builtMovieReviewsUri.toString());

            movieReviewsUrlConnection = (HttpURLConnection) movieReviewsURL.openConnection();
            movieReviewsUrlConnection.setRequestMethod("GET");
            movieReviewsUrlConnection.connect();

            InputStream movieReviewsInputStream = movieReviewsUrlConnection.getInputStream();
            StringBuffer movieReviewsBuffer = new StringBuffer();
            if (movieReviewsInputStream == null)
                return null;

            movieReviewsReader = new BufferedReader(new InputStreamReader(movieReviewsInputStream));
            String line;
            while ((line = movieReviewsReader.readLine()) != null)
                movieReviewsBuffer.append(line + "\n");

            if (movieReviewsBuffer.length() == 0)
                return null;  // Don't parse of there's no data

            movieReviewsJsonStr = movieReviewsBuffer.toString();

            Log.v(LOG_TAG, "Movie Review Data string: " + movieReviewsJsonStr);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        } finally {
            if (movieReviewsUrlConnection != null) {
                movieReviewsUrlConnection.disconnect();
            }
            if (movieReviewsReader != null) {
                try {
                    movieReviewsReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing movie reviews reader stream.", e);
                }
            }
        }

        try {
            return getMovieReviewsDataFromJson(movieReviewsJsonStr );
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if(result != null ) {
            movieReviewsAdapter.clear();
            for (String movieReviewStr : result) {
                movieReviewsAdapter.add(movieReviewStr);
            }
        }
    }

    private void appendApiKey() {
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
    }


    private String getBaseURL(String param){
        return "http://api.themoviedb.org/3/movie/" + param + "/reviews?";
    }


    private String[] getMovieReviewsDataFromJson(String movieReviewsJsonStr )
            throws JSONException {

        final String TMBD_REVIEW_CONTENT = "content";

        JSONObject movieReviewsJson = new JSONObject(movieReviewsJsonStr);
        JSONArray movieReviewsArray = movieReviewsJson.getJSONArray("results");

        String[] movieReviews = new String[movieReviewsArray.length()];

        for(int i = 0; i < movieReviewsArray.length(); i++) {
            JSONObject movieReviewObject = movieReviewsArray.getJSONObject(i);
            movieReviews[i] = movieReviewObject.getString(TMBD_REVIEW_CONTENT);


        }


        return movieReviews;
    }

}
