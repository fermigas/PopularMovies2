package com.example.android.popularmovies.app;


import android.app.Application;
import android.content.SharedPreferences;

import java.util.Set;


public class MoviePreferences {

    SharedPreferences mSharedPreferences;
    Application mApplication;

    public MoviePreferences(Application mApplication, SharedPreferences mSharedPreferences) {
        this.mApplication = mApplication;
        this.mSharedPreferences = mSharedPreferences;
    }

    public String getPeriodPreferences() {
        return   mSharedPreferences.getString(mApplication
                    .getString(R.string.pref_period_key), "all");
    }

    public String getGenresAsCommaSeparatedNumbers() {

        String genres;
        Set<String> genresSet = mSharedPreferences.getStringSet("genre_ids", null);
        if ( genresSet != null && !genresSet.isEmpty()  )
            genres = genresSet.toString().replaceAll("\\s+", "").replace("[", "").replace("]", "");
        else
            genres = "";

        return genres;
    }

    public String getSortOrder(){ return  mSharedPreferences.getString(
            mApplication.getString(R.string.pref_sort_order_key),
            mApplication.getString(R.string.pref_sort_order_most_popular));
    }

    public String getVoteCount(){  return  mSharedPreferences.getString(
            mApplication.getString(R.string.pref_vote_count_key),
            mApplication.getString(R.string.pref_vote_count_value_0)); }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
