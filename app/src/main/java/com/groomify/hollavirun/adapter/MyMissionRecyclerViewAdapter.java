package com.groomify.hollavirun.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.groomify.hollavirun.R;
import com.groomify.hollavirun.fragment.MissionListFragment.OnListFragmentInteractionListener;
import com.groomify.hollavirun.fragment.dummy.MissionContent.MissionItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MissionItem} and makes a call to the
 * specified {@link com.groomify.hollavirun.fragment.MissionListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMissionRecyclerViewAdapter extends RecyclerView.Adapter<MyMissionRecyclerViewAdapter.ViewHolder> {

    private final List<MissionItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMissionRecyclerViewAdapter(List<MissionItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public MissionItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.mission_item_number);
            mContentView = (TextView) view.findViewById(R.id.mission_item_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
