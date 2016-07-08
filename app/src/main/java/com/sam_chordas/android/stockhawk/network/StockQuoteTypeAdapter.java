package com.sam_chordas.android.stockhawk.network;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by gau on 7/7/2016.
 */
public class StockQuoteTypeAdapter extends TypeAdapter<StockQuote> {

    private Gson gson = new Gson();

    @Override
    public void write(JsonWriter jsonWriter, StockQuote quotes) throws IOException {
        gson.toJson(quotes, StockQuote.class, jsonWriter);
    }

    @Override
    public StockQuote read(JsonReader jsonReader) throws IOException {
        StockQuote quotes;

        jsonReader.beginObject();
        jsonReader.nextName();

        if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
            quotes = new StockQuote((StockData[]) gson.fromJson(jsonReader, StockData[].class));
        } else if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
            quotes = new StockQuote((StockData) gson.fromJson(jsonReader, StockData.class));
        } else {
            throw new JsonParseException("Unexpected token " + jsonReader.peek());
        }

        jsonReader.endObject();
        return quotes;
    }
}