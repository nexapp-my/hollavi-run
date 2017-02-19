package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RunnerInfoResponse implements Serializable, Parcelable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("run_bib_no")
    @Expose
    private String runBibNo;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<RunnerInfoResponse> CREATOR = new Creator<RunnerInfoResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RunnerInfoResponse createFromParcel(Parcel in) {
            RunnerInfoResponse instance = new RunnerInfoResponse();
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.team = ((String) in.readValue((String.class.getClassLoader())));
            instance.runBibNo = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public RunnerInfoResponse[] newArray(int size) {
            return (new RunnerInfoResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = 7482769033941443971L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getRunBibNo() {
        return runBibNo;
    }

    public void setRunBibNo(String runBibNo) {
        this.runBibNo = runBibNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(team);
        dest.writeValue(runBibNo);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "RunnerInfoResponse{" +
                "name='" + name + '\'' +
                ", team='" + team + '\'' +
                ", runBibNo='" + runBibNo + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}