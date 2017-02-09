
package com.groomify.hollavirun.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mission {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cover_photo")
    @Expose
    private Object coverPhoto;
    @SerializedName("url")
    @Expose
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(Object coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
