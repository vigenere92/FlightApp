package com.flightapp.myapp.coupons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flightapp.myapp.R;

class CouponProviderViewHolder extends RecyclerView.ViewHolder {
    public TextView couponProvider;

    public CouponProviderViewHolder( View view ){
        super( view );
        this.couponProvider = ( TextView )view.findViewById( R.id.couponProvider );
    }
}
public class ProviderAdapter extends RecyclerView.Adapter<CouponProviderViewHolder>{
    private Context mContext;
    private String[] ticketProviders;

    public ProviderAdapter( Context context, String[] ticketProviders ){
        this.mContext = context;
        this.ticketProviders = ticketProviders;
    }

    @Override
    public CouponProviderViewHolder onCreateViewHolder( ViewGroup viewGroup, int i ){
        View v = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.coupon_item_layout, null );
        CouponProviderViewHolder vh = new CouponProviderViewHolder( v );
        return  vh;
    }

    @Override
    public void onBindViewHolder( CouponProviderViewHolder vh, int i){
        String providerName = ticketProviders[i];
        vh.couponProvider.setText( providerName );

    }

    @Override
    public int getItemCount() {
        return ticketProviders.length;
    }
}
