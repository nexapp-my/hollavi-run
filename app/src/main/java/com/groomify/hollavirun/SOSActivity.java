package com.groomify.hollavirun;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SOSActivity extends AppCompatActivity {

    Toolbar toolbar;

    private View firstAidPanel;
    private View emergencyContactPanel;
    private View groomifySupportPanel;
    private View setupEmergencyContactPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
/*

        if(firstAidPanel == null){
            firstAidPanel = findViewById(R.id.first_aid_panel);
        }

        if(emergencyContactPanel == null){
            emergencyContactPanel = findViewById(R.id.emergency_contact_panel);
        }

        if(groomifySupportPanel == null){
            groomifySupportPanel = findViewById(R.id.groomify_support_panel);
        }

        if(setupEmergencyContactPanel == null){
            setupEmergencyContactPanel = findViewById(R.id.set_emergency_contact_panel);
        }
*/

       // drawPanelBorder();

    }

    private void drawPanelBorder(){
        GradientDrawable border = new GradientDrawable();
        border.setColor(0xffffff); //white background
        border.setStroke(1, 0x979797); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            firstAidPanel.setBackgroundDrawable(border);
        } else {
            firstAidPanel.setBackground(border);
        }
    }
}
