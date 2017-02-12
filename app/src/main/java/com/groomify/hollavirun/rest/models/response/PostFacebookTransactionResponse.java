

package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostFacebookTransactionResponse implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("runner_id")
    @Expose
    private Integer runnerId;
    @SerializedName("race_id")
    @Expose
    private Integer raceId;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<PostFacebookTransactionResponse> CREATOR = new Creator<PostFacebookTransactionResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PostFacebookTransactionResponse createFromParcel(Parcel in) {
            PostFacebookTransactionResponse instance = new PostFacebookTransactionResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.runnerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.raceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.action = ((String) in.readValue((String.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PostFacebookTransactionResponse[] newArray(int size) {
            return (new PostFacebookTransactionResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -2725869117007458633L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Integer runnerId) {
        this.runnerId = runnerId;
    }

    public Integer getRaceId() {
        return raceId;
    }

    public void setRaceId(Integer raceId) {
        this.raceId = raceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(runnerId);
        dest.writeValue(raceId);
        dest.writeValue(action);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

}