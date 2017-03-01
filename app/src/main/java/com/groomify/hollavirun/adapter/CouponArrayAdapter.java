package com.groomify.hollavirun.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.entities.Coupon;
import com.groomify.hollavirun.entities.Mission;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Valkyrie1988 on 11/29/2016.
 */

public class CouponArrayAdapter extends ArrayAdapter<Coupon> {
    private final Context context;
    private final Coupon[] values;

    public CouponArrayAdapter(Context context, Coupon[] values) {
        super(context, R.layout.item_coupon, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_coupon, parent, false);

        TextView couponDescTextView = (TextView) rowView.findViewById(R.id.coupon_desc_text_view);
        TextView couponTitleTextView = (TextView) rowView.findViewById(R.id.coupon_title_text_view);
        TextView couponExpirationTextView = (TextView) rowView.findViewById(R.id.coupon_expiration_text_view);
        ImageView couponImageImageView = (ImageView) rowView.findViewById(R.id.coupon_image_view);

        if(values != null && values[position] != null){
            Coupon coupon = values[position];
            couponDescTextView.setText(coupon.getDescription());
            couponTitleTextView.setText(coupon.getName());

            long millisDiff = coupon.getExpirationTime().getTime() - Calendar.getInstance().getTime().getTime();

            if(millisDiff > 0){
                long hours = TimeUnit.MICROSECONDS.toHours(millisDiff);
                couponExpirationTextView.setText("Expires in "+hours+" hours");
            }else{
                couponExpirationTextView.setText("Expired");
            }

            couponImageImageView.setImageResource(coupon.getResourceId());
            /*if(coupon.getImageByteArr() != null){
                //couponImageImageView = new ImageView(this.getContext());
                Bitmap miniMapBitmap = BitmapFactory.decodeByteArray(coupon.getImageByteArr(), 0, coupon.getImageByteArr().length);
                couponImageImageView.setImageBitmap(miniMapBitmap);
            }*/
        }





        return rowView;
    }
}
