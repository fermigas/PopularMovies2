package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class MoviesCursorAdapter extends CursorAdapter {


    public MoviesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        ImageView imageView  = new ImageView(context);

        imageView.setLayoutParams(new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setPadding(0,0,0,0);

        // TODO  this is called automatically, I think
        // bindView(imageView, context, cursor);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view;


        // TODO  Get image from database instead of web as it does here
        String poster = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
        String fullPosterPath =
                context.getString(R.string.tmdb_base_image_url) +
                        context.getString(R.string.tmdb_image_size_185) +
                        poster;

        Picasso.with(context).load(fullPosterPath).into(imageView);

    }
}


