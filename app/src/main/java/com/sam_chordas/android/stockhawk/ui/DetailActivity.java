package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
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
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {


    private static StockNetworkAdapter stockNetworkAdapter = StockNetworkAdapter.getAdapter();
    private static List<String> sDates;
    private static List<String> dates;
    private static String exactDate;
    private static StockDataHistory today = new StockDataHistory();
    private static String sym;
    private final String TAG = getClass().getSimpleName();
    ArrayList<StockDataHistory> sdhl;

    @BindView(R.id.openValue)
    TextView tvOpen;
    @BindView(R.id.closeValue)
    TextView tvClose;
    @BindView(R.id.highValue)
    TextView tvHigh;
    @BindView(R.id.volValue)
    TextView tvVolume;
    @BindView(R.id.dateValue)
    TextView tvDate;
    @BindView(R.id.spinner)
    Spinner spinner;



    Callback<StockQueryHistory> response = new Callback<StockQueryHistory>() {
        @Override
        public void success(StockQueryHistory sd, Response response) {

            List<StockDataHistory> sdhistory = sd.query.results.quote;
            populateAndDraw(sdhistory);

        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(TAG, getString(R.string.failure) + error);

        }
    };

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        StockDataHistory sd = sdhl.get(h.getXIndex());
        tvDate.setText(getString(R.string.date_string) + dates.get(h.getXIndex()));
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

        //Iterate through the previous days to get data just prior to time period
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
        dates = sDates.subList(k, sDates.size());

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

            //if day is holiday only populate close values from previous days.

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
        sdhl.remove(sdhl.size() - 1);
        sdhl.add(today);

        LineDataSet dataset = new LineDataSet(entries, getString(R.string.calls));
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
            urlStringBuilder1.append(getString(R.string.queryPart1));
            urlStringBuilder1.append(URLEncoder.encode("\"" + symbol + "\"", "UTF-8"));
            urlStringBuilder1.append(getString(R.string.queryPart2));
            urlStringBuilder1.append(URLEncoder.encode("\"" + startdate + "\"", "UTF-8"));
            urlStringBuilder1.append(getString(R.string.queryPart3));
            urlStringBuilder1.append(URLEncoder.encode("\"" + endDate + "\"", "UTF-8"));
            urlStringBuilder1.append(getString(R.string.queryPart4));
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);




        sdhl = new ArrayList<StockDataHistory>();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stockHistory, android.R.layout.simple_spinner_item);
        spinner.setOnItemSelectedListener(this);

        sym = getIntent().getStringExtra(getString(R.string.stockd));

        Cursor cr = null;

        try {
            cr = getContentResolver().query(QuoteProvider.Quotes.withSymbol(sym),
                    null, QuoteColumns.ISCURRENT + " = ?",
                    new String[]{"1"}, null);
            if (cr != null && cr.getCount() != 0) {
                cr.moveToFirst();
                int opc = cr.getColumnIndex(QuoteColumns.OPEN);
                int clc = cr.getColumnIndex(QuoteColumns.BIDPRICE);
                int hic = cr.getColumnIndex(QuoteColumns.HIGH);
                int vc = cr.getColumnIndex(QuoteColumns.VOLUME);

                //Today's stock might not be closed, hence added the current tag
                today.close = getString(R.string.curr_string) + cr.getString(clc);
                today.open = cr.getString(opc);
                today.volume = cr.getString(vc);
                today.high = cr.getString(hic);
                String na = getString(R.string.na);

                //On off days put na for everything other than close.
                if (today.open == null) {
                    today.open = na;
                    today.high = na;
                    today.volume = na;

                }
            }
        } catch (
                Exception e
                ) {
            Log.e(TAG, getString(R.string.exception) + e);

        } finally {
            cr.close();
        }

        getWeekHistory(sym);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String s = (String) adapterView.getItemAtPosition(i);
        sdhl.clear();
        if (s.equals(getString(R.string.week))) {
            getWeekHistory(sym);
        } else if (s.equals(getString(R.string.days30))) {
            getMonthHistory(sym);
        } else if (s.equals(getString(R.string.days90))) {
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
