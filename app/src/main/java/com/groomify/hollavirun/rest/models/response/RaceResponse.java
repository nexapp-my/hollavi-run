package com.groomify.hollavirun.rest.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RaceResponse implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
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
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("first_aid")
    @Expose
    private String firstAid;
    @SerializedName("gr_support")
    @Expose
    private String grSupport;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<RaceResponse> CREATOR = new Creator<RaceResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RaceResponse createFromParcel(Parcel in) {
            RaceResponse instance = new RaceResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.location = ((String) in.readValue((String.class.getClassLoader())));
            instance.distance = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.missionNo = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.status = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.endTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.firstAid = ((String) in.readValue((String.class.getClassLoader())));
            instance.grSupport = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public RaceResponse[] newArray(int size) {
            return (new RaceResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -7788206753895020565L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(location);
        dest.writeValue(distance);
        dest.writeValue(missionNo);
        dest.writeValue(status);
        dest.writeValue(endTime);
        dest.writeValue(firstAid);
        dest.writeValue(grSupport);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

}