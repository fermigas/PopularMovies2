package com.example.android.popularmovies.app;


import android.app.Application;

import com.example.android.popularmovies.app.component.AppComponent;
import com.example.android.popularmovies.app.component.DaggerAppComponent;
import com.example.android.popularmovies.app.module.AppModule;

public class MoviesApplication extends Application {

    private AppComponent mAppComponent;


    @Override public void onCreate() {
        super.onCreate();

        // AppModule holds application-wide singletons
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }


    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}


