package xyz.bnayagrawal.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by binay on 27/1/18.
 */

public class Movie implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private int id;
    private String mTitle;
    private String mPosterPath;
    private String mOverview;
    private double mVoteAverage;
    private String mReleaseDate;

    //public constructor for this class
    public Movie(int id, String title, String posterPath, String overview, double voteAverage, String releaseDate) {
        this.id = id;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mOverview = overview;
        this.mVoteAverage = voteAverage;
        this.mReleaseDate = releaseDate;
    }

    public Movie(Parcel in) {
        this.mTitle = in.readString();
        this.mPosterPath = in.readString();
        this.mOverview = in.readString();
        this.mVoteAverage = in.readDouble();
        this.mReleaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mPosterPath);
        parcel.writeString(this.mOverview);
        parcel.writeDouble(this.mVoteAverage);
        parcel.writeString(this.mReleaseDate);
    }

    //Getter methods
    public String getTitle() {
        return this.mTitle;
    }

    public String getPosterPath() {
        return this.mPosterPath;
    }

    public String getOverview() {
        return this.mOverview;
    }

    public double getVoteAverage() {
        return this.mVoteAverage;
    }

    public String getReleaseDate() {
        return this.mReleaseDate;
    }

    public enum SortOrder {
        POPULAR_MOVIES,
        TOP_RATED_MOVIES
    }
}
