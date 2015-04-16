package com.flightapp.myapp.events;

import java.util.ArrayList;

public class FlightDeleteEvent {
    private ArrayList<String> ids;

    public FlightDeleteEvent( ArrayList<String> ids ){
        this.ids = ids;
    }

    public ArrayList<String> getIds(){
        return ids;
    }
}
