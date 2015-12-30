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

import android.provider.BaseColumns;
import android.text.format.Time;

public class MoviesContract {


    public static final class MoviesEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_ADULT = "adult";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        public static final String COLUMN_POPULARITY = "popularity";

        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static final String COLUMN_VIDEO = "video";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_GENRE_IDS = "genre_ids";

        public static final String COLUMN_FAVORITE = "favorite";

        public static final String COLUMN_WATCHED = "watched";

        public static final String COLUMN_WATCH_ME = "watch_me";

    }
}
