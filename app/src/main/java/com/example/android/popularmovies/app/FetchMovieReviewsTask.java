package com.example.android.popularmovies.app;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FetchMovieReviewsTask extends AsyncTask<Void, Void, String[]> {

    private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

    private final Context context;
    private ArrayAdapter<String> movieReviewsAdapter;
    private String url = null;

    public FetchMovieReviewsTask(Context context, String url, ArrayAdapter<String> movieReviewsAdapter) {

        this.context = context;
        this.movieReviewsAdapter = movieReviewsAdapter;
        this.url = url;
    }

    @Override
    protected String[] doInBackground(Void... params) {

        String movieReviewsJsonStr = null;

        try {
            movieReviewsJsonStr = getJSONString();
            Log.v(LOG_TAG, "Movie Review Data string: " + movieReviewsJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
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


    private String getJSONString () throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
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
