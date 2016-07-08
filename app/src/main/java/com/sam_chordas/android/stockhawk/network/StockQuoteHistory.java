package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gau on 7/6/2016.
 */
public class StockQuoteHistory {

    @SerializedName("quote")
    public List<StockDataHistory> quote;
}
