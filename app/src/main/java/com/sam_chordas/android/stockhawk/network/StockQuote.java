package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gau on 7/6/2016.
 */
public class StockQuote {
    @SerializedName("quote")
    public List<StockData> quote;

    public StockQuote(StockData... s) {
        quote = Arrays.asList(s);
    }
}
