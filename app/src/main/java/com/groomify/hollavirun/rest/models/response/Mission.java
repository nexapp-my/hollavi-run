
package com.groomify.hollavirun.rest.models.response;
import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mission implements Serializable, Parcelable
{


    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cover_photo")
    @Expose
    private String coverPhoto;
    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("lat")
    @Expose
    private String latitude;

    @SerializedName("lng")
    @Expose
    private String longitude;

    private final static long serialVersionUID = -1434140419791461395L;

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Mission() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.coverPhoto);
        dest.writeString(this.url);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
    }

    protected Mission(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.coverPhoto = in.readString();
        this.url = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
    }

    public static final Creator<Mission> CREATOR = new Creator<Mission>() {
        @Override
        public Mission createFromParcel(Parcel source) {
            return new Mission(source);
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };
}