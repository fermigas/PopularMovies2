package com.example.android.popularmovies.app;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.popularmovies.app.component.DaggerAppComponent;
import com.example.android.popularmovies.app.component.AppComponent;
import com.example.android.popularmovies.app.module.AppModule;

import javax.inject.Inject;

public class MoviesApplication extends Application {

    private AppComponent mAppComponent;


    @Override public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();



    }


    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}


