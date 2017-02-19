
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyRanking implements Serializable, Parcelable
{

    @SerializedName("ranking")
    @Expose
    private Integer ranking;
    @SerializedName("runner_name")
    @Expose
    private String runnerName;
    @SerializedName("runner_bib")
    @Expose
    private String runnerBib;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("total_mission_time")
    @Expose
    private Integer totalMissionTime;
    public final static Creator<MyRanking> CREATOR = new Creator<MyRanking>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MyRanking createFromParcel(Parcel in) {
            MyRanking instance = new MyRanking();
            instance.ranking = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.runnerName = ((String) in.readValue((String.class.getClassLoader())));
            instance.runnerBib = ((String) in.readValue((String.class.getClassLoader())));
            instance.team = ((String) in.readValue((String.class.getClassLoader())));
            instance.totalMissionTime = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public MyRanking[] newArray(int size) {
            return (new MyRanking[size]);
        }

    }
    ;
    private final static long serialVersionUID = 4123279110324986606L;

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public String getRunnerBib() {
        return runnerBib;
    }

    public void setRunnerBib(String runnerBib) {
        this.runnerBib = runnerBib;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Integer getTotalMissionTime() {
        return totalMissionTime;
    }

    public void setTotalMissionTime(Integer totalMissionTime) {
        this.totalMissionTime = totalMissionTime;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(ranking);
        dest.writeValue(runnerName);
        dest.writeValue(runnerBib);
        dest.writeValue(team);
        dest.writeValue(totalMissionTime);
    }

    public int describeContents() {
        return  0;
    }

}
