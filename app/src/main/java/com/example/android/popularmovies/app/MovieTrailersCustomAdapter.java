package com.example.android.popularmovies.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MovieTrailersCustomAdapter extends BaseAdapter {

    private List<MovieTrailersResponse.YoutubeEntity> mTrailerItem;
    private Context mContext;
    private LayoutInflater inflater;

    public MovieTrailersCustomAdapter(Context mContext, List<MovieTrailersResponse.YoutubeEntity> mTrailerItem) {
        this.mContext = mContext;
        this.mTrailerItem = mTrailerItem;
    }

    @Override
    public int getCount() {
        return mTrailerItem.size();
    }

    @Override
    public Object getItem(int position) {
        return mTrailerItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_movie_trailer, parent, false);
        MovieTrailersResponse.YoutubeEntity item =
                (MovieTrailersResponse.YoutubeEntity) getItem(position);

        TextView trailer = (TextView) rowView.findViewById(R.id.list_item_movie_trailer_textview);
       //  trailer.setText("https://www.youtube.com/watch?v=" + item.getSource());
       // trailer.setText(item.getName());


        String uri = "@drawable/play_icon.png";
        int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
        ImageView playButton = (ImageView) rowView.findViewById(R.id.list_item_movie_trailer_imageview);
        Drawable res = mContext.getResources().getDrawable(imageResource);
        playButton.setImageDrawable(res);


        return rowView;
    }
}
