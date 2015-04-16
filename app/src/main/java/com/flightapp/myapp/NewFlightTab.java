package com.flightapp.myapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.Dialog;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.flightapp.myapp.basespicemanager.FlightRequest;
import com.flightapp.myapp.basespicemanager.MinPriceResource;
import com.flightapp.myapp.basespicemanager.ResourceSerializer;
import com.flightapp.myapp.database.CityAndAirportsTable;
import com.flightapp.myapp.jobs.FlightRequestJob;
import com.flightapp.myapp.objects.Flight;
import com.flightapp.myapp.seekbar.RangeSeekBar;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import com.flightapp.myapp.citiesAndAirports.CitiesAndAirport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewFlightTab extends Fragment implements OnDateSetListener {

    private MaterialDialog progressDialog;
    private CityAndAirportsTable dbHelper;
    private SQLiteDatabase database;
    public static String city = "Delhi";
    public static String airportCode = "DLI";
    private TextView cityTextView;
    private TextView codeTextView;
    private TextView day;
    private TextView date;
    private TextView month;
    private TextView year;
    private TextView fromYear, toYear;
    public static final String datePickerTag = "datepicker";
    private String[] daysOfWeek  = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
    private String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL","AUG", "SEP", "OCT", "NOV", "DEC" };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.new_flight,container,false);
        return v;
    }

    class GetLowestPrices extends AsyncTask< Flight, String, Boolean>{

        @Override
        public Boolean doInBackground( Flight... flights ){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Flight flight = flights[0];
            ArrayList< Date > flightDates = new ArrayList< Date >();

            //Get the dates between start date and end date
            Calendar calStartDate = new GregorianCalendar();
            Calendar calEndDate = new GregorianCalendar();
            calStartDate.setTime( flight.startDate );
            calEndDate.setTime( flight.endDate );
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
            String startDate = sdf.format(  flight.startDate );
            String endDate = sdf.format( flight.endDate );
            String minPriceQuery = "http://developer.goibibo.com/api/stats/minfare/?app_id=" + MainActivity.goIbiboAppId +
                    "&app_key=" + MainActivity.goIbiboAppKey + "&format=json&vertical=flight&source=" + flight.fromDestinationCode.toUpperCase() +
                    "&destination=" + flight.toDestinationCode + "&sdate=" + startDate + "&edate=" + endDate + "&class=E";
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
                minPriceFlightSet = true;

            }

            if( !minPriceFlightSet ){
                for( Date date : flightDates ){
                    String fDate = sdf.format( date );
                    String request = "http://developer.goibibo.com/api/search/?app_id=" + MainActivity.goIbiboAppId + "&app_key=" + MainActivity.goIbiboAppKey +
                            "&format=json&source=" + flight.fromDestinationCode.toUpperCase() + "&destination=" + flight.toDestinationCode.toUpperCase() + "&dateofdeparture=" +
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

            if( minPriceFlightSet && minPriceFlight != null ) {
                //Fetch the attributes of the flight with the minimum price
                airline = minPriceFlight.getAirline();
                totalFare = minPriceFlight.getFare().getTotalfare().toString();
                departureDateTime = minPriceFlight.getDepdate();
                arrivalDateTime = minPriceFlight.getArrdate();
                noOfStops = minPriceFlight.getStops();
                duration = minPriceFlight.getDuration();
                seatsAvailable = minPriceFlight.getSeatsavailable();
            }

            if( minPriceFlightSet ) {

                flight.currentPrice = Math.round( Float.valueOf( totalFare ) );
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

                String addNewFlightToDatabase = "insert into Flights ( From_Destination, To_Destination, From_Destination_Code, To_Destination_Code, Start_Date, End_Date," +
                        "Stops, Departure_Start_Time, Departure_End_Time, Min_Price_Set, Current_Price, Travel_Class, Date_Created, MinPriceFlightId ) values " +
                        "( '" + flight.fromDestination + "', '" + flight.toDestination + "', '" + flight.fromDestinationCode + "', '" +
                        flight.toDestinationCode + "', '" + flight.startDate.toString() + "', '" +
                        flight.endDate.toString() + "', '" + flight.noOfStops + "', '" + flight.departureStartTime + "', '" +
                        flight.departureEndTime + "', '" + flight.minPriceSet + "', '" + flight.currentPrice + "', '" + flight.travelClass + "', '" + flight.flightSetDate.toString() +
                        "', '" + id + "' );";
                database.execSQL(addNewFlightToDatabase);

            }

            return  true;

        }

        @Override
        public void onPostExecute( Boolean val ){
            progressDialog.dismiss();
            MainActivity.pager.getAdapter().notifyDataSetChanged();
            MainActivity.pager.setCurrentItem( 1, true );
        }
    }

    public int indexOfArray(String input){
        for(int i=0;i<months.length;i++)
        {
            if(months[i].equals(input))
            {
                return i ;
            }
        }
        return -1;     // if the text not found the function return -1
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ){
        super.onActivityCreated(savedInstanceState);
        fromYear = ( TextView ) getActivity().findViewById( R.id.from_year );
        toYear = ( TextView ) getActivity().findViewById( R.id.to_year );
        fromYear.setText( String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) ) ) ;
        toYear.setText( String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) ) );
        dbHelper = new CityAndAirportsTable( getActivity().getApplicationContext() );
        database = dbHelper.getWritableDatabase();

        final RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>( getActivity() );
        rangeSeekBar.setRangeValues(0, 24);
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.seekbar_placeholder);
        layout.addView(rangeSeekBar);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) );

        final RelativeLayout fromDateLayout = ( RelativeLayout ) getActivity().findViewById( R.id.from_date_layout );
        final RelativeLayout toDateLayout = ( RelativeLayout ) getActivity().findViewById( R.id.to_date_layout );

        final Button newFlightButton = ( Button ) getActivity().findViewById( R.id.new_flight_button );

        newFlightButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.clear();
                long id;
                int in = indexOfArray( ( ( TextView ) getActivity().findViewById( R.id.to_month ) ).getText().toString() );
                String fromDestination = ( ( TextView ) getActivity().findViewById( R.id.departure_city ) ).getText().toString();
                String toDestination = ( ( TextView ) getActivity().findViewById( R.id.arrival_city ) ).getText().toString();
                String fromDestinationCode = ( ( TextView ) getActivity().findViewById( R.id.departure_city_code ) ).getText().toString();
                String toDestinationCode = ( ( TextView ) getActivity().findViewById( R.id.arrival_city_code ) ).getText().toString();
                String fromYear = ( ( TextView ) getActivity().findViewById( R.id.from_year ) ).getText().toString() ;
                cal.set( Integer.parseInt( fromYear ),
                        indexOfArray( ( ( TextView ) getActivity().findViewById( R.id.from_month ) ).getText().toString() ),
                        Integer.parseInt( ( ( TextView ) getActivity().findViewById( R.id.from_date ) ).getText().toString() ));
                Date startDate = cal.getTime();

                cal.set( Integer.parseInt( ( ( TextView ) getActivity().findViewById( R.id.to_year ) ).getText().toString() ),
                        indexOfArray( ( ( TextView ) getActivity().findViewById( R.id.to_month ) ).getText().toString() ),
                        Integer.parseInt( ( ( TextView ) getActivity().findViewById( R.id.to_date ) ).getText().toString() ));
                Date endDate = cal.getTime();
                int minPriceSet = Integer.parseInt(((TextView) getActivity().findViewById(R.id.min_fare_amount)).getText().toString());
                int currentPrice;
                String travelClass = ( ( TextView ) getActivity().findViewById( R.id.travel_class ) ).getText().toString();
                Date flightSetDate = ( Date ) Calendar.getInstance().getTime();

                int noOfStops;
                if ( ( ( CheckBox ) getActivity().findViewById( R.id.two_plus_stop ) ).isChecked() ) {
                    noOfStops = 2;
                }
                else if( ( ( CheckBox ) getActivity().findViewById( R.id.one_stop ) ).isChecked() ){
                    noOfStops = 1;
                }
                else {
                    noOfStops = 0;
                }

                int departureStartTime = rangeSeekBar.getSelectedMinValue();
                int departureEndTime = rangeSeekBar.getSelectedMaxValue();


                Flight flight = new Flight( 0, fromDestination, toDestination, fromDestinationCode, toDestinationCode, startDate, endDate, minPriceSet, 0, travelClass,
                        flightSetDate, noOfStops, departureStartTime, departureEndTime );

                progressDialog = new MaterialDialog.Builder( getActivity() )
                        .title( "Fetching minimum price" )
                        .content( "Please wait" )
                        .progress(true, 0)
                        .build();

                progressDialog.show();

                GetLowestPrices lowestPrices = new GetLowestPrices();
                lowestPrices.execute( flight );
            }
        });

        fromDateLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = ( TextView ) getActivity().findViewById( R.id.from_day);
                date = ( TextView ) getActivity().findViewById( R.id.from_date );
                month = ( TextView ) getActivity().findViewById( R.id.from_month );
                year = ( TextView ) getActivity().findViewById( R.id.from_year );
                datePickerDialog.setVibrate( false );
                datePickerDialog.setYearRange(2015, 2028);
                datePickerDialog.setCloseOnSingleTapDay( true );
                datePickerDialog.show( getActivity().getSupportFragmentManager(), datePickerTag );
            }
        });

        toDateLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = ( TextView ) getActivity().findViewById( R.id.to_day);
                date = ( TextView ) getActivity().findViewById( R.id.to_date );
                month = ( TextView ) getActivity().findViewById( R.id.to_month );
                year = ( TextView ) getActivity().findViewById( R.id.to_year );
                datePickerDialog.setVibrate( false );
                datePickerDialog.setYearRange(2015, 2028);
                datePickerDialog.setCloseOnSingleTapDay( true );
                datePickerDialog.show( getActivity().getSupportFragmentManager(), datePickerTag );

            }
        });

        final TableRow traveClassRow = ( TableRow ) getActivity().findViewById( R.id.travel_class_row);
        traveClassRow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog( getActivity() );
                dialog.setCanceledOnTouchOutside( true );
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView( R.layout.travel_class );

                TextView economyClass = ( TextView ) dialog.findViewById( R.id.economy_class );
                TextView businessClass = ( TextView ) dialog.findViewById( R.id.business_class );
                TextView firstClass = ( TextView ) dialog.findViewById( R.id.first_class );
                final TextView travelClass = ( TextView ) getActivity().findViewById( R.id.travel_class );
                economyClass.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        travelClass.setText( "ECONOMY");
                        dialog.dismiss();
                    }
                });
                businessClass.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        travelClass.setText( "BUSINESS");
                        dialog.dismiss();
                    }
                });
                firstClass.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        travelClass.setText( "FIRST CLASS");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        final RelativeLayout departureCityLayout = ( RelativeLayout ) getActivity().findViewById( R.id.depature_city_layout);
        final RelativeLayout arrivalCityLayout = ( RelativeLayout ) getActivity().findViewById( R.id.arrival_city_layout );
        departureCityLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityTextView = ( TextView ) v.findViewById( R.id.departure_city );
                codeTextView = ( TextView ) v.findViewById( R.id.departure_city_code );
                Intent intent = new Intent(getActivity(), CitiesAndAirport.class);
                startActivity(intent);
            }
        });

        arrivalCityLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityTextView = ( TextView ) v.findViewById( R.id.arrival_city );
                codeTextView = ( TextView ) v.findViewById( R.id.arrival_city_code );
                Intent intent = new Intent(getActivity(), CitiesAndAirport.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if( cityTextView != null ){
            cityTextView.setText( city );
        }
        if( codeTextView != null ){
            codeTextView.setText( airportCode );
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int selectedYear, int selectedMonth, int selectedDay ) {
        Calendar calendar = new GregorianCalendar( selectedYear, selectedMonth, selectedDay );
        int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
        date.setText( Integer.toString( selectedDay ) );
        day.setText( daysOfWeek[ dayOfWeek-1 ]);
        month.setText( months[ selectedMonth ]);
        year.setText( Integer.toString( selectedYear ));


    }

}
