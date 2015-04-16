package com.flightapp.myapp.basespicemanager;


import java.util.ArrayList;

public class FlightRequest{

    private FlightData data;

    public FlightRequest(){

    }

    public FlightData getData(){
        return this.data;
    }

    public class FlightData{

        private ArrayList< FlightDetails > onwardflights;

        public FlightData(){

        }

        public ArrayList< FlightDetails > getFlightDetails(){
            return this.onwardflights;
        }
    }

    public class FlightDetails {

        private String origin;

        private Fare fare;

        private String deptime;

        private String arrtime;

        private String duration;

        private String destination;

        private String airline;

        private String depdate;

        private String arrdate;

        private String stops;

        private String seatsavailable;

        public FlightDetails(){

        }

        public String getOrigin(){
            return this.origin;
        }

        public Fare getFare(){
            return this.fare;
        }

        public String getDeptime(){
            return this.deptime;
        }

        public String getArrtime(){
            return this.arrtime;
        }

        public String getDuration(){
            return this.duration;
        }

        public String getDestination(){
            return this.destination;
        }

        public String getAirline(){
            return this.airline;
        }

        public String getDepdate(){
            return this.depdate;
        }

        public String getArrdate(){
            return this.arrdate;
        }

        public String getStops(){
            return this.stops;
        }

        public String getSeatsavailable(){
            return this.seatsavailable;
        }
    }

    public class Fare{

        String totalfare;

        public Fare(){

        }

        public String getTotalfare(){
            return this.totalfare;
        }
    }

}

