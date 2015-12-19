package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Objects;
import java.util.Set;

public class ApiParameters {

    private final String LOG_TAG = ApiParameters.class.getSimpleName();

    Activity mActivity;
    int mcurrentPage = 1;

    ApiParameters(Activity activity, int currentPage){

        this.mActivity = activity;
        this.mcurrentPage = currentPage;

    }


    public Uri buildMoviesUri() {

        Uri uri = Uri.parse(getBaseURL());
        Uri.Builder builder = uri.buildUpon();

        builder.appendQueryParameter("page", String.valueOf(mcurrentPage));

        String sortOrder = getSortOrder();
        if(sortOrder != null && !sortOrder.equals("none") )
            builder.appendQueryParameter("sort_by", sortOrder);

        builder.appendQueryParameter("vote_count.gte", getVoteCount());

        String genres = getGenresAsCommaSeparatedNumbers();
        if(!genres.equals(""))
           builder.appendQueryParameter("with_genres", genres);

        // Use primariy_release_date or you get back multile results
        // because sometimes there are much later re-releases
        // This can pollute results upon sorting by gross, popularity, etc.
        TimePeriod timePeriod = new TimePeriod(getPeriodPreferences());
        if(timePeriod.periodHasLowerDate()) {
            builder.appendQueryParameter("primary_release_date.gte", timePeriod.getLowerDate());
        }
        if(timePeriod.periodHasUpperDate()) {
            builder.appendQueryParameter("primary_release_date.lte", timePeriod.getUpperDate());
        }

        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

        uri = builder.build();

        Log.v(LOG_TAG, "Movie URL " + uri.toString());
        return uri;
    }

    private String getPeriodPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        return   prefs.getString(mActivity.getString(R.string.pref_period_key), "all");
    }


    private String getGenresAsCommaSeparatedNumbers() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        String genres;
        Set<String> genresSet = prefs.getStringSet("genre_ids", null);
        if ( genresSet != null && !genresSet.isEmpty()  )
           genres = genresSet.toString()
                   .replaceAll("\\s+", "")
                   .replace("[", "")
                   .replace("]", "");
        else
            genres = "";

        return genres;
    }

    private String getBaseURL(){
       return mActivity.getString(R.string.tmdb_base_url);
    }


    private String getSortOrder(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        return  prefs.getString(
                   mActivity.getString(R.string.pref_sort_order_key),
                   mActivity.getString(R.string.pref_sort_order_most_popular));
    }

    private String getVoteCount(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        return  prefs.getString(
                   mActivity.getString(R.string.pref_vote_count_key),
                   mActivity.getString(R.string.pref_vote_count_value_0));
    }
}
