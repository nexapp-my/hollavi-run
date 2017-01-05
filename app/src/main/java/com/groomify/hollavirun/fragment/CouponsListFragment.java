package com.groomify.hollavirun.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.groomify.hollavirun.CouponDetailsActivity;
import com.groomify.hollavirun.R;
import com.groomify.hollavirun.adapter.CouponArrayAdapter;
import com.groomify.hollavirun.entities.Coupon;
import com.groomify.hollavirun.utils.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;


public class CouponsListFragment extends ListFragment {
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    //private OnListFragmentInteractionListener mListener;

    public static View viewInstance;


    Coupon[] coupons = null;


    public CouponsListFragment() {
        // Required empty public constructor
    }


    public static CouponsListFragment newInstance(int columnCount) {
        CouponsListFragment fragment = new CouponsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 3);


        Bitmap tshirt = BitmapUtils.cropBitmap(72, 72, BitmapFactory.decodeResource(getResources(), R.drawable.t_shirt));
        Bitmap goodieBag = BitmapUtils.cropBitmap(72, 72, BitmapFactory.decodeResource(getResources(), R.drawable.goodie_bag));
        Bitmap firstAidKit = BitmapUtils.cropBitmap(72, 72, BitmapFactory.decodeResource(getResources(), R.drawable.first_aid_kit));
        Bitmap milo = BitmapUtils.cropBitmap(72, 72, BitmapFactory.decodeResource(getResources(), R.drawable.milo));

        ByteArrayOutputStream tshirtByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream goodieBagByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream firstAidKitByteArr = new ByteArrayOutputStream();
        ByteArrayOutputStream miloByteArr = new ByteArrayOutputStream();

        tshirt.compress(Bitmap.CompressFormat.PNG, 50, tshirtByteArr);
        goodieBag.compress(Bitmap.CompressFormat.PNG, 50, goodieBagByteArr);
        firstAidKit.compress(Bitmap.CompressFormat.PNG, 50, firstAidKitByteArr);
        milo.compress(Bitmap.CompressFormat.PNG, 50, miloByteArr);

        coupons = new Coupon[]{
                new Coupon(1, "Free Milo Drink", "Energise yourself", cal.getTime(), miloByteArr.toByteArray()),
                new Coupon(2, "Goodie Bag", "Everything is inside", cal.getTime(), goodieBagByteArr.toByteArray()),
                new Coupon(3, "First Aid Kit", "Help yourself", cal.getTime(), firstAidKitByteArr.toByteArray()),
                new Coupon(4, "Groomify T-Shirt", "Signature T-Shirt", cal.getTime(), tshirtByteArr.toByteArray())
        };

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        setListAdapter(new CouponArrayAdapter(this.getContext(), coupons));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coupons_list, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getContext(),"Clicked on coupon "+position, Toast.LENGTH_SHORT );

        Intent intent = new Intent(getContext(), CouponDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("COUPON", coupons[position]);
        intent.putExtra("EXTRA_COUPON", bundle);
        startActivity(intent);

    }
}
