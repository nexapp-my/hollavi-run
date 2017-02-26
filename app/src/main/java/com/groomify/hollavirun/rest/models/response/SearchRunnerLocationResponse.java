package com.groomify.hollavirun.rest.models.response;
import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchRunnerLocationResponse implements Serializable, Parcelable
{

    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("run_bib_no")
    @Expose
    private String runBibNo;
    @SerializedName("location")
    @Expose
    private Location location = null;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getRunBibNo() {
        return runBibNo;
    }

    public void setRunBibNo(String runBibNo) {
        this.runBibNo = runBibNo;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.team);
        dest.writeString(this.runBibNo);
        dest.writeParcelable(this.location, flags);
    }

    public SearchRunnerLocationResponse() {
    }

    protected SearchRunnerLocationResponse(Parcel in) {
        this.team = in.readString();
        this.runBibNo = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<SearchRunnerLocationResponse> CREATOR = new Creator<SearchRunnerLocationResponse>() {
        @Override
        public SearchRunnerLocationResponse createFromParcel(Parcel source) {
            return new SearchRunnerLocationResponse(source);
        }

        @Override
        public SearchRunnerLocationResponse[] newArray(int size) {
            return new SearchRunnerLocationResponse[size];
        }
    };
}