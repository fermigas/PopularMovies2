package com.example.android.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    int[]   genreIds;
    String  adult;
    String  originalLanguage;
    String  posterPath;
    String  overview;
    String  releaseDate;
    String  id;
    String  title;
    String  video;
    int     voteCount;
    double  voteAverage;

    public Movie (
            int[]   genreIds,
            String  adult,
            String  originalLanguage,
            String  posterPath,
            String  overview,
            String  releaseDate,
            String  id,
            String  title,
            String  video,
            int     voteCount,
            double  voteAverage
    ){
       this.genreIds = genreIds;
       this.adult = adult;
       this.originalLanguage = originalLanguage;
       this.posterPath = posterPath;
       this.overview = overview;
       this.releaseDate = releaseDate;
       this.id = id;
       this.title = title;
       this.video = video;
       this.voteCount = voteCount;
       this.voteAverage = voteAverage;

    }


    private Movie(Parcel in){
        genreIds = in.createIntArray();
        adult = in.readString();
        originalLanguage = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        id = in.readString();
        title = in.readString();
        video = in.readString ();
        voteCount = in.readInt();
        voteAverage = in.readDouble();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(genreIds);
        dest.writeString(adult);
        dest.writeString(originalLanguage);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(video);
        dest.writeInt(voteCount);
        dest.writeDouble(voteAverage);
    }

    public static final Parcelable.Creator<Movie> CREATOR= new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);  //using parcelable constructor
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
