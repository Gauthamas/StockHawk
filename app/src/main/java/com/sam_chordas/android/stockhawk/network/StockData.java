package com.sam_chordas.android.stockhawk.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gau on 7/6/2016.
 */
public class StockData {


    @SerializedName("Bid")
    public String bid;

    @SerializedName("Change")
    public String change;

    @SerializedName("ChangeinPercent")
    public String changePercent;

    @SerializedName("symbol")
    public String symbol;

}
