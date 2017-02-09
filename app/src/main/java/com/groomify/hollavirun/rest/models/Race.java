package com.groomify.hollavirun.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Race {

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
    private Object endTime;
    @SerializedName("first_aid")
    @Expose
    private Object firstAid;
    @SerializedName("gr_support")
    @Expose
    private Object grSupport;
    @SerializedName("url")
    @Expose
    private String url;

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

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    public Object getFirstAid() {
        return firstAid;
    }

    public void setFirstAid(Object firstAid) {
        this.firstAid = firstAid;
    }

    public Object getGrSupport() {
        return grSupport;
    }

    public void setGrSupport(Object grSupport) {
        this.grSupport = grSupport;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
