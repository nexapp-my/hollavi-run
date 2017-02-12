
package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MissionTransaction implements Serializable, Parcelable
{

    @SerializedName("mission_id")
    @Expose
    private Integer missionId;
    @SerializedName("mission_time")
    @Expose
    private Integer missionTime;
    @SerializedName("runner_id")
    @Expose
    private Integer runnerId;
    @SerializedName("photos_attributes")
    @Expose
    private List<PhotosAttribute> photosAttributes = null;
    public final static Creator<MissionTransaction> CREATOR = new Creator<MissionTransaction>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MissionTransaction createFromParcel(Parcel in) {
            MissionTransaction instance = new MissionTransaction();
            instance.missionId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.missionTime = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.runnerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.photosAttributes, (com.groomify.hollavirun.rest.models.request.PhotosAttribute.class.getClassLoader()));
            return instance;
        }

        public MissionTransaction[] newArray(int size) {
            return (new MissionTransaction[size]);
        }

    }
    ;
    private final static long serialVersionUID = 54226719163284370L;

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }

    public Integer getMissionTime() {
        return missionTime;
    }

    public void setMissionTime(Integer missionTime) {
        this.missionTime = missionTime;
    }

    public Integer getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Integer runnerId) {
        this.runnerId = runnerId;
    }

    public List<PhotosAttribute> getPhotosAttributes() {
        return photosAttributes;
    }

    public void setPhotosAttributes(List<PhotosAttribute> photosAttributes) {
        this.photosAttributes = photosAttributes;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(missionId);
        dest.writeValue(missionTime);
        dest.writeValue(runnerId);
        dest.writeList(photosAttributes);
    }

    public int describeContents() {
        return  0;
    }

}
