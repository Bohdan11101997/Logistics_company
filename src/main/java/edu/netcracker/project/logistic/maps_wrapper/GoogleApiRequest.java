package edu.netcracker.project.logistic.maps_wrapper;

import com.google.appengine.api.appidentity.AppIdentityServiceFailureException;
import com.google.appengine.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import edu.com.google.maps.*;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;


public class GoogleApiRequest {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GoogleApiRequest.class);

    private static final String API_KEY = "AIzaSyBHUNATkvBBtqWEF8roNL_EJEF-IQ1THP0";
    private static final String RESERVE_API_KEY = "test_key";

    private static long request_quote_per_key = 2500;
    private static long requests_count = 0;

    private static GeoApiContext context;
    private static boolean out_of_keys = false;

    private static GoogleApiRequest own;

    private GoogleApiRequest(){
        new_context(API_KEY);
        TimerToNextDay();
    }

    private static void TimerToNextDay() {
        TimerToNextDay(null);
    }
    //using thread Factory due to Appengine requirements
    private static void TimerToNextDay(Long milliseconds){
        new ThreadFactoryBuilder().build().newThread(() -> {
            while (true)
            {
                final long millis = milliseconds == null ?
                        DateTime.now().withTime(0, 0, 0, 0).plusDays(1).minus(DateTime.now().getMillisOfDay()).getMillisOfDay()
                        : milliseconds;
                new ThreadFactoryBuilder().build().newThread(() -> {
                    try {
                        Thread.currentThread().sleep(millis); //always running not in main thread
                    } catch (InterruptedException e) {e.printStackTrace();}
                    //"restart" origin key
                    reset();
                }).start();
            }
        }).start();
    }

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

    private static void reset(){
        logger.info("Resetting key.");
        new_context(API_KEY);
        out_of_keys = false;
        requests_count = 0;
    }

    private static boolean roll_keys(){
        if(out_of_keys)
            return false;
        logger.info("Setting new API_KEY");
        new_context(RESERVE_API_KEY);
        out_of_keys = true;
        return true;
    }

    private static void new_context(String key){
        context = new GeoApiContext.Builder()
                .apiKey(key)
                .build();
    }

    public static GeocodingApiRequest GeocodingApi(){
        if(own == null) own = new GoogleApiRequest();
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New Geocoding request.");

        return GeocodingApi.newRequest(context);
    }

    public static DistanceMatrixApiRequest DistanceMatrixApi(){
        if(own == null) own = new GoogleApiRequest();
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New DistanceMatrix request.");

        return DistanceMatrixApi.newRequest(context);
    }

    public static DirectionsApiRequest DirectionsApi(){
        if(own == null) own = new GoogleApiRequest();
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New Directions request.");

        return DirectionsApi.newRequest(context);
    }

    public static StaticMap StaticMap(){
        if(own == null) own = new GoogleApiRequest();
        requests_count++;
        if(check_request_limit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New StaticMap request.");

        return new StaticMap().key(out_of_keys ? RESERVE_API_KEY : API_KEY);
    }

    private static boolean check_request_limit() {
        return requests_count > request_quote_per_key && !roll_keys();
    }
}
