package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jbren on 3/13/2017.
 */

public class MovieReviewInfo implements Parcelable
{
    public String mAuthor;
    public String mContent;

    public MovieReviewInfo()
    {
    }

    protected MovieReviewInfo(Parcel in)
    {
        mAuthor = in.readString();
        mContent = in.readString();
    }

    public static final Creator<MovieReviewInfo> CREATOR = new Creator<MovieReviewInfo>() {
        @Override
        public MovieReviewInfo createFromParcel(Parcel in) {
            return new MovieReviewInfo(in);
        }

        @Override
        public MovieReviewInfo[] newArray(int size) {
            return new MovieReviewInfo[size];
        }
    };

    public String GetReviewInfo()
    {
        return "Author: " + mAuthor + "\n\n" +
                "Content: " + mContent + "\n\n";
    }

    public String ToString()
    {
        return GetReviewInfo();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }
}