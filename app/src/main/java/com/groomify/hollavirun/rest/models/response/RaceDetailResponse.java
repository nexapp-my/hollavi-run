
package com.groomify.hollavirun.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RaceDetailResponse extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Long id;
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
    @SerializedName("map")
    @Expose
    private Map map;
    @SerializedName("badge")
    @Expose
    private Badge badge;
    @SerializedName("start_point")
    @Expose
    private StartPoint startPoint;
    @SerializedName("end_point")
    @Expose
    private EndPoint endPoint;
    @SerializedName("mapInfo")
    @Expose
    private MapInfo mapInfo;
    @SerializedName("race_track_coordinates")
    @Expose
    private RealmList<RaceTrackCoordinate> raceTrackCoordinates = null;
    @SerializedName("missions")
    @Expose
    private RealmList<Mission_> missions = null;
    @SerializedName("url")
    @Expose
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public StartPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(StartPoint startPoint) {
        this.startPoint = startPoint;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPoint endPoint) {
        this.endPoint = endPoint;
    }

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
    }

    public RealmList<RaceTrackCoordinate> getRaceTrackCoordinates() {
        return raceTrackCoordinates;
    }

    public void setRaceTrackCoordinates(RealmList<RaceTrackCoordinate> raceTrackCoordinates) {
        this.raceTrackCoordinates = raceTrackCoordinates;
    }

    public RealmList<Mission_> getMissions() {
        return missions;
    }

    public void setMissions(RealmList<Mission_> missions) {
        this.missions = missions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
