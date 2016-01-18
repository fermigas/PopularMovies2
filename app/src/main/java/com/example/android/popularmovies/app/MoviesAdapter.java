package com.example.android.popularmovies.app;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.apache.http.util.EncodingUtils.getBytes;

public class MoviesAdapter extends ArrayAdapter<MoviesResponse.ResultsEntity> {


    private List<MoviesResponse.ResultsEntity> mMovieItem;
    private Context mContext;

    public MoviesAdapter(Context mContext, List<MoviesResponse.ResultsEntity> mMovieItem) {

        super(mContext, 0, mMovieItem);
        this.mContext = mContext;
        this.mMovieItem = mMovieItem;
    }

    @Override
    public int getCount() {
        return mMovieItem.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundColor(Color.BLACK);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        String fullPosterPath =
                mContext.getString(R.string.tmdb_base_image_url) +
                        mContext.getString(R.string.tmdb_image_size_185) +
                        mMovieItem.get(position).getPoster_path();
        Picasso.with(mContext).load(fullPosterPath).into(imageView);

        return imageView;
    }

}
