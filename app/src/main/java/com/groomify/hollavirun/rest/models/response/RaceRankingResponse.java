
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RaceRankingResponse implements Serializable, Parcelable
{

    @SerializedName("rankings")
    @Expose
    private List<Ranking> rankings = null;
    @SerializedName("my_ranking")
    @Expose
    private MyRanking myRanking;
    public final static Creator<RaceRankingResponse> CREATOR = new Creator<RaceRankingResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public RaceRankingResponse createFromParcel(Parcel in) {
            RaceRankingResponse instance = new RaceRankingResponse();
            in.readList(instance.rankings, (Ranking.class.getClassLoader()));
            instance.myRanking = ((MyRanking) in.readValue((MyRanking.class.getClassLoader())));
            return instance;
        }

        public RaceRankingResponse[] newArray(int size) {
            return (new RaceRankingResponse[size]);
        }

    }
    ;
    private final static long serialVersionUID = -1146495592337054261L;

    public List<Ranking> getRankings() {
        return rankings;
    }

    public void setRankings(List<Ranking> rankings) {
        this.rankings = rankings;
    }

    public MyRanking getMyRanking() {
        return myRanking;
    }

    public void setMyRanking(MyRanking myRanking) {
        this.myRanking = myRanking;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(rankings);
        dest.writeValue(myRanking);
    }

    public int describeContents() {
        return  0;
    }

}
