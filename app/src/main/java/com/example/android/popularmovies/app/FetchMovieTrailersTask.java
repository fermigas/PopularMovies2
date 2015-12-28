package com.example.android.popularmovies.app;

import android.content.Context;
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


public class FetchMovieTrailersTask extends AsyncTask<Void, Void, String[]> {

    private final String LOG_TAG = FetchMovieTrailersTask.class.getSimpleName();

    private final Context context;
    private ArrayAdapter<String> movieTrailersAdapter;
    private String url = null;

    public FetchMovieTrailersTask(Context context, String url, ArrayAdapter<String> movieTrailersAdapter) {

        this.context = context;
        this.movieTrailersAdapter = movieTrailersAdapter;
        this.url = url;

    }


    @Override
    protected String[] doInBackground(Void... params) {

        String movieTrailersJsonStr = null;

        try {
            movieTrailersJsonStr = getJSONString();
            Log.v(LOG_TAG, "Movie Trailers Data string: " + movieTrailersJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        }

        try {
            return getMovieTrailersDataFromJson(movieTrailersJsonStr );
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if(result != null ) {
            movieTrailersAdapter.clear();
            for (String movieTrailerStr : result) {
                movieTrailersAdapter.add(movieTrailerStr);
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



    private String[] getMovieTrailersDataFromJson(String movieTrailersJsonStr )
            throws JSONException {


        JSONObject movieTrailersJson = new JSONObject(movieTrailersJsonStr);
        JSONArray movieTrailersArray = movieTrailersJson.getJSONArray("youtube");

        String[] movieTrailers = new String[movieTrailersArray.length()];

        for(int i = 0; i < movieTrailersArray.length(); i++) {
            JSONObject movieTrailerObject = movieTrailersArray.getJSONObject(i);
            movieTrailers[i] = "https://www.youtube.com/watch?v=" + movieTrailerObject.getString("source");
        }

        return movieTrailers;
    }


}
