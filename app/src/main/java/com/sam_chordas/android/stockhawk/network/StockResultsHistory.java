package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gau on 7/6/2016.
 */
public class StockResultsHistory {

    @SerializedName("results")
    public StockQuoteHistory results;

}
