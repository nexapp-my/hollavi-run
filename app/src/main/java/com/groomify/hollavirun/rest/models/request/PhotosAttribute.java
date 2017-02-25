
package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotosAttribute implements Serializable, Parcelable
{

    @SerializedName("race_id")
    @Expose
    private Long raceId;
    @SerializedName("runner_id")
    @Expose
    private Long runnerId;
    @SerializedName("content")
    @Expose
    private String content;

    public PhotosAttribute() {
    }

    public PhotosAttribute(String content, Long raceId, Long runnerId) {
        this.content = content;
        this.raceId = raceId;
        this.runnerId = runnerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public Long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Long runnerId) {
        this.runnerId = runnerId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.raceId);
        dest.writeValue(this.runnerId);
        dest.writeString(this.content);
    }

    protected PhotosAttribute(Parcel in) {
        this.raceId = (Long) in.readValue(Long.class.getClassLoader());
        this.runnerId = (Long) in.readValue(Long.class.getClassLoader());
        this.content = in.readString();
    }

    public static final Creator<PhotosAttribute> CREATOR = new Creator<PhotosAttribute>() {
        @Override
        public PhotosAttribute createFromParcel(Parcel source) {
            return new PhotosAttribute(source);
        }

        @Override
        public PhotosAttribute[] newArray(int size) {
            return new PhotosAttribute[size];
        }
    };
}
