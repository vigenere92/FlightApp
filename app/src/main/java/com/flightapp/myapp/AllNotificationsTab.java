package com.flightapp.myapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flightapp.myapp.database.CityAndAirportsTable;
import com.flightapp.myapp.events.FlightDeleteEvent;

import java.util.ArrayList;
import java.util.Arrays;

import de.greenrobot.event.EventBus;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.UndoBarController;

public class AllNotificationsTab extends Fragment {
    private CardArrayAdapter mCardArrayAdapter;
    private UndoBarController mUndoBarController;
    private CardListView mListView;
    private CityAndAirportsTable dbHelper;
    private SQLiteDatabase database;
    private ArrayList<Card> notificationCards = new ArrayList<Card>();

    public void populateCards(){
        Cursor cursor = database.query( CityAndAirportsTable.notificationsTable, new String[] { CityAndAirportsTable.notificationId,
                        CityAndAirportsTable.notifyFlightId }, null, null, null, null, "Id DESC" );
        cursor.moveToFirst();
        while( !cursor.isAfterLast() ){
            String notificationId = String.valueOf( cursor.getInt( 0 ) );
            String flightId = String.valueOf( cursor.getInt( 1 ) );
            Cursor flightCursor = database.query( CityAndAirportsTable.flightTable, new String[] { CityAndAirportsTable.fromDestinationCode,
            CityAndAirportsTable.toDestinationCode, CityAndAirportsTable.currentPrice }, CityAndAirportsTable.flightId + "=" + flightId,
                    null, null, null, null );
            if( flightCursor.moveToFirst()  ){

                Card card = new Card( this.getActivity() );
                card.setTitle( "Price for flight " + flightCursor.getString( 0 ) + " to " + flightCursor.getString( 1 ) + " is " +
                flightCursor.getInt( 2 ) );
                card.setSwipeable( true );
                card.setId( notificationId );

                card.setOnSwipeListener( new Card.OnSwipeListener() {
                    @Override
                    public void onSwipe(Card card) {
                        String id = card.getId();
                        database.delete( CityAndAirportsTable.notificationsTable, CityAndAirportsTable.notificationId + "=" + id, null );
                    }
                });

                notificationCards.add(card);
            }
            flightCursor.close();
            cursor.moveToNext();
        }
    }

    //This method will be called when a flight is deleted.
    public void onEvent( FlightDeleteEvent event ){
        ArrayList<Card> dummyNotificationCards = new ArrayList<Card>();
        ArrayList<String> ids = event.getIds();
        dummyNotificationCards = notificationCards;
        for( Card card : dummyNotificationCards ){
            if( ids.contains( card.getId() ) ){
                notificationCards.remove( card );
            }
        }
        mCardArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.all_notifications,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Register this fragment for events
        EventBus.getDefault().register( this );

        dbHelper = new CityAndAirportsTable( getActivity().getApplicationContext() );
        database = dbHelper.getReadableDatabase();

        mListView = (CardListView) getActivity().findViewById(R.id.notification_cardlist);
        populateCards();
        mCardArrayAdapter = new CardArrayAdapter( getActivity(), notificationCards );

        if( mListView != null ){
            mListView.setAdapter( mCardArrayAdapter );
        }
    }

}
