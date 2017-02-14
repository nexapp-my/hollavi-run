
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RaceDetailResponse implements Serializable, Parcelable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("distance")
    @Expose
    private Long distance;
    @SerializedName("mission_no")
    @Expose
    private Long missionNo;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("first_aid")
    @Expose
    private String firstAid;
    @SerializedName("gr_support")
    @Expose
    private String grSupport;
    @SerializedName("missions")
    @Expose
    private List<Mission> missions = null;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Creator<RaceDetailResponse> CREATOR = new Creator<RaceDetailResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public RaceDetailResponse createFromParcel(Parcel in) {
            RaceDetailResponse instance = new RaceDetailResponse();
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.location = ((String) in.readValue((String.class.getClassLoader())));
            instance.distance = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.missionNo = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.status = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.endTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.firstAid = ((String) in.readValue((String.class.getClassLoader())));
            instance.grSupport = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.missions, (Mission.class.getClassLoader()));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public RaceDetailResponse[] newArray(int size) {
            return (new RaceDetailResponse[size]);
        }

    }
    ;
    private final static long serialVersionUID = 1691735398046549404L;

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

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getMissionNo() {
        return missionNo;
    }

    public void setMissionNo(Long missionNo) {
        this.missionNo = missionNo;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFirstAid() {
        return firstAid;
    }

    public void setFirstAid(String firstAid) {
        this.firstAid = firstAid;
    }

    public String getGrSupport() {
        return grSupport;
    }

    public void setGrSupport(String grSupport) {
        this.grSupport = grSupport;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public void setMissions(List<Mission> missions) {
        this.missions = missions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(location);
        dest.writeValue(distance);
        dest.writeValue(missionNo);
        dest.writeValue(status);
        dest.writeValue(endTime);
        dest.writeValue(firstAid);
        dest.writeValue(grSupport);
        dest.writeList(missions);
        dest.writeValue(url);
    }

    public int describeContents() {
        return  0;
    }

}
