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
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Valkyrie1988 on 11/29/2016.
 */

public class CouponArrayAdapter extends ArrayAdapter<Coupon> {
    private final Context context;
    private final List<Coupon> values;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
    public CouponArrayAdapter(Context context, List<Coupon> values) {
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

        if(values != null && values.get(position) != null){
            Coupon coupon = values.get(position);
            couponDescTextView.setText(coupon.getDescription());
            couponTitleTextView.setText(coupon.getName());

            long millisDiff = coupon.getExpirationTime().getTime() - Calendar.getInstance().getTime().getTime();

            if(millisDiff > 0){
                long hours = TimeUnit.MILLISECONDS.toHours(millisDiff);
                if(hours > 24){
                    couponExpirationTextView.setText("Expiry Date: "+sdf.format(coupon.getExpirationTime()));
                }else{
                    if(hours > 0){
                        couponExpirationTextView.setText("Expires in "+hours+" hours");
                    }else{
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisDiff);
                        couponExpirationTextView.setText("Expires in "+minutes+" minutes");
                    }
                }

            }else{
                couponExpirationTextView.setText("Expired");
            }

            if(coupon.getResourceId() > -1){
                couponImageImageView.setImageResource(coupon.getResourceId());
            }else{
                ImageLoader.getInstance().displayImage(coupon.getCoverPhotoUrl(), couponImageImageView, ImageLoadUtils.getDisplayImageOptions());
            }
            /*if(coupon.getImageByteArr() != null){
                //couponImageImageView = new ImageView(this.getContext());
                Bitmap miniMapBitmap = BitmapFactory.decodeByteArray(coupon.getImageByteArr(), 0, coupon.getImageByteArr().length);
                couponImageImageView.setImageBitmap(miniMapBitmap);
            }*/
        }





        return rowView;
    }
}
