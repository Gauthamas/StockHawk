package com.sam_chordas.android.stockhawk.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;

/**
 * Created by gau on 7/4/2016.
 */
public class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ContentResolver cr;
    private Cursor c;

    public StockRemoteViewsFactory(Context context){
        mContext = context;
        cr = context.getContentResolver();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        if(c!=null)
            c.close();
        c = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null, QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"}, null);


    }

    @Override
    public void onDestroy() {
        c.close();
    }

    @Override
    public int getCount() {
        if(c!=null)
            return c.getCount();
        else
            return 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        c.moveToPosition(i);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        rv.setTextViewText(R.id.stock_symbol,c.getString(c.getColumnIndex("symbol")));
        rv.setTextViewText(R.id.bid_price,c.getString(c.getColumnIndex("bid_price")));
        rv.setTextViewText(R.id.change,c.getString(c.getColumnIndex("percent_change")));

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(mContext.getString(R.string.stockd), c.getString(c.getColumnIndex("symbol")));
        rv.setOnClickFillInIntent(R.id.list_row, fillInIntent);


        if (c.getInt(c.getColumnIndex("is_up")) == 1){
            rv.setTextColor(R.id.change,mContext.getResources().getColor(R.color.material_green_700));
        } else{
            rv.setTextColor(R.id.change,mContext.getResources().getColor(R.color.material_red_700));
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        if(c!=null)
            return c.getInt(c.getColumnIndex(QuoteColumns._ID));
        else
            return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
