package com.pingme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ConfigActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent serviceIntent = new Intent( "PING_USER_ACTION" );
        serviceIntent.setClassName( this, "com.pingme.PingMeService" );
        startService( serviceIntent );
    }
}