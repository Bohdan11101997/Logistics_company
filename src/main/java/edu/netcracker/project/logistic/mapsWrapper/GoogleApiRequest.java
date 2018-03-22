package edu.netcracker.project.logistic.mapsWrapper;

import com.google.appengine.api.appidentity.AppIdentityServiceFailureException;
import com.google.maps.*;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;


public class GoogleApiRequest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GoogleApiRequest.class);

    private static final String RESERVE_API_KEY=  "AIzaSyCU2xp68iQecwO3kZk1cLZCGavoD1j4lIc" ;
    private static final String API_KEY = "AIzaSyBHUNATkvBBtqWEF8roNL_EJEF-IQ1THP0";
    private static long requestQuotePerKey = 2500;
    private static long requestsCount = 0;

    private static GeoApiContext context;
    private static boolean outOfKeys = false;

    private static GoogleApiRequest own;

    private GoogleApiRequest() {
        newСontext(API_KEY);
    }

    private static void TimerToNextDay() {
        TimerToNextDay(null);
    }

    private static void TimerToNextDay(Long milliseconds) {
        Thread deamon = new Thread(() -> {
            while (true) {
                final long millis = milliseconds == null ?
                        DateTime.now().withTime(0, 0, 0, 0).plusDays(1).minus(DateTime.now().getMillisOfDay()).getMillisOfDay()
                        : milliseconds;
                Thread timer = new Thread(() -> {
                    try {
                        Thread.currentThread().sleep(millis); //always running not in main thread
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //"restarting" origin key
                    reset();
                });
                timer.setDaemon(true);
                timer.start();
            }
        });
        deamon.setDaemon(true);
        deamon.start();
    }

    // @return - @true if new value is valid
    public static boolean setQuotePerDay(long quote_per_key) {
        requestQuotePerKey = quote_per_key > 0 ? quote_per_key : requestQuotePerKey;
        return quote_per_key > 0;
    }

    public static long getQuote() {
        return requestQuotePerKey;
    }

    public static long getUsedQuote() {
        return requestsCount;
    }

    private static void reset() {
        logger.info("Resetting key.");
        newСontext(API_KEY);
        outOfKeys = false;
        requestsCount = 0;
    }

    private static boolean rollKeys() {
        if(requestsCount >= getQuote()) {
            if (outOfKeys)
                return false;
            logger.warn("Setting new API_KEY");
            newСontext(RESERVE_API_KEY);
            outOfKeys = true;
        }
        return true;
    }

    private static void newСontext(String key) {
        context = new GeoApiContext.Builder()
                .apiKey(key)
                .build();
    }

    public static GeocodingApiRequest GeocodingApi() {
        if (own == null) own = new GoogleApiRequest();
        requestsCount++;
        if (checkRequestLimit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New Geocoding request.");

        return GeocodingApi.newRequest(context);
    }

    public static DistanceMatrixApiRequest DistanceMatrixApi() {
        if (own == null) own = new GoogleApiRequest();
        requestsCount++;
        if (checkRequestLimit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New DistanceMatrix request.");

        return DistanceMatrixApi.newRequest(context);
    }

    public static DirectionsApiRequest DirectionsApi() {
        if (own == null) own = new GoogleApiRequest();
        requestsCount++;
        if (checkRequestLimit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New DirectionsApi request.");

        return DirectionsApi.newRequest(context);
    }

    public static StaticMap StaticMap() {
        if (own == null) own = new GoogleApiRequest();
        requestsCount++;
        if (checkRequestLimit()) throw new AppIdentityServiceFailureException("Run out of requests limits.");

        logger.info("New StaticMap request.");

        return new StaticMap().key(outOfKeys ? RESERVE_API_KEY : API_KEY);
    }

    private static boolean checkRequestLimit() {
        boolean return_value = requestsCount > requestQuotePerKey;
        if(return_value) {
            logger.error("Google Maps API_KEY quote wasted!");
            return_value = !rollKeys();
        }
        return return_value;
    }
}
