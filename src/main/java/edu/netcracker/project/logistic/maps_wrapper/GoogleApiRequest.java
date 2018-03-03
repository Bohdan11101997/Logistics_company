package edu.netcracker.project.logistic.maps_wrapper;

import com.google.appengine.api.appidentity.AppIdentityServiceFailureException;
import com.google.maps.*;

public class GoogleApiRequest {

    private static final String API_KEY = "AIzaSyBHUNATkvBBtqWEF8roNL_EJEF-IQ1THP0";
    private static final String RESERVE_API_KEY = "test_key";

    private static long request_quote_per_key = 2500;
    private static long requests_count = 0;

    private static GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(API_KEY)
            .build();
    private static boolean out_of_keys = false;

    // @return - @true if new value is valid
    public static boolean setQuotePerDay(long quote_per_key){
        request_quote_per_key = quote_per_key > 0 ? quote_per_key : request_quote_per_key;
        return quote_per_key > 0;
    }

    public static long getQuote(){
        return request_quote_per_key;
    }

    public static long getUsedQuote(){
        return requests_count;
    }

    private static boolean roll_keys(){
        if(out_of_keys)
            return false;
        context = new GeoApiContext.Builder()
                .apiKey(RESERVE_API_KEY)
                .build();
        out_of_keys = true;
        return true;
    }

    private static void new_context(){
        context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
    }

    public static GeocodingApiRequest GeocodingApi(){
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");
        return GeocodingApi.newRequest(context);
    }

    public static DistanceMatrixApiRequest DistanceMatrixApi(){
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");
       return DistanceMatrixApi.newRequest(context);
    }

    public static DirectionsApiRequest DirectionsApi(){
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");
        return DirectionsApi.newRequest(context);
    }

    public static StaticMap StaticMap(){
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");
       return new StaticMap().key(API_KEY);
    }

    private static boolean check_request_limit() {
        return requests_count > request_quote_per_key && !roll_keys();
    }
}
