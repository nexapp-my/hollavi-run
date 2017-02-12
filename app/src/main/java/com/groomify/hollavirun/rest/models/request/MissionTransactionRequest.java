
package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MissionTransactionRequest implements Serializable, Parcelable
{

    @SerializedName("mission_transaction")
    @Expose
    private MissionTransaction missionTransaction;
    public final static Creator<MissionTransactionRequest> CREATOR = new Creator<MissionTransactionRequest>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MissionTransactionRequest createFromParcel(Parcel in) {
            MissionTransactionRequest instance = new MissionTransactionRequest();
            instance.missionTransaction = ((MissionTransaction) in.readValue((MissionTransaction.class.getClassLoader())));
            return instance;
        }

        public MissionTransactionRequest[] newArray(int size) {
            return (new MissionTransactionRequest[size]);
        }

    }
    ;
    private final static long serialVersionUID = 5777816197454696984L;

    public MissionTransaction getMissionTransaction() {
        return missionTransaction;
    }

    public void setMissionTransaction(MissionTransaction missionTransaction) {
        this.missionTransaction = missionTransaction;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(missionTransaction);
    }

    public int describeContents() {
        return  0;
    }

}
