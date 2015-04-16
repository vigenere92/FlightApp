package com.flightapp.myapp.citiesAndAirports;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flightapp.myapp.NewFlightTab;
import com.flightapp.myapp.R;

import java.util.ArrayList;

public class CitiesAndAirportAdapter extends RecyclerView.Adapter<CitiesAndAirportAdapter.ViewHolder>{

    private ArrayList<CityAndAirport> citiesAndAirports;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView cityAndAirportTextView;
        public TextView airportCodeTextView;
        Context context;

        public ViewHolder(View v, Context cxt ) {
            super(v);
            cityAndAirportTextView = (TextView) v.findViewById(R.id.cityAndAirport);
            airportCodeTextView = (TextView) v.findViewById(R.id.airportCode);
            context = cxt;
            v.setClickable( true );
            v.setOnClickListener( this );

        }

        @Override
        public void onClick(View v) {
            TextView cityAndAirportView = ( TextView ) v.findViewById( R.id.cityAndAirport);
            TextView airportCodeView = ( TextView ) v.findViewById( R.id.airportCode );
            NewFlightTab.city = cityAndAirportView.getText().toString().split("-")[0];
            NewFlightTab.airportCode = airportCodeView.getText().toString();
            ((Activity)context).finish();

        }
    }

    public CitiesAndAirportAdapter( ArrayList<CityAndAirport> cityAndAirportList, Context cxt ){
        citiesAndAirports = cityAndAirportList;
        context = cxt;
    }

    @Override
    public CitiesAndAirportAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_airport_item, parent, false);
        ViewHolder vh = new ViewHolder(v, context);
        return vh;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int position ){
        CityAndAirport cityAndAirport = citiesAndAirports.get( position );
        String cityAndAirportStr = cityAndAirport.city + "-" + cityAndAirport.airportName;
        String airportCodeStr = cityAndAirport.airportCode;
        viewHolder.cityAndAirportTextView.setText( cityAndAirportStr) ;
        viewHolder.airportCodeTextView.setText( airportCodeStr );
    }

    @Override
    public int getItemCount(){
        return citiesAndAirports.size();
    }
}
