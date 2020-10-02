package com.madehuge_nishant.videostreamer.DataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class promotion_option_list implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("data")
    @Expose
    private String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
