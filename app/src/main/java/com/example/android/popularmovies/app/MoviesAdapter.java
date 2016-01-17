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

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();


    private List<MoviesResponse.ResultsEntity> mMovieItem;
    private Context mContext;
    private LayoutInflater inflater;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundColor(Color.BLACK);
            imageView.setPadding(0,0,0,0);
        }
        else {
            imageView = (ImageView) convertView;
        }


        Cursor cursor = null;

        try {  //  TODO:  very wasteful;  should pass the cursor or BMP in; or always use cursorAdapter

            String selection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(mMovieItem.get(position).getId())};

             cursor = mContext.getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI, null,
                    selection,
                    selectionArgs,
                    null);

            if(cursor!= null && cursor.getCount() > 0) {

                String cursorContents = DatabaseUtils.dumpCursorToString(cursor);
                Log.v(LOG_TAG, cursorContents);

                int index = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_BITMAP);
                cursor.moveToFirst();
                byte[] imageBytes =  cursor.getBlob(index );

                if (imageBytes != null)
                    imageView.setImageBitmap(
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }

            // TODO:  Fill image view with (no poster bmp)
        }
        finally {
            if(cursor != null)
                cursor.close();
        }


        return imageView;
    }

//        String fullPosterPathUrl =
//                mContext.getString(R.string.tmdb_base_image_url) +
//                        mContext.getString(R.string.tmdb_image_size_185) +
//                        mMovieItem.get(position).getPoster_path();
//
//
//        final Target target;
//
//        Picasso.with(mContext)
//            .load(fullPosterPathUrl)
//            .into( target = new Target() {
//                @Override
//                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
//
//                    byte[] imageBytes = getBitmapAsByteArray(bitmap);
//
//                    ContentValues values = new ContentValues();
//                    values.put(MoviesContract.MovieEntry.COLUMN_POSTER_BITMAP, imageBytes);
//
//                    mContext.getContentResolver().update(
//                            MoviesContract.MovieEntry.CONTENT_URI,
//                            values,
//                            MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
//                            new String[] {
//                                    String.valueOf(
//                                        mMovieItem.get(position).getId ()
//                                    )
//                            }
//                    );
//
//                    imageView.setImageBitmap(bitmap);
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//
//                    // TODO  Log Me  -- just in case these are failing
//                    Log.v(LOG_TAG, "*** Picasso failed to download the Bitmap.");
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            }
//        );
//
//        imageView.setTag(target);


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }
}
