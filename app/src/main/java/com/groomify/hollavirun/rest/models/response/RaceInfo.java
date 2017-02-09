
package com.groomify.hollavirun.rest.models.response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RaceInfo {

    @SerializedName("infos")
    @Expose
    private List<Info> infos = null;

    public List<Info> getInfos() {
        return infos;
    }

    public void setInfos(List<Info> infos) {
        this.infos = infos;
    }

}
