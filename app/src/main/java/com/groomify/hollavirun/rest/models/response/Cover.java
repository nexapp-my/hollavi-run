
package com.groomify.hollavirun.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cover {

    @SerializedName("cover")
    @Expose
    private Cover_ cover;

    public Cover_ getCover() {
        return cover;
    }

    public void setCover(Cover_ cover) {
        this.cover = cover;
    }

}
