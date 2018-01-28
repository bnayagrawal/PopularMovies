package xyz.bnayagrawal.android.popularmovies.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by binay on 27/1/18.
 */

public class Configuration implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Configuration createFromParcel(Parcel in) {
            return new Configuration(in);
        }

        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
    private String base_url;
    private String secure_base_url;
    private String[] backdrop_sizes, logo_sizes, poster_sizes, profile_sizes, still_sizes;

    public Configuration(String base_url,
                         String secure_base_url,
                         String[] backdrop_sizes,
                         String[] logo_sizes,
                         String[] poster_sizes,
                         String[] profile_sizes,
                         String[] still_sizes) {
        this.base_url = base_url;
        this.secure_base_url = secure_base_url;
        this.backdrop_sizes = backdrop_sizes;
        this.logo_sizes = logo_sizes;
        this.poster_sizes = poster_sizes;
        this.profile_sizes = profile_sizes;
        this.still_sizes = still_sizes;
    }

    public Configuration(Parcel in) {
        this.base_url = in.readString();
        this.secure_base_url = in.readString();
        this.backdrop_sizes = in.createStringArray();
        this.logo_sizes = in.createStringArray();
        this.poster_sizes = in.createStringArray();
        this.profile_sizes = in.createStringArray();
        this.still_sizes = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.base_url);
        parcel.writeString(this.secure_base_url);
        parcel.writeStringArray(this.backdrop_sizes);
        parcel.writeStringArray(this.logo_sizes);
        parcel.writeStringArray(this.poster_sizes);
        parcel.writeStringArray(this.profile_sizes);
        parcel.writeStringArray(this.still_sizes);
    }

    public String getBase_url() {
        return this.base_url;
    }

    public String getSecure_base_url() {
        return this.secure_base_url;
    }

    public String[] getBackdrop_sizes() {
        return this.backdrop_sizes;
    }

    public String[] getLogo_sizes() {
        return this.logo_sizes;
    }

    public String[] getPoster_sizes() {
        return this.poster_sizes;
    }

    public String[] getProfile_sizes() {
        return this.profile_sizes;
    }

    public String[] getStill_sizes() {
        return this.still_sizes;
    }
}
