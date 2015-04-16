package com.flightapp.myapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ){
        Intent routineService = new Intent( context, RoutineService.class );
        context.startService( routineService );
    }
}
