package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.network.StockData;
import com.sam_chordas.android.stockhawk.network.StockDataHistory;
import com.sam_chordas.android.stockhawk.network.StockNetworkAdapter;
import com.sam_chordas.android.stockhawk.network.StockQueryHistory;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static StockNetworkAdapter stockNetworkAdapter = StockNetworkAdapter.getAdapter();
    private Spinner spinner;

    private static List<String> sDates;
    private final String TAG = getClass().getSimpleName();

    public class StockFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "";
        }
    }

    private static String sym;

    public static final String queryPart1 =
            "select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20";

    public static final String queryPart2 = "%20and%20startDate%20%3D%20";

    public static final String queryPart3 = "%20and%20endDate%20%3D%20";

    public static final String queryPart4 =
            "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    Callback<StockQueryHistory> response = new Callback<StockQueryHistory>() {
        @Override
        public void success(StockQueryHistory sd, Response response) {

            List<StockDataHistory> sdhistory = sd.query.results.quote;
            populateAndDraw(sdhistory);

        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(TAG, "failure: " + error);

        }
    };

    private void populateAndDraw(List<StockDataHistory> sdh) {

        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        ArrayList<Entry> entries = new ArrayList<>();

        int i = 0, j = sdh.size() - 1;

        float val = 0f;
        for (String s : sDates) {
            StockDataHistory sd = sdh.get(j);

            if (sd.date.equals(s)) {
                val = Float.parseFloat(sd.close);

                if (j > 0)
                    j--;
            }

            entries.add(new Entry(val, i));
            i++;


        }
        // creating list of entry


        LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // creating labels

        LineData data = new LineData(sDates, dataset);
        data.setValueFormatter(new StockFormatter());
        lineChart.setData(data); // set the data and list of lables into chart
        lineChart.animateXY(5000, 7000);


    }

    private void getStockHistory(String symbol, String startdate, String endDate) {
        StringBuilder urlStringBuilder1 = new StringBuilder();

        try {
            // Base URL for the Yahoo query
            urlStringBuilder1.append(queryPart1);
            urlStringBuilder1.append(URLEncoder.encode("\"" + symbol + "\"", "UTF-8"));
            urlStringBuilder1.append(queryPart2);
            urlStringBuilder1.append(URLEncoder.encode("\"" + startdate + "\"", "UTF-8"));
            urlStringBuilder1.append(queryPart3);
            urlStringBuilder1.append(URLEncoder.encode("\"" + endDate + "\"", "UTF-8"));
            urlStringBuilder1.append(queryPart4);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        stockNetworkAdapter.getStockHistory(urlStringBuilder1.toString(), response);
    }

    private void getWeekHistory(String symbol) {
        sDates = Utils.getWeekString();
        getStockHistory(symbol, Utils.getWeekBack(), Utils.getToday());

    }

    private void getMonthHistory(String symbol) {
        sDates = Utils.getMonthString();
        getStockHistory(symbol, Utils.getMonthBack(), Utils.getToday());
    }

    private void getThreeMonthHistory(String symbol) {
        sDates = Utils.getThreeMonthString();
        getStockHistory(symbol, Utils.getThreeMonthBack(), Utils.getToday());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stockHistory, android.R.layout.simple_spinner_item);
        spinner.setOnItemSelectedListener(this);

        sym = getIntent().getStringExtra("stockdata");
        getWeekHistory(sym);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String s = (String) adapterView.getItemAtPosition(i);
        if (s.equals("week")) {
            getWeekHistory(sym);
        } else if (s.equals("30days")) {
            getMonthHistory(sym);
        } else if (s.equals("90days")) {
            getThreeMonthHistory(sym);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
