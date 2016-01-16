package com.example.android.popularmovies.app;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MovieCursorQueryParameters {

    static Uri mUri;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;


    public MovieCursorQueryParameters(Uri mUri) {
        this.mUri = mUri;
    }


    public String getSelection(){

        StringBuilder selection = new StringBuilder();

        /* Work Backwards so all the ANDs are in the proper places */

        String genreIds = getGenreIdsSelectionString();
        if ( genreIds != null)
            selection.insert(0, genreIds );  // No AND here;  Make sure it's always first!

        String timePeriods = getTimePeriodSelectionString();
        if ( timePeriods != null)
            selection.insert(0, timePeriods + " AND ");

        String voteCount = getVoteCountSelectionString();
        if ( voteCount != null)
            selection.insert(0, voteCount + " AND ");

        String dataSource = getDataSourceSelectionString();
        if ( dataSource != null)
            selection.insert(0, dataSource + " AND ");


        return selection.toString();
    }


    public String[] getSelectionArgs (){

        List<String> selectionArgsList = new ArrayList<String>();
        selectionArgsList.add(getDataSourceSelectionStringArgs());
        selectionArgsList.add(getVoteCountSelectionArg());

        List<String> periods = Arrays.asList(getTimePeriodSelectionArgs());
        selectionArgsList.addAll(periods);

        List<String> genres = Arrays.asList(getGenreIdsSelectionArgs());
        selectionArgsList.addAll(genres);

        String[] selectionArgs = new String[selectionArgsList.size()];
        selectionArgs = selectionArgsList.toArray(selectionArgs);

        return selectionArgs;
    }


    @Nullable
    public static String getDataSourceSelectionString() {
        String dataSource = MoviesContract.MovieEntry.getDataSourceFromUri(mUri);
        switch (dataSource){

            // return nothing;  parameter doesn't get added
            case "all":
                return null;
            // return
            case "favorite_movies":
                return
                        MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_FAVORITE + " = ? ";
            case "watched":
                return
                        MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_WATCHED + " = ? ";
            case "watch_me":
                return
                        MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_WATCH_ME + " = ? ";
            default:
                return null;
        }
    }


    @Nullable
    public static String getDataSourceSelectionStringArgs() {
        String dataSource = MoviesContract.MovieEntry.getDataSourceFromUri(mUri);
        switch (dataSource){

            // return nothing;  parameter doesn't get added
            case "all":
                return null;
            // return
            case "favorite_movies":
                return "1";
            case "watched":
                return "1";
            case "watch_me":
                return "1";
            default:
                return null;
        }
    }

    @Nullable
    public static String getVoteCountSelectionString() {
        String voteCount =   MoviesContract.MovieEntry.getVoteCountFromUri(mUri);
        if(voteCount != null)
            return MoviesContract.MovieEntry.TABLE_NAME + "." +
                    MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " = ? ";
        else
            return null;

    }

    public static String getVoteCountSelectionArg() {
        return MoviesContract.MovieEntry.getVoteCountFromUri(mUri);
    }

    @Nullable
    public static String getTimePeriodSelectionString() {
        String timePeriod =   MoviesContract.MovieEntry.getTimePeriodFromUri(mUri);
        if(timePeriod != null) {
            TimePeriod tp = new TimePeriod(timePeriod);

            if(tp.isDateRange()){
                return  MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " BETWEEN ? AND ? ";
            } else if(tp.isOnlyAfter()) {
                return MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " >= ? ";
            } else {
                return MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " <= ? ";
            }

        }
        else
            return null;

    }

    public static String[] getTimePeriodSelectionArgs() {
        String timePeriod =   MoviesContract.MovieEntry.getTimePeriodFromUri(mUri);
        if(timePeriod != null) {
            TimePeriod tp = new TimePeriod(timePeriod);

            if(tp.isDateRange()){
                return  new String[] {tp.getLowerDate(), tp.getUpperDate()};
            } else if(tp.isOnlyAfter()) {
                return new String[] {tp.getLowerDate()};
            } else {
                return new String[] {tp.getUpperDate()};
            }

        }
        else
            return null;

    }


    public static String getGenreIdsSelectionString() {
        String genreIds =   MoviesContract.MovieEntry.getGenreIdsFromUri(mUri);
        if(genreIds != null) {
            String[] ids = genreIds.split(Pattern.quote(","));

            StringBuilder whereClause = new StringBuilder();
            whereClause.append(
                    " instr(" +   MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_GENRE_IDS +  ", ? ) > 0 "
            );

            for (int i = 0; i < ids.length -1 ; i++) {
                whereClause.insert(0,
                        " instr(" + MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_GENRE_IDS +  ", ? ) > 0 " + " AND "
                );
            }
            return whereClause.toString();
        }
        else
            return null;

    }

    public static String[] getGenreIdsSelectionArgs() {
        String genreIds =   MoviesContract.MovieEntry.getGenreIdsFromUri(mUri);
        if(genreIds != null)
            return genreIds.split(Pattern.quote(","));
        else
            return null;
    }

    public static String getSortOrder(){

        String sortOrder = MoviesContract.MovieEntry.getSortOrderFromUri(mUri);

        // TODO  Make ASC/DESC, revenue, PRIMARY_RELEASE dates work
        switch (sortOrder){
            case "none":
                return "";
            case "popularity.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_VOTE_COUNT;
            case "vote_average.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE;
            case "release_date.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE;
            case "primary_release_date.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE;
            case "original_title.asc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE;
        }
        return sortOrder;
    }
}
