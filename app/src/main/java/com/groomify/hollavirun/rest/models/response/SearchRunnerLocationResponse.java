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
    @SerializedName("locations")
    @Expose
    private List<Location> locations = null;
    public final static Parcelable.Creator<SearchRunnerLocationResponse> CREATOR = new Creator<SearchRunnerLocationResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SearchRunnerLocationResponse createFromParcel(Parcel in) {
            SearchRunnerLocationResponse instance = new SearchRunnerLocationResponse();
            instance.team = ((String) in.readValue((String.class.getClassLoader())));
            instance.runBibNo = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.locations, (com.groomify.hollavirun.rest.models.response.Location.class.getClassLoader()));
            return instance;
        }

        public SearchRunnerLocationResponse[] newArray(int size) {
            return (new SearchRunnerLocationResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -4982914067482577723L;

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

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(team);
        dest.writeValue(runBibNo);
        dest.writeList(locations);
    }

    public int describeContents() {
        return 0;
    }

}