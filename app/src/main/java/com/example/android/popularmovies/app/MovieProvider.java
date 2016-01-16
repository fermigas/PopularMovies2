package com.example.android.popularmovies.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.app.MoviesContract.CONTENT_AUTHORITY;


//  URISs to support
//
//  For Main Movie List
//  CONTENT://com.example.android.popularmovies.app/movie/?which_movies=
//              [all|favorite_movies|watched|watchme]
//  CONTENT://com.example.android.popularmovies.app/movie/?sort_order=
//              [none|vote_count|vote_average|release_date|alphabetically]
//  CONTENT://com.example.android.popularmovies.app/movie/?vote_count=
//              []
//  CONTENT://com.example.android.popularmovies.app/movie/?time_period=
//              []
//  CONTENT://com.example.android.popularmovies.app/movie/?genre_filters=
//              []
//
//   For Movie Details
//  CONTENT://com.example.android.popularmovies.app/movie/[movie_id]
//  CONTENT://com.example.android.popularmovies.app/movie/[movie_id]/trailer
//  CONTENT://com.example.android.popularmovies.app/movie/[movie_id]/review

//  Queries
//      SELECT
//         Movies
//      WHERE
//          Favorite = true:  only favorites
//          Favorite = false: tmdb movies (online data)
//          Later:  All movies, Watched, Watch_me
//      ORDER BY:
//      All | Favorite | Watched | Watch ME movies, no sorting or filtering at all
//             ""  sorted by Most Popular:  vote_count DESC
//             ""  sorted by Highest Rated: vote_average DESC
//             ""  sorted by Latest Releases: release_date DESC
//             ""  sorted by Highest Grossing:  need to add field for this, or cut it
//             ""  sorted by Primary Releases, Newest First:  primary_release_date DESC
//             ""  sorted by Primary Releases, Oldest First:  primary_release_date ASC
//             ""  sorted Alphabetically: title ASC


//  Inserts
//      Movie
//          Movie Poster Image
//

//  Deletes
//      Everything from all 4 tables to begin with;  get more granular later

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_QUERY_STRING = 101;
    static final int TRAILERS_BY_MOVIE_ID = 201;
    static final int REVIEWS_BY_MOVIE_ID = 301;

    private static final SQLiteQueryBuilder sMoviesQueryBuilder;

    static {

        sMoviesQueryBuilder = new SQLiteQueryBuilder();
    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)){

            case MOVIES: {
                retCursor = getMovies(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case MOVIES_WITH_QUERY_STRING: {
                // TODO make separate function for handling query strings
                retCursor = getMoviesWithQueryString(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMoviesWithQueryString(Uri uri, String[] projection, String sortOrder) {


        return  mOpenHelper.getReadableDatabase().query(
                MoviesContract.MovieEntry.TABLE_NAME,
                projection,
                null,  // TODO getSelection(uri),
                null,  // TODO selectionArgs,
                null,
                null,
                sortOrder
        );

    }


    private Cursor getMovies(Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {


        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){

            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;

            case MOVIES_WITH_QUERY_STRING:
                return MoviesContract.MovieEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES:
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if(null==selection) selection = "1";
        switch ((match)){
            case MOVIES:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection,
                                    selectionArgs);
                   break;
            default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        return rowsUpdated;
    }


    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIES);

        // TODO match /movies + querystring
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", MOVIES_WITH_QUERY_STRING);


        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
