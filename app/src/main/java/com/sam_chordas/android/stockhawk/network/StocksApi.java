package com.sam_chordas.android.stockhawk.network;

import retrofit.Callback;
import retrofit.http.EncodedQuery;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by gau on 7/6/2016.
 */
public interface StocksApi {

    @GET("/v1/public/yql")
    public StockQuery getStock(@EncodedQuery("q") String query);

    @GET("/v1/public/yql")
    public void getStockHistory(@EncodedQuery("q") String query, Callback<StockQueryHistory> response);

}
