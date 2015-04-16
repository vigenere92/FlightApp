package com.flightapp.myapp;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.flightapp.myapp.notifications.AlarmReceiver;
import com.flightapp.myapp.objects.Flight;
import com.path.android.jobqueue.JobManager;

public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private PendingIntent pendingIntent;
    public static JobManager jobManager;
    public static ViewPager pager;
    public static final String goIbiboAppId = "4e8f09f4";
    public static final String goIbiboAppKey = "c6c4f9a49909cc2fa2b7d9a43dc0d95c";
    private Flight flight;
    private boolean isNewFlight = false;
    ViewPagerAdapter adapter;
    SlidingTabLayout slidingTabs;
    CharSequence tiles[] = {"New Flight", "All Flights", "Notifications"};
    int numOfTabs =3;

    public void setNewFlight( Flight flight ){
        this.flight = flight;
        this.isNewFlight = true;
    }

    public Flight getFlight(){
        return flight;
    }

    public void setBoolNewFlight( Boolean val ){
        this.isNewFlight = val;
    }

    public boolean isNewFlight(){
        return isNewFlight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent alarmIntent = new Intent( MainActivity.this, AlarmReceiver.class );
        pendingIntent = PendingIntent.getBroadcast( MainActivity.this, 0, alarmIntent, 0 );
        AlarmManager manager = ( AlarmManager ) getSystemService( Context.ALARM_SERVICE );
        int interval = 1000*60*5;
        manager.setRepeating( AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent );

        jobManager = new JobManager( this );
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), tiles, numOfTabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        slidingTabs.setDistributeEvenly(true);
        slidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.TabScrollBar);
            }
        });
        slidingTabs.setViewPager(pager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}



