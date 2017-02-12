package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FbTransaction implements Serializable, Parcelable
{

    @SerializedName("race_id")
    @Expose
    private Integer raceId;
    @SerializedName("runner_id")
    @Expose
    private Integer runnerId;
    @SerializedName("action")
    @Expose
    private String action;
    public final static Parcelable.Creator<FbTransaction> CREATOR = new Creator<FbTransaction>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FbTransaction createFromParcel(Parcel in) {
            FbTransaction instance = new FbTransaction();
            instance.raceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.runnerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.action = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public FbTransaction[] newArray(int size) {
            return (new FbTransaction[size]);
        }

    }
            ;
    private final static long serialVersionUID = -6836008258752509088L;

    public Integer getRaceId() {
        return raceId;
    }

    public void setRaceId(Integer raceId) {
        this.raceId = raceId;
    }

    public Integer getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Integer runnerId) {
        this.runnerId = runnerId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(raceId);
        dest.writeValue(runnerId);
        dest.writeValue(action);
    }

    public int describeContents() {
        return 0;
    }

}