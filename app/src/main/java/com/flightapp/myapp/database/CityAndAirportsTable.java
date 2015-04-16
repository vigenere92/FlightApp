package com.flightapp.myapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class CityAndAirportsTable extends SQLiteOpenHelper{

    public final static String notificationsTable = "Notifications";
    public final static String notificationId = "Id";
    public final static String notifyFlightId = "FlightId";

    public final static String minPriceFlightsTable = "MinPriceFlights";
    public final static String minPriceFlightId = "Id";
    public final static String airline = "Airline";
    public final static String totalFare = "TotalFare";
    public final static String departureDateTime = "DepartureDateTime";
    public final static String arrivalDateTime = "ArrivalDateTime";
    public final static String duration = "Duration";
    public final static String seatsAvailable = "SeatsAvailable";

    public final static String cityAndAirportsTable = "City_Airports";
    public final static String city = "City";
    public final static String airportCode  = "Code";
    public final static String airportName = "Airport_Name";

    public final static String flightTable = "Flights";
    public final static String flightId = "Id";
    public final static String fromDestination = "From_Destination";
    public final static String toDestination = "To_Destination";
    public final static String fromDestinationCode = "From_Destination_Code";
    public final static String toDestinationCode = "To_Destination_Code";
    public final static String startDate = "Start_Date";
    public final static String endDate = "End_Date";
    public final static String noOfStops = "Stops";
    public final static String depatureStartTime = "Departure_Start_Time";
    public final static String departureEndTime = "Departure_End_Time";
    public final static String minPriceSet = "Min_Price_Set";
    public final static String currentPrice = "Current_Price";
    public final static String travelClass = "Travel_Class";
    public final static String dateCreated = "Date_Created";
    public final static String minPFlightId = "MinPriceFlightId";

    private final static String databaseName = "Application_DB";
    private final static int databaseVersion = 1;

    public static final String flightTableCreate = "create table " +
            flightTable + "(" + flightId + " integer primary key autoincrement, " +
            fromDestination + " text not null, " +
            toDestination + " text not null, " +
            fromDestinationCode + " text not null, " +
            toDestinationCode + " text not null, " +
            startDate + " text not null, " +
            endDate + " text not null, " +
            minPriceSet + " integer not null, " +
            currentPrice + " integer, " +
            travelClass + " text not null, " +
            noOfStops + " integer default 0, " +
            depatureStartTime + " integer, " +
            departureEndTime + " integer, " +
            dateCreated + " text not null, " +
            minPFlightId + " int not null );";

    private final ArrayList<ArrayList<String>> listOfCityAndAirports  = new ArrayList<ArrayList<String>>();

    public static final String cityAndAirportsTableCreate = "create table " +
            cityAndAirportsTable + "( " + airportCode + " text primary key, " +
            airportName + " text not null, " +
            city + " text not null );";

    public static final String minPriceFlightTableCreate = "create table " +
            minPriceFlightsTable + "(" + minPriceFlightId + " integer primary key autoincrement, " +
            airline + " text not null, " +
            totalFare + " integer not null default 0, " +
            departureDateTime + " text not null, " +
            arrivalDateTime + " text not null, " +
            noOfStops + " integer not null, " +
            duration + " text not null, " +
            seatsAvailable + " integer not null );";

    public static final String notificationTableCreate = "create table " +
            notificationsTable + "(" + notificationId + " integer primary key autoincrement, " +
            notifyFlightId + " integer not null );";

    public CityAndAirportsTable( Context context){
        super( context, databaseName, null, databaseVersion );
        initAirportsAndCities();
    }

    public void populateCityAndAirportsTable( SQLiteDatabase database ){
        for( ArrayList<String> airportAndCity : listOfCityAndAirports ){
            String addEntry = "insert into " + cityAndAirportsTable +
                   " (" + city + ", " + airportName + ", " + airportCode + " ) values ('" +
                    airportAndCity.get(0) + "', '" + airportAndCity.get(1) + "', '" + airportAndCity.get(2) + "' );";
            database.execSQL(addEntry);
        }
    }
    @Override
    public void onCreate( SQLiteDatabase database){

        database.execSQL( flightTableCreate );
        database.execSQL( cityAndAirportsTableCreate );
        database.execSQL( notificationTableCreate );
        database.execSQL( minPriceFlightTableCreate );
        populateCityAndAirportsTable(database);
    }

    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ){
        Log.w(CityAndAirportsTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + cityAndAirportsTable );
        onCreate(database);
    }

    public void initAirportsAndCities(){

        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Agra",  "Kheria", "AGR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Agatti Island",  "Agatti Island", "AGX")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Aizawl",  "Aizawl", "AJL")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Akola",  "Akola", "AKD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Ahmedabad",  "Ahmedabad", "AMD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Amritsar",  "Raja Sansi", "ATQ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bhubaneswar",  "Bhubaneswar", "BBI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Vadodara",  "Vadodara", "BDQ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bareli",  "Bareli", "BEK")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bellary",  "Bellary", "BEP")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bhuj",  "Rudra Mata", "BHJ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bhopal",  "Bhopal", "BHO")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bhavnagar",  "Bhavnagar", "BHU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bikaner",  "Bikaner", "BKB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bangalore",  "Bangalore International Airport", "BLR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Mumbai",  "Chhatrapati Shivaji International", "BOM")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bhatinda",  "Bhatinda", "BUP")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Car Nicobar",  "Car Nicobar", "CBD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Calicut",  "Kozhikode Airport", "CCJ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kolkata",  "Netaji Subhas Chandra", "CCU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Cuddapah",  "Cuddapah", "CDP")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Coimbatore",  "Peelamedu", "CJB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Cooch Behar",  "Cooch Behar", "COH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kochi",  "Cochin International", "COK")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Daparizo",  "Daparizo", "DAE")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Darjeeling",  "Darjeeling", "DAI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Dhanbad",  "Dhanbad", "DBD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Dehra Dun",  "Dehra Dun", "DED")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("New Delhi",  "Indira Gandhi Intl", "DEL")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Deparizo",  "Deparizo", "DEP")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Dharamshala",  "Gaggal Airport", "DHM")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Dibrugarh",  "Dibrugarh", "DIB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Diu",  "Diu", "DIU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Dimapur",  "Dimapur", "DMU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Guwahati",  "Borjhar", "GAU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Gaya",  "Gaya", "GAY")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Goa",  "Dabolim", "GOI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Gorakhpur",  "Gorakhpur", "GOP")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Guna",  "Guna", "GUX")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Gwalior",  "Gwalior", "GWL")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Hubli",  "Hubli", "HBX")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Khajuraho",  "Khajuraho", "HJR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Hissar",  "Hissar", "HSS")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Hyderabad",  "Hyderabad Airport", "HYD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Indore",  "Devi Ahilyabai Holkar", "IDR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Imphal",  "Municipal", "IMF")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Nasik",  "Gandhinagar Arpt", "ISK")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Agartala",  "Singerbhil", "IXA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Siliguri",  "Bagdogra", "IXB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Chandigarh",  "Chandigarh", "IXC")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Allahabad",  "Bamrauli", "IXD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Mangalore",  "Bajpe", "IXE")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Belgaum",  "Sambre", "IXG")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kailashahar",  "Kailashahar", "IXH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Lilabari",  "Lilabari", "IXI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jammu",  "Satwari", "IXJ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Keshod",  "Keshod", "IXK")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Leh",  "Bakula Rimpoche", "IXL")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Madurai",  "Madurai", "IXM")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Khowai",  "Khowai", "IXN")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Pathankot",  "Pathankot", "IXP")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kamalpur",  "Kamalpur", "IXQ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Ranchi",  "Birsa Munda International", "IXR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Silchar",  "Kumbhirgram", "IXS")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Pasighat",  "Pasighat", "IXT")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Aurangabad",  "Chikkalthana", "IXU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Along",  "Along", "IXV")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jamshedpur",  "Sonari", "IXW")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kandla",  "Kandla", "IXY")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Port Blair",  "Port Blair", "IXZ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jaipur",  "Sanganeer", "JAI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jodhpur",  "Jodhpur", "JDH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jamnagar",  "Govardhanpur", "JGA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jagdalpur",  "Jagdalpur", "JGB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jabalpur",  "Jabalpur", "JLR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jorhat",  "Rowriah", "JRH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jaisalmer",  "Jaisalmer", "JSA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kolhapur",  "Kolhapur", "KLH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kanpur",  "Kanpur", "KNU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Kota",  "Kota", "KTU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bhuntar Kullu.",  "Kullu Manali", "KUU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Malda",  "Malda", "LDA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Lucknow",  "Amausi", "LKO")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Latur",  "Latur", "LTU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Ludhiana",  "Amritsar", "LUH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Chennai",  "Madras International (Meenambakkam)", "MAA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Dibrugarh",  "Mohanbari", "MOH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Mysore",  "Mysore", "MYQ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Muzaffarnagar",  "Muzaffarnagar", "MZA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Muzaffarpur",  "Muzaffarpur", "MZU")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Nagpur",  "Sonegaon", "NAG")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Nanded",  "Nanded", "NDC")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Daman",  "Daman", "NMB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Neyveli",  "Neyveli", "NVY")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Osmanabad",  "Osmanabad", "OMN")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Bilaspur",  "Bilaspur", "PAB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Patna",  "Patna", "PAT")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Porbandar",  "Porbandar", "PBD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Pantnagar",  "Pantnagar", "PGH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Pune",  "Lohegaon", "PNQ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Pondicherry",  "Pondicherry", "PNY")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Puttaparthi",  "Puttaprathe", "PUT")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Jeypore",  "Jeypore", "PYB")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Rajkot",  "Civil", "RAJ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Rewa",  "Rewa", "REW")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Balurghat",  "Balurghat", "RGH")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Rajahmundry",  "Rajahmundry", "RJA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Rajouri",  "Rajouri", "RJI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Ramagundam",  "Ramagundam", "RMD")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Raipur",  "Raipur", "RPR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Rourkela",  "Rourkela", "RRK")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Ratnagiri",  "Ratnagiri", "RTC")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Rupsi",  "Rupsi", "RUP")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Shillong",  "Barapani", "SHL")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Simla",  "Simla", "SLV")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Sholapur",  "Sholapur", "SSE")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Surat",  "Surat", "STV")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Srinagar",  "Srinagar", "SXR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Salem",  "Salem", "SXV")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Tuticorin",  "Tuticorin", "TCR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Tezu",  "Tezu", "TEI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Tezpur",  "Salonibari", "TEZ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Tirupati",  "Tirupati", "TIR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Thanjavur",  "Thanjavur", "TJV")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Satna",  "Satna", "TNI")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Thiruvananthapuram",  "Thiruvananthapuram International", "TRV")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Trichy",  "Civil", "TRZ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Udaipur",  "Dabok", "UDR")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Vijayawada",  "Vijayawada", "VGA")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Varanasi",  "Varanasi", "VNS")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Visakhapatnam",  "Vishakhapatnam", "VTZ")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Warangal",  "Warangal", "WGC")));
        listOfCityAndAirports.add(new ArrayList<String>(Arrays.asList("Zero",  "Zero", "ZER")));

    }
}
