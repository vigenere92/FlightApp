package com.flightapp.myapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flight implements Parcelable{
    public long id;
    public String fromDestination;
    public String toDestination;
    public String fromDestinationCode;
    public String toDestinationCode;
    public Date startDate;
    public Date endDate;
    public int minPriceSet;
    public int currentPrice;
    public String travelClass;
    public Date flightSetDate;
    public int noOfStops;
    public int departureStartTime;
    public int departureEndTime;

    public Flight( long id, String fromDestination, String toDestination, String fromDestinationCode, String toDestinationCode, Date startDate, Date endDate, int minPriceSet,
                   int currentPrice, String travelClass, Date flightSetDate, int noOfStops, int departureStartTime, int departureEndTime){

        this.id = id;
        this.fromDestination = fromDestination;
        this.toDestination = toDestination;
        this.fromDestinationCode = fromDestinationCode;
        this.toDestinationCode = toDestinationCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minPriceSet = minPriceSet;
        this.travelClass = travelClass;
        this.noOfStops = noOfStops;
        this.departureStartTime = departureStartTime;
        this.departureEndTime = departureEndTime;
        this.flightSetDate = flightSetDate;
        this.currentPrice = currentPrice;

    }

    @Override
    public void writeToParcel( Parcel flightParcel, int flags){
        flightParcel.writeLong( id );
        flightParcel.writeString( fromDestination );
        flightParcel.writeString( toDestination );
        flightParcel.writeString( startDate.toString() );
        flightParcel.writeString( endDate.toString() );
        flightParcel.writeInt( minPriceSet );
        flightParcel.writeInt( noOfStops );
        flightParcel.writeInt( departureStartTime );
        flightParcel.writeInt( departureEndTime );
    }

    private Flight( Parcel flightParcel ){
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        this.id = flightParcel.readInt();
        this.fromDestination = flightParcel.readString();
        this.toDestination = flightParcel.readString();
        try {
            this.startDate = format.parse( flightParcel.readString() );
            this.endDate = format.parse( flightParcel.readString() );
        }
        catch ( java.text.ParseException e ){
            e.printStackTrace();
        }
        this.minPriceSet = flightParcel.readInt();
        this.noOfStops = flightParcel.readInt();
        this.departureStartTime = flightParcel.readInt();
        this.departureEndTime = flightParcel.readInt();
    }

    public static final Parcelable.Creator<Flight> CREATOR = new Parcelable.Creator<Flight>() {
        @Override
        public Flight createFromParcel(Parcel flightParcel ) {
            return new Flight( flightParcel );
        }

        @Override
        public Flight[] newArray(int size) {
            return new Flight[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }


}
