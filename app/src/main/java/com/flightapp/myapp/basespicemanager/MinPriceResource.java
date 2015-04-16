package com.flightapp.myapp.basespicemanager;

import java.util.List;

public class MinPriceResource {
    public Resource resource1;

    public class Resource {
        public int fare;
        public String typeoftravel;
        public String vertical;
        public String roundtrip;
        public String destination;
        public int returndate;
        public String lastupdated;
        public String source;
        public String carrier;
        public long date;
        public String clazz;
        public List<Extra> extra;

        public class Extra {

            public String duration;
            public String arrtime;
            public int nostops;
            public String deptime;
            public String flightno;
        }
    }

}
