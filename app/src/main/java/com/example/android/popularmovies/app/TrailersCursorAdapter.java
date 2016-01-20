package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class TrailersCursorAdapter extends CursorAdapter {

    public TrailersCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item_movie_trailer, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView trailer = (TextView) view.findViewById(R.id.list_item_movie_trailer_textview);
        trailer.setText("https://www.youtube.com/watch?v=" +
                cursor.getString(cursor.getColumnIndex("source")));

    }
}
