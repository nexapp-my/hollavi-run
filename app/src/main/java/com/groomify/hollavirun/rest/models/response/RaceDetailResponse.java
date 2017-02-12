
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RaceDetailResponse implements Serializable, Parcelable
{

    @SerializedName("infos")
    @Expose
    private List<Info> infos = null;
    public final static Creator<RaceDetailResponse> CREATOR = new Creator<RaceDetailResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public RaceDetailResponse createFromParcel(Parcel in) {
            RaceDetailResponse instance = new RaceDetailResponse();
            in.readList(instance.infos, (com.groomify.hollavirun.rest.models.response.Info.class.getClassLoader()));
            return instance;
        }

        public RaceDetailResponse[] newArray(int size) {
            return (new RaceDetailResponse[size]);
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
