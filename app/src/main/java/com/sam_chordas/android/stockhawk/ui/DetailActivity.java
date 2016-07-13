package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    public static final String queryPart1 =
            "select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20";
    public static final String queryPart2 = "%20and%20startDate%20%3D%20";
    public static final String queryPart3 = "%20and%20endDate%20%3D%20";
    public static final String queryPart4 =
            "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private static StockNetworkAdapter stockNetworkAdapter = StockNetworkAdapter.getAdapter();
    private static List<String> sDates;
    private static String exactDate;
    private static StockDataHistory today = new StockDataHistory();
    private static String sym;
    private final String TAG = getClass().getSimpleName();
    ArrayList<StockDataHistory> sdhl;
    TextView tvOpen, tvClose, tvHigh, tvVolume;
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
    private Spinner spinner;

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        StockDataHistory sd = sdhl.get(h.getXIndex());
        tvVolume.setText(getString(R.string.volume_string) + sd.volume);
        tvHigh.setText(getString(R.string.high_string) + sd.high);
        tvOpen.setText(getString(R.string.open_string) + sd.open);
        tvClose.setText(getString(R.string.close_string) + sd.close);

    }

    @Override
    public void onNothingSelected() {

    }

    private void populateAndDraw(List<StockDataHistory> sdh) {

        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        ArrayList<Entry> entries = new ArrayList<>();


        int i = 0, j = sdh.size() - 1, k = 0;
        StockDataHistory curr = null;
        for (String s : sDates) {
            if (s.equals(exactDate))
                break;
            StockDataHistory sd = sdh.get(j);
            if (sd.date.equals(s)) {
                curr = sd;
                j--;
            }
            k++;
        }
        List<String> dates = sDates.subList(k, sDates.size());

        float val = 0f;
        String na = getString(R.string.na);
        for (; k < sDates.size(); k++) {
            String s = sDates.get(k);
            StockDataHistory sd = sdh.get(j);
            boolean isCurr = false;
            if (sd.date.equals(s)) {
                val = Float.parseFloat(sd.close);
                isCurr = true;
                curr = sd;
                if (j > 0)
                    j--;
            }

            if (!isCurr) {
                StockDataHistory sdcurr = new StockDataHistory();
                sdcurr.close = curr.close;
                sdcurr.high = na;
                sdcurr.open = na;
                sdcurr.volume = na;
                curr = sdcurr;
            }
            entries.add(new Entry(val, i));
            sdhl.add(curr);
            i++;


        }
        // creating list of entry


        LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // creating labels


        lineChart.setOnChartValueSelectedListener(this);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(false);
        lineChart.setSelected(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);


        LineData data = new LineData(dates, dataset);
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
        sDates = Utils.getWeekandSomeString();
        exactDate = Utils.getWeekBack();
        getStockHistory(symbol, sDates.get(0), Utils.getToday());

    }

    private void getMonthHistory(String symbol) {
        sDates = Utils.getMonthandSomeString();
        exactDate = Utils.getMonthBack();
        getStockHistory(symbol, sDates.get(0), Utils.getToday());
    }

    private void getThreeMonthHistory(String symbol) {
        sDates = Utils.getThreeMonthandSomeString();
        exactDate = Utils.getThreeMonthBack();
        getStockHistory(symbol, sDates.get(0), Utils.getToday());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        tvOpen = (TextView) findViewById(R.id.openValue);
        tvClose = (TextView) findViewById(R.id.closeValue);
        tvHigh = (TextView) findViewById(R.id.highValue);
        tvVolume = (TextView) findViewById(R.id.volValue);

        sdhl = new ArrayList<StockDataHistory>();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stockHistory, android.R.layout.simple_spinner_item);
        spinner.setOnItemSelectedListener(this);

        sym = getIntent().getStringExtra("stockdata");
        getWeekHistory(sym);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String s = (String) adapterView.getItemAtPosition(i);
        sdhl.clear();
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

    public class StockFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "";
        }
    }
}
