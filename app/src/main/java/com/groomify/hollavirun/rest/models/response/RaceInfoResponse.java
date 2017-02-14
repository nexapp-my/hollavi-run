
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RaceInfoResponse implements Serializable, Parcelable
{

    @SerializedName("infos")
    @Expose
    private List<Info> infos = null;
    public final static Creator<RaceInfoResponse> CREATOR = new Creator<RaceInfoResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public RaceInfoResponse createFromParcel(Parcel in) {
            RaceInfoResponse instance = new RaceInfoResponse();
            in.readList(instance.infos, (com.groomify.hollavirun.rest.models.response.Info.class.getClassLoader()));
            return instance;
        }

        public RaceInfoResponse[] newArray(int size) {
            return (new RaceInfoResponse[size]);
        }

    }
    ;
    private final static long serialVersionUID = -3310163745639562511L;

    public List<Info> getInfos() {
        return infos;
    }

    public void setInfos(List<Info> infos) {
        this.infos = infos;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(infos);
    }

    public int describeContents() {
        return  0;
    }

}
