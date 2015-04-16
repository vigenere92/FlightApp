package com.flightapp.myapp.objects;

public class FlightDetailItem {
    public String airline;
    public String fromDestinationCode;
    public String toDestinationCode;
    public String departureTime;
    public String departureDate;
    public String arrivalTime;
    public String arrivalDate;
    public String duration;

    public FlightDetailItem( String airline, String fromDestinationCode, String toDestinationCode, String departureDate,
                             String arrivalDate, String departureTime, String arrivalTime, String duration ){
        this.airline = airline;
        this.fromDestinationCode = fromDestinationCode;
        this.toDestinationCode = toDestinationCode;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
    }
}
