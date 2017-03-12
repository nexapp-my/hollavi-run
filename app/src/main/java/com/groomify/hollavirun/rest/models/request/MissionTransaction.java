
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
    @SerializedName("remark")
    @Expose
    private String remark;


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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.missionId);
        dest.writeValue(this.missionTime);
        dest.writeValue(this.runnerId);
        dest.writeTypedList(this.photosAttributes);
        dest.writeString(this.remark);
    }

    public MissionTransaction() {
    }

    protected MissionTransaction(Parcel in) {
        this.missionId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.missionTime = (Integer) in.readValue(Integer.class.getClassLoader());
        this.runnerId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.photosAttributes = in.createTypedArrayList(PhotosAttribute.CREATOR);
        this.remark = in.readString();
    }

    public static final Creator<MissionTransaction> CREATOR = new Creator<MissionTransaction>() {
        @Override
        public MissionTransaction createFromParcel(Parcel source) {
            return new MissionTransaction(source);
        }

        @Override
        public MissionTransaction[] newArray(int size) {
            return new MissionTransaction[size];
        }
    };
}
