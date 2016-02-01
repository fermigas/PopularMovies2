package com.example.android.popularmovies.app;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestNetModule {

    public TestNetModule(){

    }

    @Provides
    @Singleton
    public Gson gson(){
        return new Gson();
    }

}
