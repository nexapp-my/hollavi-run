package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateRunnerInfoRequest implements Serializable, Parcelable
{

    @SerializedName("runner")
    @Expose
    private Runner runner;
    public final static Parcelable.Creator<UpdateRunnerInfoRequest> CREATOR = new Creator<UpdateRunnerInfoRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UpdateRunnerInfoRequest createFromParcel(Parcel in) {
            UpdateRunnerInfoRequest instance = new UpdateRunnerInfoRequest();
            instance.runner = ((Runner) in.readValue((Runner.class.getClassLoader())));
            return instance;
        }

        public UpdateRunnerInfoRequest[] newArray(int size) {
            return (new UpdateRunnerInfoRequest[size]);
        }

    }
            ;
    private final static long serialVersionUID = 33167147473735462L;

    public Runner getRunner() {
        return runner;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(runner);
    }

    public int describeContents() {
        return 0;
    }

}