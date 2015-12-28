package com.example.android.popularmovies.app;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.PendingIntent.getActivity;

public class FetchMovieReviewsTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

    private final Context context;
    private ArrayAdapter<String> movieReviewsAdapter;


    private Uri.Builder builder;
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;


    public FetchMovieReviewsTask(Context context, ArrayAdapter<String> movieReviewsAdapter) {

        this.context = context;
        this.movieReviewsAdapter = movieReviewsAdapter;

    }

    @Override
    protected String[] doInBackground(String... params) {

        String movieReviewsJsonStr = null;

        try {

            movieReviewsJsonStr = getJSONString(params[0]);
            if (movieReviewsJsonStr == null) return null;

            Log.v(LOG_TAG, "Movie Review Data string: " + movieReviewsJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;

        } finally {

            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
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

    @Nullable
    private String getJSONString(String param) throws IOException {

        connectToUrl(getUrl(param));

        InputStream stream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (stream == null)
            return null;

        reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null)
            buffer.append(line + "\n");

        if (buffer.length() == 0)
            return null;

        return buffer.toString();
    }

    private void connectToUrl(URL url) throws IOException {
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
    }

    @NonNull
    private URL getUrl(String param) throws MalformedURLException {
        Uri uri = Uri.parse(getBaseURL(param));
        builder = uri.buildUpon();
        appendApiKey();

        uri = builder.build();
        return new URL(uri.toString());
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
