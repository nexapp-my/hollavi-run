package com.groomify.hollavirun.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.groomify.hollavirun.QRActivity;
import com.groomify.hollavirun.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MissionDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MissionDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MissionDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static final int QR_REQUEST = 111;

    private static final String TAG = MissionDetailsFragment.class.getSimpleName();

    ImageView imgPlaceHolderOne;
    ImageView imgPlaceHolderTwo;
    ImageView imgPlaceHolderThree;

    Button scanQRButton;

    boolean unlocked = false;

    public MissionDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MissionDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MissionDetailsFragment newInstance(String param1, String param2) {
        MissionDetailsFragment fragment = new MissionDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_mission_details, container, false);

        scanQRButton = (Button) view.findViewById(R.id.scan_qr_button);

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Scan QR button clicked", Toast.LENGTH_SHORT);
                requestQRCodeScan(v);
            }
        });

        imgPlaceHolderOne = (ImageView) view.findViewById(R.id.add_pic_placeholder1);
        imgPlaceHolderTwo = (ImageView) view.findViewById(R.id.add_pic_placeholder2);
        imgPlaceHolderThree = (ImageView) view.findViewById(R.id.add_pic_placeholder3);

        toggleMissionPanel();

        return view;
    }

    private void toggleMissionPanel(){
        if(!unlocked){
            imgPlaceHolderOne.setVisibility(View.INVISIBLE);
            imgPlaceHolderTwo.setVisibility(View.INVISIBLE);
            imgPlaceHolderThree.setVisibility(View.INVISIBLE);
            scanQRButton.setText("SCAN QR TO ACTIVE");
        }else{
            imgPlaceHolderOne.setVisibility(View.VISIBLE);
            imgPlaceHolderTwo.setVisibility(View.VISIBLE);
            imgPlaceHolderThree.setVisibility(View.VISIBLE);
            scanQRButton.setText("MISSION COMPLETE");
        }
    }

    public void requestQRCodeScan(View v) {
        Intent qrScanIntent = new Intent(getContext(), QRActivity.class);
        startActivityForResult(qrScanIntent, QR_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Yes it is called back");
        if (requestCode == QR_REQUEST) {
            Log.i(TAG, "Yes it is QR request.");
            String result;
            if (resultCode == Activity.RESULT_OK) {
                unlocked = true;
                toggleMissionPanel();
                result = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
                Log.i(TAG, "Yes it is success: "+ result);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT);
            } else {
                result = "Error";
            }
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT);
            /*mResultTextView.setText(result);
            mResultTextView.setVisibility(View.VISIBLE);*/
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
