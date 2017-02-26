package com.groomify.hollavirun;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridView;

import com.groomify.hollavirun.adapter.GridViewImageAdapter;
import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Mission;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.GalleryUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RunGalleryActivity extends AppCompatActivity {

    private static final String TAG = RunGalleryActivity.class.getSimpleName();
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private Toolbar toolbar;

    private Storage storage;
    private Mission[] missions;
    private Long userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_gallery);

        storage = SimpleStorage.getInternalStorage(this);

        missions = AppUtils.getDefaultMission();
        userId = SharedPreferencesHelper.getUserId(this);
        gridView = (GridView) findViewById(R.id.grid_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        for(Mission mission: missions){
            String directoryName = "Groomify_"+userId+"_Mission_"+mission.getId();
            List<File> files = storage.getFiles(directoryName, OrderType.NAME);
            Log.i(TAG, "Searching mission "+mission.getId()+" directory: "+directoryName);

            for(File file: files){
                Log.i(TAG, "File found, pushing to list:"+file.getAbsolutePath());
                imagePaths.add(file.getAbsolutePath());
            }

        }

        // Initilizing Grid View
        initilizeGridLayout();

        // loading all image paths from SD card
        //imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new GridViewImageAdapter(RunGalleryActivity.this, imagePaths,
                columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);
    }

    private void initilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((GalleryUtils.getScreenWidth(this) - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }
}
