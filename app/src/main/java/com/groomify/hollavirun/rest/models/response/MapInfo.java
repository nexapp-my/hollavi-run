
package com.groomify.hollavirun.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class MapInfo extends RealmObject {

    @SerializedName("mapCenter")
    @Expose
    private MapCenter mapCenter;
    @SerializedName("zoomLevel")
    @Expose
    private int zoomLevel;

    public MapCenter getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(MapCenter mapCenter) {
        this.mapCenter = mapCenter;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

}
