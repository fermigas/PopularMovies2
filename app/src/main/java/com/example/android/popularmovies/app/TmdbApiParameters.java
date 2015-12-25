package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Set;

public class TmdbApiParameters {

    private final String LOG_TAG = TmdbApiParameters.class.getSimpleName();

    Activity mActivity;
    int mcurrentPage = 1;
    private Uri.Builder builder;
    private Uri uri;
    private SharedPreferences prefs;

    TmdbApiParameters(Activity activity, int currentPage){
        this.mActivity = activity;
        this.mcurrentPage = currentPage;
        uri = Uri.parse(getBaseURL());
        builder = uri.buildUpon();
        prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
    }


    public Uri buildMoviesUri() {

        appendPage();
        appendSortOrder();
        appendVoteCount();
        appendGenres();
        appendTimePeriods();
        appendApiKey();

        uri = builder.build();

        Log.v(LOG_TAG, "Movie URL " + uri.toString());
        return uri;
    }

    private void appendApiKey() {
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
    }

    private void appendTimePeriods() {
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
    }

    private void appendGenres() {
        String genres = getGenresAsCommaSeparatedNumbers();
        if(!genres.equals(""))
           builder.appendQueryParameter("with_genres", genres);
    }

    private void appendVoteCount() {
        builder.appendQueryParameter("vote_count.gte", getVoteCount());
    }

    private void appendSortOrder() {
        String sortOrder = getSortOrder();
        if(sortOrder != null && !sortOrder.equals("none") )
            builder.appendQueryParameter("sort_by", sortOrder);
    }

    private void appendPage() {
         builder.appendQueryParameter("page", String.valueOf(mcurrentPage));
    }

    private String getPeriodPreferences() { return   prefs.getString(mActivity.getString(R.string.pref_period_key), "all"); }


    private String getGenresAsCommaSeparatedNumbers() {

        String genres;
        Set<String> genresSet = prefs.getStringSet("genre_ids", null);
        if ( genresSet != null && !genresSet.isEmpty()  )
           genres = genresSet.toString().replaceAll("\\s+", "").replace("[", "").replace("]", "");
        else
            genres = "";

        return genres;
    }

    private String getBaseURL(){
       return mActivity.getString(R.string.tmdb_base_url);
    }

    private String getSortOrder(){ return  prefs.getString( mActivity.getString(R.string.pref_sort_order_key), mActivity.getString(R.string.pref_sort_order_most_popular)); }

    private String getVoteCount(){  return  prefs.getString( mActivity.getString(R.string.pref_vote_count_key),  mActivity.getString(R.string.pref_vote_count_value_0)); }

}
