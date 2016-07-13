package com.sam_chordas.android.stockhawk.rest;


import android.content.ContentProviderOperation;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.network.StockData;
import com.sam_chordas.android.stockhawk.network.StockQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {


    public static boolean showPercent = true;
    private static String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList quoteJsonToContentVals(StockQuery sq) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        try {

            if (sq != null) {
                List<StockData> sd = sq.query.results.quote;

                for (StockData s : sd) {
                    batchOperations.add(buildBatchOperation(s));
                }
            }


        } catch (Exception e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(StockData sd) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = sd.change;
            builder.withValue(QuoteColumns.SYMBOL, sd.symbol);
            builder.withValue(QuoteColumns.BIDPRICE, sd.bid);
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    sd.changePercent, true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static String getToday() {

        Calendar rightNow = Calendar.getInstance();

        return getDate(rightNow);
    }

    private static String getDate(Calendar rightNow) {
        int year = rightNow.get(Calendar.YEAR);
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH);
        month++;

        String dayS = Integer.toString(day);
        if (day < 10)
            dayS = "0" + dayS;

        String monthS = Integer.toString(month);
        if (month < 10)
            monthS = "0" + monthS;

        return Integer.toString(year) + "-" + monthS + "-" + dayS;
    }

    public static String getWeekBack() {

        Calendar rightNow = Calendar.getInstance();
        rightNow.add(Calendar.DATE, -6);

        return getDate(rightNow);
    }

    public static String getMonthBack() {

        Calendar rightNow = Calendar.getInstance();
        rightNow.add(Calendar.DATE, -29);

        return getDate(rightNow);


    }

    public static String getThreeMonthBack() {

        Calendar rightNow = Calendar.getInstance();
        rightNow.add(Calendar.DATE, -89);

        return getDate(rightNow);

    }

    public static List<String> getWeekandSomeString() {

        Calendar rightNow = Calendar.getInstance();
        List<String> week = new ArrayList<String>();
        rightNow.add(Calendar.DATE, -11);
        week.add(getDate(rightNow));

        for (int i = 0; i < 11; i++) {
            rightNow.add(Calendar.DATE, 1);
            week.add(getDate(rightNow));

        }
        return week;

    }


    public static List<String> getMonthandSomeString() {

        Calendar rightNow = Calendar.getInstance();
        List<String> week = new ArrayList<String>();
        rightNow.add(Calendar.DATE, -34);
        week.add(getDate(rightNow));

        for (int i = 0; i < 34; i++) {
            rightNow.add(Calendar.DATE, 1);
            week.add(getDate(rightNow));

        }
        return week;

    }

    public static List<String> getThreeMonthandSomeString() {

        Calendar rightNow = Calendar.getInstance();
        List<String> week = new ArrayList<String>();
        rightNow.add(Calendar.DATE, -94);
        week.add(getDate(rightNow));

        for (int i = 0; i < 94; i++) {
            rightNow.add(Calendar.DATE, 1);
            week.add(getDate(rightNow));

        }
        return week;

    }
}
