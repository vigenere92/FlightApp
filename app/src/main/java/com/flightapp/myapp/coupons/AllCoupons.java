package com.flightapp.myapp.coupons;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flightapp.myapp.R;
import com.flightapp.myapp.objects.Coupons;
import com.flightapp.myapp.objects.Flight;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

class CouponCard extends Card {
    private String couponDescription;

    void setCouponDescription( String description ){
        this.couponDescription = description;
    }

    String getCouponDescription(){
        return this.couponDescription;
    }
    public CouponCard( Context context){ super( context, R.layout.coupon_card_layout );}

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView couponDescription = ( TextView ) view.findViewById( R.id.coupon_description );
        if( couponDescription != null ){
            couponDescription.setText( this.couponDescription );
        }
    }
}

public class AllCoupons extends ActionBarActivity {

    private String[] ticketProviders = { "Travelguru", "Goibibo", "Cleartrip", "Easygotrip.com",
            "Makemytrip", "Musafir", "Travelxp", "Yatra" };

    private String dataweaveApiKey = "77172393ca7de1ef8020939b4db784014042dcbf";
    private RecyclerView.Adapter couponProviders;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CardListView couponsCardsView;
    private CardArrayAdapter couponsCardArrayAdapter;
    private ArrayList<Card> couponCardsList;
    private Map<String, String> airlineToDataWeaveName;
    private Map<String, Coupons> providerToCoupons;


    class GetCoupons extends AsyncTask<Void, Void, Void>{

        @Override
        public Void doInBackground( Void... params){
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
            for( String provider : ticketProviders ){
                String couponQuery = "http://api.dataweave.in/v1/coupons/listByStore/?api_key=" + dataweaveApiKey + "&store=" +
                        provider + "&page=1&per_page=100";
                try {
                    HttpResponse response = httpClient.execute( new HttpGet( couponQuery ) );
                    httpEntity = response.getEntity();
                }
                catch ( IOException e ){

                }
                String couponQueryResult = "";
                try {
                    couponQueryResult = EntityUtils.toString(httpEntity);
                }
                catch (Exception e){

                }
                Gson gson = new Gson();
                Coupons coupon = gson.fromJson( couponQueryResult, Coupons.class );
                providerToCoupons.put( provider, coupon );
            }

            return null;

        }

        @Override
        public void onPostExecute( Void result ){


        }
    }

    public void populateCouponCards(){
        couponCardsList = new ArrayList<Card>();
        for( int i=0; i <10; i++ ){
            CouponCard card = new CouponCard( this );
            card.setCouponDescription( "This is a coupon" );
            couponCardsList.add( card );

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_coupons);
        airlineToDataWeaveName = new HashMap< String, String>();
        providerToCoupons = new HashMap< String, Coupons >();
        airlineToDataWeaveName.put( "jetairways", "Jet Airways");
        airlineToDataWeaveName.put( "airindia", "Air India");
        airlineToDataWeaveName.put( "airasia", "AirAsia");
        airlineToDataWeaveName.put( "britishairways", "Britishairways");
        airlineToDataWeaveName.put( "goair", "Goair" );
        airlineToDataWeaveName.put( "indigo", "Indigo" );
        airlineToDataWeaveName.put( "qatarairways", "Qatar Airways");
        airlineToDataWeaveName.put( "spicejet", "Spicejet");
        airlineToDataWeaveName.put( "etihadairways","Etihad Airways");
        couponProviders = new ProviderAdapter( getApplicationContext(), ticketProviders );
        mRecyclerView = ( RecyclerView )findViewById( R.id.coupon_providers );
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false );
        mRecyclerView.setLayoutManager( mLayoutManager );
        mRecyclerView.setAdapter( couponProviders );
        couponsCardsView = (CardListView) findViewById(R.id.coupons_list);
        populateCouponCards();
        couponsCardArrayAdapter = new CardArrayAdapter( this, couponCardsList );
        if( couponsCardsView != null ){
            couponsCardsView.setAdapter( couponsCardArrayAdapter );
        }
        new GetCoupons().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_coupons, menu);
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
