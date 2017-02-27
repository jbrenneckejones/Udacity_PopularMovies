package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jbren on 2/22/2017.
 */

public class MovieInfo implements Parcelable
{
    public int mMoviePage;
    public String mMoviePosterPath;
    public String mMovieOverview;

    public  String mMovieReleaseDate;

    public String mMovieTitle;
    public float mMoviePopularity;

    public int mMovieVotes;
    public float mMovieAverage;

    public MovieInfo()
    {

    }

    protected MovieInfo(Parcel in) {
        mMoviePage = in.readInt();
        mMoviePosterPath = in.readString();
        mMovieOverview = in.readString();
        mMovieReleaseDate = in.readString();
        mMovieTitle = in.readString();
        mMoviePopularity = in.readFloat();
        mMovieVotes = in.readInt();
        mMovieAverage = in.readFloat();
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    public String GetMovieInfo()
    {
        return "Title: " + mMovieTitle + "\n\n" +
                "Release Date: " + mMovieReleaseDate + "\n\n" +
                "Overview: " + mMovieOverview + "\n\n" +
                "Popularity: " + mMoviePopularity + "\n\n" +
                "Votes: " + mMovieVotes + "\n\n" +
                "Average: " + mMovieAverage;
    }

    public String ToString()
    {
        return "Page:" + mMoviePage + " - " + "Poster:" + mMoviePosterPath + " - " + "Overview:" + mMovieOverview +
                " - " + "Release Date:" + mMovieReleaseDate + " - " + "Title:" + mMovieTitle +
                " - " + "Popularity:" + mMoviePopularity + " - " + "Votes:" + mMovieVotes +
                " - " + "Average:" + mMovieAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mMoviePage);
        dest.writeString(mMoviePosterPath);
        dest.writeString(mMovieOverview);
        dest.writeString(mMovieReleaseDate);
        dest.writeString(mMovieTitle);
        dest.writeFloat(mMoviePopularity);
        dest.writeInt(mMovieVotes);
        dest.writeFloat(mMovieAverage);
    }
}
