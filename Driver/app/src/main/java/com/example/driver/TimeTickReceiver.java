package com.example.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TimeTickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_TIME_TICK.equals(intent.getAction())){
            Toast.makeText(context, "a minute has passed", Toast.LENGTH_SHORT).show();
        }
    }
}
