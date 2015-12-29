package com.example.android.popularmovies.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class MovieReviewsCustomAdapter extends BaseAdapter {

    private List<MovieReviewsResponse.ResultsEntity> mReviewItem;
    private Context mContext;
    private LayoutInflater inflater;

    public MovieReviewsCustomAdapter(Context mContext, List<MovieReviewsResponse.ResultsEntity> mReviewItem) {
        this.mContext = mContext;
        this.mReviewItem = mReviewItem;
    }

    @Override
    public int getCount() {
        return mReviewItem.size();
    }

    @Override
    public Object getItem(int position) {
        return mReviewItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_movie_review, parent, false);
        MovieReviewsResponse.ResultsEntity item =
                (MovieReviewsResponse.ResultsEntity) getItem(position);

        TextView trailer = (TextView) rowView.findViewById(R.id.list_item_movie_review_textview);
        trailer.setText(item.getContent());

        return rowView;
    }
}
