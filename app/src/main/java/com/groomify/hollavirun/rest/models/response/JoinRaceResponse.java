package com.groomify.hollavirun.rest.models.response;
import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JoinRaceResponse implements Serializable, Parcelable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("mission_no")
    @Expose
    private Integer missionNo;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("runner_id")
    @Expose
    private Integer runnerId;
    public final static Parcelable.Creator<JoinRaceResponse> CREATOR = new Creator<JoinRaceResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public JoinRaceResponse createFromParcel(Parcel in) {
            JoinRaceResponse instance = new JoinRaceResponse();
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.location = ((String) in.readValue((String.class.getClassLoader())));
            instance.distance = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.missionNo = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.status = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.runnerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public JoinRaceResponse[] newArray(int size) {
            return (new JoinRaceResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -1070655777658237317L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getMissionNo() {
        return missionNo;
    }

    public void setMissionNo(Integer missionNo) {
        this.missionNo = missionNo;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Integer runnerId) {
        this.runnerId = runnerId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(location);
        dest.writeValue(distance);
        dest.writeValue(missionNo);
        dest.writeValue(status);
        dest.writeValue(runnerId);
    }

    public int describeContents() {
        return 0;
    }

}