
package com.groomify.hollavirun.rest.models.response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponsResponse {

    @SerializedName("coupons")
    @Expose
    private List<Coupon> coupons = null;

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

}
