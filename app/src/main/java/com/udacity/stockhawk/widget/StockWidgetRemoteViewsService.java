package com.udacity.stockhawk.widget;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.support.v4.content.ContextCompat;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Stock;
import com.udacity.stockhawk.ui.StockDetailActivity;
import com.udacity.stockhawk.util.GeneralUtils;

import java.text.DecimalFormat;

/**
 * Created by jorgemenchon on 09/04/2017.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private DecimalFormat dollarFormatWithPlus;
            private DecimalFormat percentageFormat;
            private Cursor dataQuotes = null;

            @Override
            public void onCreate() {
                dollarFormatWithPlus = GeneralUtils.getDollarFormatWithPlus();
                percentageFormat = GeneralUtils.getPercentageFormat();
            }

            @Override
            public void onDataSetChanged() {
                if (dataQuotes != null) {
                    dataQuotes.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                Uri uriAllItems = Contract.Quote.URI;
                dataQuotes = getContentResolver().query(uriAllItems, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (dataQuotes != null) {
                    dataQuotes.close();
                    dataQuotes = null;
                }
            }

            @Override
            public int getCount() {
                return dataQuotes == null ? 0 : dataQuotes.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        dataQuotes == null || !dataQuotes.moveToPosition(position)) {
                    return null;
                }


                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.widget_stock_list_item);

                String symbol = dataQuotes.getString(Contract.Quote.POSITION_SYMBOL);
                Integer id = dataQuotes.getInt(Contract.Quote.POSITION_ID);
                Float price = dataQuotes.getFloat(Contract.Quote.POSITION_PRICE);
                Float absoluteChange = dataQuotes.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                Float percentageChange = dataQuotes.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                String history = dataQuotes.getString(Contract.Quote.POSITION_HISTORY);
                String name = dataQuotes.getString(Contract.Quote.POSITION_NAME);

                Stock stock = new Stock(id, symbol, price, absoluteChange, percentageChange, history, name);

                remoteViews.setTextViewText(R.id.tv_symbol_widget, symbol);
                remoteViews.setTextViewText(R.id.tv_price_widget, GeneralUtils.getInDollarFormat(price));
                String change = dollarFormatWithPlus.format(stock.getAbsoluteChange());
                String percentage = percentageFormat.format(stock.getPercentageChange() / 100);
                remoteViews.setTextViewText(R.id.tv_change_widget, change);
                remoteViews.setTextViewText(R.id.tv_change_percentage_widget, GeneralUtils.addParenthesis(percentage));


                if (absoluteChange > 0) {
                    remoteViews.setInt(R.id.tv_change_widget, "setTextColor", ContextCompat.getColor(getApplicationContext(), R.color.material_green_700));
                    remoteViews.setInt(R.id.tv_change_percentage_widget, "setTextColor", ContextCompat.getColor(getApplicationContext(), R.color.material_green_700));
                } else {
                    remoteViews.setInt(R.id.tv_change_widget, "setTextColor", ContextCompat.getColor(getApplicationContext(), R.color.material_red_700));
                    remoteViews.setInt(R.id.tv_change_percentage_widget, "setTextColor", ContextCompat.getColor(getApplicationContext(), R.color.material_red_700));
                }

                Class destinationActivity = StockDetailActivity.class;
                Intent intent = new Intent(StockWidgetRemoteViewsService.this, destinationActivity);
                intent.putExtra(GeneralUtils.INTENT_STOCK, stock);
                remoteViews.setOnClickFillInIntent(R.id.widget_list_item, intent);
                remoteViews.setContentDescription(R.id.widget_list_item, GeneralUtils.stockToStringComplete(getApplicationContext(), stock));
                return remoteViews;
            }

          /*  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }*/

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_stock_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (dataQuotes.moveToPosition(position))
                    return dataQuotes.getLong(Contract.Quote.POSITION_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }


}
