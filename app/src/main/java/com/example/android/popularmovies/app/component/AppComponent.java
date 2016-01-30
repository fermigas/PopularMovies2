package com.example.android.popularmovies.app.component;

import android.content.SharedPreferences;

import com.example.android.popularmovies.app.MainActivity;
import com.example.android.popularmovies.app.MovieDetailsFragment;
import com.example.android.popularmovies.app.MoviesApplication;
import com.example.android.popularmovies.app.TmdbApiParameters;
import com.example.android.popularmovies.app.module.AppModule;
import com.example.android.popularmovies.app.module.NetModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = {AppModule.class})
public interface AppComponent {
    // void inject(MovieDetailsFragment fragment);

    void inject(MoviesApplication moviesApplication);
    void inject(MainActivity activity);
    void inject(SharedPreferences sharedPreferences);

    void inject(TmdbApiParameters tmdbApiParameters);
}