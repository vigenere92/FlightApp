package com.flightapp.myapp.notifications;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flightapp.myapp.database.CityAndAirportsTable;
import com.flightapp.myapp.jobs.FlightRequestJob;
import com.path.android.jobqueue.JobManager;

public class RoutineService extends IntentService {

    public RoutineService(){
        super( "RoutineService" );
    }

    @Override
    protected void onHandleIntent( Intent intent ){
        JobManager jobManager = new JobManager( this );
        String[] allColumns = { CityAndAirportsTable.flightId,
                CityAndAirportsTable.fromDestinationCode, CityAndAirportsTable.toDestinationCode,
                CityAndAirportsTable.startDate, CityAndAirportsTable.endDate, CityAndAirportsTable.noOfStops, CityAndAirportsTable.depatureStartTime,
                CityAndAirportsTable.departureEndTime, CityAndAirportsTable.minPriceSet, CityAndAirportsTable.travelClass };
        CityAndAirportsTable dbHelper = new CityAndAirportsTable( getApplicationContext() );
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query( CityAndAirportsTable.flightTable, allColumns, null, null, null, null, "Id DESC" );
        cursor.moveToFirst();
        while( !cursor.isAfterLast() ){
            int flightId = cursor.getInt( 0 );
            String fromDestinationCode = cursor.getString( 1 );
            String toDestinationCode = cursor.getString( 2 );
            String startDate = cursor.getString( 3 );
            String endDate = cursor.getString( 4 );
            int noOfStops = cursor.getInt( 5 );
            int departureStartTime = cursor.getInt( 6 );
            int departureEndTime = cursor.getInt( 7 );
            int minPriceSet = cursor.getInt( 8 );
            String travelClass = cursor.getString( 9 );
            jobManager.addJobInBackground( new FlightRequestJob( flightId, fromDestinationCode, toDestinationCode, startDate,
                    endDate, minPriceSet, travelClass, noOfStops, departureStartTime, departureEndTime ) );
            cursor.moveToNext();
        }
    }
}
