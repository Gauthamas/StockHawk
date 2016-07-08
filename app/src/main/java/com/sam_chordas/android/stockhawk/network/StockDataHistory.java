package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gau on 7/6/2016.
 */
public class StockDataHistory {

    @SerializedName("Symbol")
    public String symbol;

    @SerializedName("Date")
    public String date;

    @SerializedName("Open")
    public String open;

    @SerializedName("High")
    public String high;

    @SerializedName("Low")
    public String low;

    @SerializedName("Close")
    public String close;

    @SerializedName("Volume")
    public String volume;


}
