package com.example.android.popularmovies.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.app.MoviePreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestAppModule {

    private final TestMoviesApplication mTestMoviesApplication;

    public TestAppModule(TestMoviesApplication testMoviesApplication) {
        this.mTestMoviesApplication = testMoviesApplication;
    }

    @Provides
    @Singleton
    TestMoviesApplication providesApplication(){
        return mTestMoviesApplication;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(TestMoviesApplication application){
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    MoviePreferences providesMoviePreferences(SharedPreferences sharedPreferences,
                                              TestMoviesApplication application){
        MoviePreferences moviePreferences =
                new MoviePreferences(application, sharedPreferences);
        return moviePreferences;
    }

}
