package com.pingme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ConfigActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Change service state: on/off
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.statusService);
        togglebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (togglebutton.isChecked()) {
                    PingMeApplication.startApp();
                } else {
                	PingMeApplication.stopApp();
                }
            }
        });
        
        //Adapter to list of choices
        
    }
    
    
}