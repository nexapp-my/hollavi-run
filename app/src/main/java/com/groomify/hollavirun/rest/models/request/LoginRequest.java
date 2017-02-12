package com.groomify.hollavirun.rest.models.request;
import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LoginRequest implements Serializable, Parcelable
{

    @SerializedName("fb_user")
    @Expose
    private FbUser fbUser;
    public final static Parcelable.Creator<LoginRequest> CREATOR = new Creator<LoginRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LoginRequest createFromParcel(Parcel in) {
            LoginRequest instance = new LoginRequest();
            instance.fbUser = ((FbUser) in.readValue((FbUser.class.getClassLoader())));
            return instance;
        }

        public LoginRequest[] newArray(int size) {
            return (new LoginRequest[size]);
        }

    }
            ;
    private final static long serialVersionUID = 8368603147745147367L;

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

}
