
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
    private Integer raceId;
    @SerializedName("runner_id")
    @Expose
    private Integer runnerId;
    @SerializedName("content")
    @Expose
    private String content;
    public final static Creator<PhotosAttribute> CREATOR = new Creator<PhotosAttribute>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PhotosAttribute createFromParcel(Parcel in) {
            PhotosAttribute instance = new PhotosAttribute();
            instance.raceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.runnerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.content = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PhotosAttribute[] newArray(int size) {
            return (new PhotosAttribute[size]);
        }

    }
    ;
    private final static long serialVersionUID = 7854938256896548285L;

    public Integer getRaceId() {
        return raceId;
    }

    public void setRaceId(Integer raceId) {
        this.raceId = raceId;
    }

    public Integer getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Integer runnerId) {
        this.runnerId = runnerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(raceId);
        dest.writeValue(runnerId);
        dest.writeValue(content);
    }

    public int describeContents() {
        return  0;
    }

}
