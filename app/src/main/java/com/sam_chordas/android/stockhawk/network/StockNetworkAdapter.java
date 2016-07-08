package com.sam_chordas.android.stockhawk.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by gau on 7/6/2016.
 */
public class StockNetworkAdapter {

    protected RestAdapter mRestAdapter;


    static StockNetworkAdapter sStockAdapter;
    static final String STOCK_URL = "https://query.yahooapis.com";
    protected StocksApi mApi;

    public static StockNetworkAdapter getAdapter() {
        if (sStockAdapter == null) {
            sStockAdapter = new StockNetworkAdapter();
        }
        return sStockAdapter;
    }

    public StockNetworkAdapter() {
        Gson gson = new GsonBuilder().registerTypeAdapter(StockQuote.class, new StockQuoteTypeAdapter()).create();
        mRestAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(STOCK_URL)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new StockApiErrorHandler())
                .build();
        mApi = mRestAdapter.create(StocksApi.class);
    }

    public StockQuery getStock(String criteria) {

        return mApi.getStock(criteria);

    }

    public void getStockHistory(String criteria, Callback<StockQueryHistory> response) {

        mApi.getStockHistory(criteria, response);

    }


}
