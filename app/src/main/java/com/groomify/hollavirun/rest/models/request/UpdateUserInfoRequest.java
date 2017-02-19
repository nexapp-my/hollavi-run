package com.groomify.hollavirun.rest.models.request;


import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateUserInfoRequest implements Serializable, Parcelable
{

    @SerializedName("fb_user")
    @Expose
    private FbUser fbUser;
    public final static Parcelable.Creator<UpdateUserInfoRequest> CREATOR = new Creator<UpdateUserInfoRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UpdateUserInfoRequest createFromParcel(Parcel in) {
            UpdateUserInfoRequest instance = new UpdateUserInfoRequest();
            instance.fbUser = ((FbUser) in.readValue((FbUser.class.getClassLoader())));
            return instance;
        }

        public UpdateUserInfoRequest[] newArray(int size) {
            return (new UpdateUserInfoRequest[size]);
        }

    }
            ;
    private final static long serialVersionUID = -4909992672362671081L;

    public FbUser getFbUser() {
        return fbUser;
    }

    public void setFbUser(FbUser fbUser) {
        this.fbUser = fbUser;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(fbUser);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "UpdateUserInfoRequest{" +
                "fbUser=" + fbUser +
                '}';
    }
}