/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.app.MoviesContract.MoviesEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +

                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MoviesEntry.COLUMN_POSTER_PATH + " TEXT , " +
                MoviesEntry.COLUMN_ADULT + " INTEGER , " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT , " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT , " +
                MoviesEntry.COLUMN_ID + " INTEGER NOT NULL , " +
                MoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT , " +
                MoviesEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT , " +
                MoviesEntry.COLUMN_TITLE + " TEXT , " +
                MoviesEntry.COLUMN_BACKDROP_PATH + " TEXT , " +
                MoviesEntry.COLUMN_POPULARITY + " REAL , " +
                MoviesEntry.COLUMN_VOTE_COUNT + " INTEGER , " +
                MoviesEntry.COLUMN_VIDEO + " INTEGER , " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL , " +
                MoviesEntry.COLUMN_GENRE_IDS + " TEXT , " +
                MoviesEntry.COLUMN_FAVORITE + " INTEGER , " +
                MoviesEntry.COLUMN_WATCHED + " INTEGER , " +
                MoviesEntry.COLUMN_WATCH_ME + " INTEGER , " +


                 " UNIQUE (" + MoviesEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
