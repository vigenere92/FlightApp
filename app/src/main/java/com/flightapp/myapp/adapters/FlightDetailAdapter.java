package com.flightapp.myapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flightapp.myapp.R;
import com.flightapp.myapp.objects.FlightDetailItem;

import java.util.ArrayList;

public class FlightDetailAdapter extends ArrayAdapter<FlightDetailItem>{
    Context context;
    int layoutResourceId;
    ArrayList<FlightDetailItem> flightRows = null;

    public FlightDetailAdapter( Context context, int layoutResourceId, ArrayList<FlightDetailItem> flightRows ){
        super( context, layoutResourceId, flightRows );
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.flightRows = flightRows;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        View row = convertView;
        FlightDetailItemHolder flightDetailItemHolder = null;

        if( row == null ){
            LayoutInflater layoutInflater = ( ( Activity ) context ).getLayoutInflater();
            row = layoutInflater.inflate( layoutResourceId, parent, false );
            flightDetailItemHolder = new FlightDetailItemHolder();
            flightDetailItemHolder.airline = ( TextView )row.findViewById( R.id.flightrow_airline );
            flightDetailItemHolder.fromDestinationCode = ( TextView )row.findViewById( R.id.flightrow_departurecitycode );
            flightDetailItemHolder.toDestinationCode = ( TextView )row.findViewById( R.id.flightrow_arrivalcitycode );
            flightDetailItemHolder.departureDate = ( TextView )row.findViewById( R.id.flightrow_departuredate );
            flightDetailItemHolder.departureTime = ( TextView )row.findViewById( R.id.flightrow_departuretime );
            flightDetailItemHolder.arrivalDate = ( TextView )row.findViewById( R.id.flightrow_arrivaldate );
            flightDetailItemHolder.arrivalTime = ( TextView )row.findViewById( R.id.flightrow_arrivaltime );
            flightDetailItemHolder.duration = ( TextView )row.findViewById( R.id.flightrow_duration );

            row.setTag( flightDetailItemHolder );
        }
        else{
            flightDetailItemHolder = ( FlightDetailItemHolder )row.getTag();
        }

        FlightDetailItem flightDetailItem = flightRows.get( position );
        flightDetailItemHolder.airline.setText( flightDetailItem.airline );
        flightDetailItemHolder.fromDestinationCode.setText( flightDetailItem.fromDestinationCode );
        flightDetailItemHolder.toDestinationCode.setText( flightDetailItem.toDestinationCode );
        flightDetailItemHolder.departureDate.setText( flightDetailItem.departureDate );
        flightDetailItemHolder.departureTime.setText( flightDetailItem.departureTime );
        flightDetailItemHolder.arrivalDate.setText( flightDetailItem.arrivalDate );
        flightDetailItemHolder.arrivalTime.setText( flightDetailItem.arrivalTime );
        flightDetailItemHolder.duration.setText( flightDetailItem.duration );

        return row;
    }

    static class FlightDetailItemHolder{
        TextView airline;
        TextView fromDestinationCode;
        TextView toDestinationCode;
        TextView departureDate;
        TextView departureTime;
        TextView arrivalDate;
        TextView arrivalTime;
        TextView duration;
    }
}
