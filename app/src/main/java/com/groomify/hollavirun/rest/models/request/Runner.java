package com.groomify.hollavirun.rest.models.request;


import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Runner implements Serializable, Parcelable
{

    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("run_bib_no")
    @Expose
    private String runBibNo;
    public final static Parcelable.Creator<Runner> CREATOR = new Creator<Runner>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Runner createFromParcel(Parcel in) {
            Runner instance = new Runner();
            instance.team = ((String) in.readValue((String.class.getClassLoader())));
            instance.runBibNo = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Runner[] newArray(int size) {
            return (new Runner[size]);
        }

    }
            ;
    private final static long serialVersionUID = 887780890408625772L;

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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(team);
        dest.writeValue(runBibNo);
    }

    public int describeContents() {
        return 0;
    }

}