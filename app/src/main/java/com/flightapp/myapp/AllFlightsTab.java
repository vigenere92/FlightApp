package com.flightapp.myapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flightapp.myapp.adapters.FlightDetailAdapter;
import com.flightapp.myapp.citiesAndAirports.CitiesAndAirport;
import com.flightapp.myapp.coupons.AllCoupons;
import com.flightapp.myapp.database.CityAndAirportsTable;
import com.flightapp.myapp.events.FlightDeleteEvent;
import com.flightapp.myapp.objects.Flight;
import com.flightapp.myapp.objects.FlightDetailItem;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import it.gmariotti.cardslib.library.cards.topcolored.TopColoredCard;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.UndoBarController;

class FlightCard extends Card {
    private String fromDate, toDate;
    private int minPrice, currentPrice;

    public FlightCard(Context context) {
        super(context, R.layout.allflight_secondhalf);
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView fromDateFlight = (TextView) view.findViewById(R.id.from_date_flight);
        if (fromDateFlight != null) {
            fromDateFlight.setText(fromDate);
        }

        TextView toDateFlight = (TextView) view.findViewById(R.id.to_date_flight);
        if (toDateFlight != null) {
            toDateFlight.setText(toDate);
        }

        TextView minimumPrice = (TextView) view.findViewById(R.id.minimum_price);
        if (minimumPrice != null) {
            minimumPrice.setText( String.valueOf( minPrice ));
        }

        TextView currentLowestPrice = (TextView) view.findViewById(R.id.current_price);
        if (currentLowestPrice != null) {
            currentLowestPrice.setText( String.valueOf( currentPrice ) );
        }

    }
}

public class AllFlightsTab extends Fragment {
    private ArrayList<Card> cards = new ArrayList<Card>();
    private CardArrayAdapter mCardArrayAdapter;
    private CardListView mListView;
    private UndoBarController mUndoBarController;
    private CityAndAirportsTable dbHelper;
    private SQLiteDatabase database;
    private ArrayList< Flight > allFlights;
    private String[] allColumns = { CityAndAirportsTable.flightId, CityAndAirportsTable.fromDestination, CityAndAirportsTable.toDestination,
            CityAndAirportsTable.fromDestinationCode, CityAndAirportsTable.toDestinationCode,
            CityAndAirportsTable.startDate, CityAndAirportsTable.endDate, CityAndAirportsTable.noOfStops, CityAndAirportsTable.depatureStartTime,
            CityAndAirportsTable.departureEndTime, CityAndAirportsTable.minPriceSet, CityAndAirportsTable.currentPrice, CityAndAirportsTable.travelClass, CityAndAirportsTable.dateCreated };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_flights, container, false);
        return v;
    }

    public void populateAllFlights(){
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Cursor cursor = database.query( CityAndAirportsTable.flightTable, allColumns, null, null, null, null, "Id DESC" );
        if ( cursor.moveToFirst() ) {
            do {
                Date startDate = new Date();
                Date endDate = new Date();
                Date flightSetDate = new Date();
                int id = cursor.getInt(0);
                String fromDestiantion = cursor.getString(1);
                String toDestination = cursor.getString(2);
                String fromDestinationCode = cursor.getString(3);
                String toDestinationCode = cursor.getString(4);
                try {
                    startDate = format.parse(cursor.getString(5));
                    endDate = format.parse(cursor.getString(6));
                    flightSetDate = format.parse(cursor.getString(13));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                int stops = cursor.getInt(7);
                int departureStartTime = cursor.getInt(8);
                int departureEndTime = cursor.getInt(9);
                int minPriceSet = cursor.getInt(10);
                int currentPrice = cursor.getInt(11);
                String travelClass = cursor.getString(12);
                Flight flight = new Flight(id, fromDestiantion, toDestination, fromDestinationCode, toDestinationCode, startDate, endDate, minPriceSet, currentPrice, travelClass,
                        flightSetDate, stops, departureStartTime, departureEndTime);
                allFlights.add(flight);
            }
            while (cursor.moveToNext());
        }
    }

    public void populateCards( final Flight flight, boolean newFlight ){
        final DateFormat df = new SimpleDateFormat("dd MMM yy");
        FlightCard card = new FlightCard( getActivity().getApplicationContext() );
        card.setFromDate( df.format( flight.startDate ) );
        card.setToDate( df.format( flight.endDate ) );
        card.setMinPrice( flight.minPriceSet );
        card.setCurrentPrice( flight.currentPrice );
        CardHeader header = new CardHeader( getActivity().getApplicationContext() );
        header.setTitle(flight.fromDestination + " - " + flight.toDestination);
        card.addCardHeader( header );
        card.setId( String.valueOf( flight.id ) );

        card.setSwipeable( true );

        card.setOnClickListener( new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd't'kkmm");
                String id = card.getId();
                Cursor cursor = database.query( CityAndAirportsTable.flightTable, new String[] { CityAndAirportsTable.minPFlightId,
                        CityAndAirportsTable.fromDestinationCode, CityAndAirportsTable.toDestinationCode, CityAndAirportsTable.fromDestination,
                        CityAndAirportsTable.toDestination},
                        CityAndAirportsTable.flightId + "=" + id, null, null, null, null );
                if( cursor.moveToFirst() ){
                    String minPriceFlightId = String.valueOf( cursor.getInt( 0 ) );
                    Cursor minPriceCursor = database.query( CityAndAirportsTable.minPriceFlightsTable, null,
                            CityAndAirportsTable.minPriceFlightId + "=" + minPriceFlightId, null, null, null, null );
                    if( minPriceCursor.moveToFirst() ){

                        String fromDestinationCode = cursor.getString( 1 );
                        String toDestinationCode = cursor.getString( 2 );
                        String fromDestination = cursor.getString( 3 );
                        String toDestination = cursor.getString( 4 );
                        Date dateOfDeparture = new Date();
                        Date dateOfArrival = new Date();
                        try {
                            String a = minPriceCursor.getString( 3 );
                            dateOfDeparture =  format.parse( minPriceCursor.getString( 3 ) );
                            dateOfArrival = format.parse( minPriceCursor.getString( 4 ) );
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        DateFormat timeFormat = new SimpleDateFormat( "kk:mm");
                        DateFormat dateFormat = new SimpleDateFormat( "dd MMM,yyyy");
                        String departureDate = dateFormat.format( dateOfDeparture );
                        String departureTime = timeFormat.format( dateOfDeparture );
                        String arrivalDate = dateFormat.format( dateOfArrival );
                        String arrivalTime = timeFormat.format( dateOfArrival );
                        String stops = minPriceCursor.getString( 5 );
                        String duration = minPriceCursor.getString(6);
                        String noOfSeatsLeft = minPriceCursor.getString(7);
                        String airline = minPriceCursor.getString(1);

                        MaterialDialog flightDetail = new MaterialDialog.Builder( getActivity() )
                                .title( fromDestination + " - " + toDestination )
                                .customView( R.layout.flightdetail_rowitem, true )
                                .backgroundColor( R.color.AppBarColor )
                                .backgroundColorRes( R.color.ColorPrimaryDark )
                                .titleColorRes( R.color.white )
                                .titleGravity(GravityEnum.CENTER )
                                .build();
                        View customView = flightDetail.getCustomView();
                        ( ( TextView )customView.findViewById( R.id.flightrow_airline ) ).setText( airline );
                        ( ( TextView )customView.findViewById( R.id.flightrow_departurecitycode ) ).setText( fromDestinationCode );
                        ( ( TextView )customView.findViewById( R.id.flightrow_arrivalcitycode ) ).setText( toDestinationCode );
                        ( ( TextView )customView.findViewById( R.id.flightrow_departuredate ) ).setText( departureDate );
                        ( ( TextView )customView.findViewById( R.id.flightrow_departuretime ) ).setText( departureTime );
                        ( ( TextView )customView.findViewById( R.id.flightrow_arrivaldate ) ).setText( arrivalDate );
                        ( ( TextView )customView.findViewById( R.id.flightrow_arrivaltime ) ).setText( arrivalTime );
                        ( ( TextView )customView.findViewById( R.id.flightrow_duration ) ).setText( duration );
                        ( ( TextView )customView.findViewById( R.id.flightrow_stops ) ).setText( stops + " Stops" );
                        if( !noOfSeatsLeft.matches( "" ) ){
                            ( ( TextView )customView.findViewById( R.id.flightrow_seatsleft ) ).setText( noOfSeatsLeft + " Seats Left");
                        }
                        ( ( Button )customView.findViewById( R.id.coupon_button) ).setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), AllCoupons.class );
                                startActivity(intent);
                            }
                        });



                        flightDetail.show();

                    }

                }
            }
        });
        card.setOnSwipeListener( new Card.OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {

            }
        });

        card.setOnUndoSwipeListListener( new Card.OnUndoSwipeListListener() {
            @Override
            public void onUndoSwipe(Card card) {
                mCardArrayAdapter.notifyDataSetChanged();

            }
        });

        card.setOnUndoHideSwipeListListener( new Card.OnUndoHideSwipeListListener() {
            @Override
            public void onUndoHideSwipe(Card card) {
                String id = card.getId();
                database.delete( CityAndAirportsTable.flightTable, CityAndAirportsTable.flightId + "=" + id, null );

                //Get ids of all notifications to be deleted
                Cursor cursor = database.query( CityAndAirportsTable.notificationsTable, null, CityAndAirportsTable.notifyFlightId + "=" + id,
                        null, null, null, null );
                ArrayList<String> ids = new ArrayList<String>();
                if( cursor.moveToFirst() ){
                    do{
                        ids.add( String.valueOf( cursor.getInt( 0 ) ) );
                    }while ( cursor.moveToNext() );
                }
                //Delete notifications for this flight.
                database.delete( CityAndAirportsTable.notificationsTable, CityAndAirportsTable.notifyFlightId + "=" + id, null );

                //Update the all notifications view
                EventBus.getDefault().post( new FlightDeleteEvent( ids ) );

            }
        });

        cards.add(card);

        if( newFlight ){
            mCardArrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper = new CityAndAirportsTable( getActivity().getApplicationContext() );
        database = dbHelper.getReadableDatabase();
        allFlights = new ArrayList< Flight >();
        populateAllFlights();

        for( final Flight flight : allFlights ){
            populateCards( flight, false );
        }

        mListView = ( CardListView ) getActivity().findViewById( R.id.allflights_cardlist );
        mCardArrayAdapter = new CardArrayAdapter( getActivity(), cards);
        mCardArrayAdapter.setUndoBarUIElements( new UndoBarController.DefaultUndoBarUIElements() {

            @Override
            public SwipeDirectionEnabled isEnabledUndoBarSwipeAction() {
                return SwipeDirectionEnabled.TOPBOTTOM;
            }

            @Override
            public AnimationType getAnimationType() {
                return AnimationType.TOPBOTTOM;
            }

        });
        mCardArrayAdapter.setEnableUndo( true );


        //Set the empty view
        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }





}
