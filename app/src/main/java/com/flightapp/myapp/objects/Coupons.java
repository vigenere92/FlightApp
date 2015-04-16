package com.flightapp.myapp.objects;

import java.util.ArrayList;

public class Coupons {
    private ArrayList<Coupon> data;

    public Coupons(){

    }

    public ArrayList<Coupon> getData(){
        return data;
    }

    public class Coupon {
        String couponTitle;
        String couponCode;
        String couponDescription;
        String expiryDate;
        String lastTested;

        public Coupon() {

        }

        public String getCouponTitle(){
            return couponTitle;
        }

        public String getCouponCode(){
            return couponCode;
        }

        public String getCouponDescription(){
            return couponDescription;
        }

        public String getExpiryDate(){
            return expiryDate;
        }

        public String getLastTested(){
            return lastTested;
        }
    }


}
