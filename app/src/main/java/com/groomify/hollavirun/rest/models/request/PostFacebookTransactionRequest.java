package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostFacebookTransactionRequest implements Serializable, Parcelable
{

    @SerializedName("fb_transaction")
    @Expose
    private FbTransaction fbTransaction;
    public final static Parcelable.Creator<PostFacebookTransactionRequest> CREATOR = new Creator<PostFacebookTransactionRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PostFacebookTransactionRequest createFromParcel(Parcel in) {
            PostFacebookTransactionRequest instance = new PostFacebookTransactionRequest();
            instance.fbTransaction = ((FbTransaction) in.readValue((FbTransaction.class.getClassLoader())));
            return instance;
        }

        public PostFacebookTransactionRequest[] newArray(int size) {
            return (new PostFacebookTransactionRequest[size]);
        }

    }
            ;
    private final static long serialVersionUID = 7711780035356028405L;

    public FbTransaction getFbTransaction() {
        return fbTransaction;
    }

    public void setFbTransaction(FbTransaction fbTransaction) {
        this.fbTransaction = fbTransaction;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(fbTransaction);
    }

    public int describeContents() {
        return 0;
    }
}