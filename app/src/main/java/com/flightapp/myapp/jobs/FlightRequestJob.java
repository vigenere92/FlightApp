package com.flightapp.myapp.jobs;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flightapp.myapp.MainActivity;
import com.flightapp.myapp.MyAppApplication;
import com.flightapp.myapp.R;
import com.flightapp.myapp.basespicemanager.FlightRequest;
import com.flightapp.myapp.database.CityAndAirportsTable;
import com.flightapp.myapp.objects.Flight;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlightRequestJob extends Job {
    public static final int PRIORITY = 1;
    private int flightId;
    private String fromDestinationCode;
    private String toDestinationCode;
    private String startDate;
    private String endDate;
    private int minPriceSet;
    private String travelClass;
    private int noOfStops;
    private int departureStartTime;
    private int departureEndTime;

    public FlightRequestJob( int flightId, String fromDestinationCode, String toDestinationCode, String startDate, String endDate,
                             int minPriceSet, String travelClass, int noOfStops, int departureStartTime, int departureEndTime ){
        super(new Params(PRIORITY).requireNetwork().persist());
        this.flightId = flightId;
        this.fromDestinationCode = fromDestinationCode;
        this.toDestinationCode = toDestinationCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minPriceSet = minPriceSet;
        this.travelClass = travelClass;
        this.noOfStops = noOfStops;
        this.departureStartTime = departureStartTime;
        this.departureEndTime = departureEndTime;
    }

    @Override
    public void onAdded() {
        // Job has been saved to disk.
        // This is a good place to dispatch a UI event to indicate the job will eventually run.
        // In this example, it would be good to update the UI with the newly posted tweet.
    }

    @Override
    public void onRun() throws Throwable {
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date flightStartDate = new Date();
        Date flightEndDate = new Date();
        try {
            flightStartDate = format.parse( startDate );
            flightEndDate = format.parse( endDate );
        }
        catch ( java.text.ParseException e ){
            e.printStackTrace();
        }

        ArrayList< Date > flightDates = new ArrayList< Date >();

        //Get the dates between start date and end date
        Calendar calStartDate = new GregorianCalendar();
        Calendar calEndDate = new GregorianCalendar();
        calStartDate.setTime( flightStartDate );
        calEndDate.setTime( flightEndDate );
        while( !calStartDate.after( calEndDate ) ){
            flightDates.add( calStartDate.getTime() );
            calStartDate.add( Calendar.DATE, 1 );
        }

        int currentMinPrice = 999999;
        HttpClient httpClient = new DefaultHttpClient();
        HttpEntity httpEntity = new HttpEntity() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public long getContentLength() {
                return 0;
            }

            @Override
            public Header getContentType() {
                return null;
            }

            @Override
            public Header getContentEncoding() {
                return null;
            }

            @Override
            public InputStream getContent() throws IOException, IllegalStateException {
                return null;
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {

            }

            @Override
            public boolean isStreaming() {
                return false;
            }

            @Override
            public void consumeContent() throws IOException {

            }
        };

        String airline = "";
        String totalFare = "";
        String departureDateTime = "";
        String arrivalDateTime = "";
        String noOfStops = "";
        String duration = "";
        String seatsAvailable = "";

        FlightRequest.FlightDetails minPriceFlight = null;
        Boolean minPriceFlightSet = false;

        //First Check using the minimum price api
        String startDate = sdf.format(  flightStartDate );
        String endDate = sdf.format( flightEndDate );
        String minPriceQuery = "http://developer.goibibo.com/api/stats/minfare/?app_id=" + MainActivity.goIbiboAppId +
                "&app_key=" + MainActivity.goIbiboAppKey + "&format=json&vertical=flight&source=" + fromDestinationCode.toUpperCase() +
                "&destination=" + toDestinationCode + "&sdate=" + startDate + "&edate=" + endDate + "&class=E";
        try {
            HttpResponse response = httpClient.execute( new HttpGet( minPriceQuery ) );
            httpEntity = response.getEntity();
        }
        catch ( IOException e ){

        }
        String minPriceResult = "";
        try {
            minPriceResult = EntityUtils.toString(httpEntity);
        }
        catch (Exception e){

        }

        JsonObject minPriceFlightObject = null;
        JsonObject object = new JsonParser().parse( minPriceResult ).getAsJsonObject();
        Set minPriceFlights  = object.entrySet();
        int minFare = 999999;
        for( int i=1; i <= minPriceFlights.size(); i++ ){
            String resource = "resource" + String.valueOf( i );
            JsonObject flightResource = object.getAsJsonObject( resource );
            if( Float.valueOf( flightResource.get( "fare" ).toString() ) < minFare  ) {
                minFare = Math.round( Float.valueOf( flightResource.get( "fare" ).toString() ) );
                minPriceFlightObject = flightResource;
            }
        }

        if( minPriceFlightObject  != null ){
            String extras = minPriceFlightObject.getAsJsonPrimitive( "extra" ).toString();
            Pattern deptimePattern = Pattern.compile( ".+\'deptime\': u\'(.+?)\',.*" );
            Pattern arrtimePattern = Pattern.compile( ".+\'arrtime\': u\'(.+?)\',.*" );
            Pattern durationPattern = Pattern.compile( ".+\'duration\': u\'(.+?)\',.*" );
            Pattern noStopsPattern = Pattern.compile( ".+\'nostops\': (.+?),.*" );
            Matcher match = deptimePattern.matcher( extras );
            if( match.find() ){
                departureDateTime = match.group( 1 );

            }
            match = arrtimePattern.matcher( extras );
            if( match.find() ){
                arrivalDateTime = match.group( 1 );

            }
            match = durationPattern.matcher( extras );
            if( match.find() ){
                duration= match.group( 1 );

            }
            match = noStopsPattern.matcher( extras );
            if( match.find() ){
                noOfStops = match.group( 1 );

            }
            airline = ( ( minPriceFlightObject.get( "carrier" ) ).toString() ).replace( "\"", "" );
            totalFare = ( minPriceFlightObject.get( "fare" ) ).toString();
            currentMinPrice = Math.round( Float.valueOf( totalFare ) );
            minPriceFlightSet = true;

        }

        if( !minPriceFlightSet ){
            for( Date date : flightDates ){
                String fDate = sdf.format( date );
                String request = "http://developer.goibibo.com/api/search/?app_id=" + MainActivity.goIbiboAppId + "&app_key=" + MainActivity.goIbiboAppKey +
                        "&format=json&source=" + fromDestinationCode.toUpperCase() + "&destination=" + toDestinationCode.toUpperCase() + "&dateofdeparture=" +
                        fDate + "&seatingclass=E&adults=1&children=0&infants=0";
                try {
                    HttpResponse response = httpClient.execute( new HttpGet( request ));
                    httpEntity = response.getEntity();
                }
                catch ( IOException e ){

                }
                String responseStr = "";
                try {
                    responseStr = EntityUtils.toString(httpEntity);
                }
                catch (Exception e){

                }
                Gson gson = new Gson();
                FlightRequest flightRequest = gson.fromJson( responseStr, FlightRequest.class );
                ArrayList< FlightRequest.FlightDetails > onwardFlights = ( flightRequest.getData() ).getFlightDetails();
                for( int i=0; i<onwardFlights.size(); i++ ){
                    String fare = ((onwardFlights.get(i)).getFare()).getTotalfare().toString();
                    int price = Math.round(Float.valueOf( fare ));
                    if( price < currentMinPrice ){
                        currentMinPrice = price;
                        minPriceFlight = onwardFlights.get( i );
                        minPriceFlightSet = true;
                    }
                }
            }
        }

        if( currentMinPrice < minPriceSet && minPriceFlightSet && minPriceFlight != null ){
            CityAndAirportsTable dbHelper = new CityAndAirportsTable( MyAppApplication.getInstance().getApplicationContext());
            SQLiteDatabase database = dbHelper.getReadableDatabase();

            //Fetch the attributes of the flight with the minimum price
            airline = minPriceFlight.getAirline();
            totalFare = minPriceFlight.getFare().getTotalfare().toString();
            departureDateTime = minPriceFlight.getDepdate();
            arrivalDateTime = minPriceFlight.getArrdate();
            noOfStops = minPriceFlight.getStops();
            duration = minPriceFlight.getDuration();
            seatsAvailable = minPriceFlight.getSeatsavailable();

            String whereClause =  CityAndAirportsTable.airline + " = '" + airline + "' AND ";
            whereClause +=  CityAndAirportsTable.totalFare + " = '" + totalFare + "' AND ";
            whereClause +=  CityAndAirportsTable.departureDateTime + " = '" + departureDateTime + "' AND ";
            whereClause +=  CityAndAirportsTable.arrivalDateTime + " = '" + arrivalDateTime + "' AND ";
            whereClause +=  CityAndAirportsTable.noOfStops + " = '" + noOfStops + "' AND ";
            whereClause +=  CityAndAirportsTable.duration + " = '" + duration + "'";

            Cursor cursor = database.query( CityAndAirportsTable.minPriceFlightsTable, new String []{ CityAndAirportsTable.minPriceFlightId },
                   whereClause, null, null, null, null );

            long id;
            //Check if rows returned is null, if yes insert the new entry
            if( cursor != null && cursor.moveToFirst() ){
                id = cursor.getInt(cursor.getColumnIndex(CityAndAirportsTable.minPriceFlightId));
            }
            else{
                ContentValues contentValues = new ContentValues();
                contentValues.put( CityAndAirportsTable.airline, airline );
                contentValues.put( CityAndAirportsTable.totalFare, totalFare );
                contentValues.put( CityAndAirportsTable.departureDateTime, departureDateTime );
                contentValues.put( CityAndAirportsTable.arrivalDateTime, arrivalDateTime );
                contentValues.put( CityAndAirportsTable.noOfStops, noOfStops );
                contentValues.put( CityAndAirportsTable.duration, duration );
                contentValues.put( CityAndAirportsTable.seatsAvailable, seatsAvailable );
                id = database.insert( CityAndAirportsTable.minPriceFlightsTable, null, contentValues );
            }

            ContentValues idAndPrice = new ContentValues();
            idAndPrice.put( CityAndAirportsTable.minPFlightId, id );
            idAndPrice.put( CityAndAirportsTable.currentPrice, currentMinPrice );
            database.update( CityAndAirportsTable.flightTable, idAndPrice, CityAndAirportsTable.flightId + "=" + flightId, null );

            //Add notification for this in the database
            ContentValues notificationContent = new ContentValues();
            notificationContent.put( CityAndAirportsTable.notifyFlightId, flightId );
            database.insert(CityAndAirportsTable.notificationsTable, null, notificationContent);
            database.close();

            //Create the notification
            NotificationManager notificationManager = ( NotificationManager ) MyAppApplication.getInstance().getSystemService( Context.NOTIFICATION_SERVICE );
            String notificationText = "Current Price for " + fromDestinationCode + "-" + toDestinationCode + " is " + currentMinPrice;
            //Check sdk level and call build() instead
            Notification notification = new Notification.Builder( MyAppApplication.getInstance().getApplicationContext() )
                    .setContentTitle("Minimum Price Reached Alert")
                    .setContentText(notificationText)
                    .setAutoCancel(true)
                    .setSmallIcon( R.drawable.ic_launcher )
                    .getNotification();
            notificationManager.notify( 0, notification );
        }


    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        // An error occurred in onRun.
        // Return value determines whether this job should retry running (true) or abort (false).
        return true;
    }

    @Override
    protected void onCancel() {
        // Job has exceeded retry attempts or shouldReRunOnThrowable() has returned false.
    }
}


