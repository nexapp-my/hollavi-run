package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MissionSubmissionResponse implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("mission_id")
    @Expose
    private Integer missionId;
    @SerializedName("mission_time")
    @Expose
    private Integer missionTime;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<MissionSubmissionResponse> CREATOR = new Creator<MissionSubmissionResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MissionSubmissionResponse createFromParcel(Parcel in) {
            MissionSubmissionResponse instance = new MissionSubmissionResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.missionId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.missionTime = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public MissionSubmissionResponse[] newArray(int size) {
            return (new MissionSubmissionResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = 3704485163873422435L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(missionId);
        dest.writeValue(missionTime);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

}