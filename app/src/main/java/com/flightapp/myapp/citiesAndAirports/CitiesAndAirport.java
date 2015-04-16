package com.flightapp.myapp.citiesAndAirports;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.flightapp.myapp.R;
import com.flightapp.myapp.database.CityAndAirportsTable;

import java.util.ArrayList;
import java.util.regex.Pattern;

class CityAndAirport {
    public String city;
    public String airportName;
    public String airportCode;
}

public class CitiesAndAirport extends ActionBarActivity {
    private CityAndAirportsTable dbHelper;
    private SQLiteDatabase database;
    private String[] allColumns = { CityAndAirportsTable.city, CityAndAirportsTable.airportName, CityAndAirportsTable.airportCode };
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private EditText cityAndAiportSearch;
    private ArrayList<CityAndAirport> citiesAndAirports = new ArrayList<CityAndAirport>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities_and_airport);
        toolbar = (Toolbar) findViewById(R.id.cities_airport_appbar);
        setSupportActionBar(toolbar);

        dbHelper = new CityAndAirportsTable( getApplicationContext() );
        database = dbHelper.getReadableDatabase();

        cityAndAiportSearch = (EditText) findViewById( R.id.city_airport_searchbox);

        mRecyclerView = (RecyclerView) findViewById(R.id.citiesAndAirport);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager( mLayoutManager );

        populateCitiesAndAirport();
        mAdapter = new CitiesAndAirportAdapter(citiesAndAirports, this);
        mRecyclerView.setAdapter( mAdapter );

        cityAndAiportSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterWithSearch( s.toString() );

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void filterWithSearch( String pattern ){
        ArrayList<CityAndAirport> matchedCities = new ArrayList<CityAndAirport>();
        for( CityAndAirport cityAndAirport : citiesAndAirports ){
            if( cityAndAirport.city.toLowerCase().contains( pattern.toLowerCase() ) ||
                    cityAndAirport.airportCode.toLowerCase().contains(( pattern.toLowerCase() ))){
                matchedCities.add(cityAndAirport);
            }
        }
        RecyclerView.Adapter newAdapter = new CitiesAndAirportAdapter( matchedCities, this );
        mRecyclerView.swapAdapter( newAdapter, false);
    }

    public void populateCitiesAndAirport(){
        Cursor cursor = database.query( CityAndAirportsTable.cityAndAirportsTable, allColumns, null, null, null, null, null );
        cursor.moveToFirst();
        while( !cursor.isAfterLast() ){
            CityAndAirport cityAndAirport = new CityAndAirport();
            cityAndAirport.city = cursor.getString(0);
            cityAndAirport.airportName = cursor.getString(1);
            cityAndAirport.airportCode = cursor.getString(2);
            citiesAndAirports.add( cityAndAirport );
            cursor.moveToNext();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cities_and_airport, menu);
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
