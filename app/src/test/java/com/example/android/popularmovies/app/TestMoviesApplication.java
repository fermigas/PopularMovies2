package com.example.android.popularmovies.app;

import android.content.SharedPreferences;


import com.example.android.popularmovies.app.MoviesApplication;
import com.example.android.popularmovies.app.MoviesFragment;
import com.example.android.popularmovies.app.Reviews;
import com.example.android.popularmovies.app.TmdbApiParameters;
import com.example.android.popularmovies.app.Trailers;

import javax.inject.Singleton;

import dagger.Component;

public class TestMoviesApplication extends MoviesApplication {


    @Singleton
    @Component(modules = {TestAppModule.class, TestNetModule.class})
    public interface TestAppComponent extends AppComponent {

    void inject(MoviesApplication moviesApplication);
    void inject(SharedPreferences sharedPreferences);
    void inject(TmdbApiParameters tmdbApiParameters);
    void inject(MoviesFragment fragment);
    void inject(Reviews reviews);
    void inject(Trailers trailers);

    }

    private TestAppComponent mAppComponent;

    @Override
    public void onCreate(){

        super.onCreate();

        mAppComponent = DaggerTestMoviesApplication_TestAppComponent.builder()
                .testAppModule(new TestAppModule(this))
                .testNetModule(new TestNetModule())
                .build();
    }

    @Override
    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
