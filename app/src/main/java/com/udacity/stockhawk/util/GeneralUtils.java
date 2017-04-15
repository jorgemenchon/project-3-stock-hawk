package com.udacity.stockhawk.util;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;

import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;


/**
 * Created by jorgemenchon on 31/03/2017.
 */

public final class GeneralUtils {

    public final static String INTENT_STOCK = "intentStock";
    public final static String DATA_FORMATTER_AXIS_GRAPHIC_HISTORY = "MM/yy";
    private static final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    public static final String SEPARATOR_HISTORY = ", ";
    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";

    // FORMAT CONS
    public static final String PERCENTAGE_FORMAT_POSITIVE_PREFIX = "+";
    public static final String DOLLAR_FORMAT_POSITIVE_PREFIX = "+$";
    public static final int PERCENTAGE_FORMAT_MAXIMUM_DIGITS = 2;
    public static final int PERCENTAGE_FORMAT_MINIMUM_DIGITS = 2;

    private GeneralUtils() {
    }

    public static String getInDollarFormat(Float price) {
        return dollarFormat.format(price);
    }

    public static boolean isValid(Stock stock) {
        return stock != null && stock.getName() != null && !stock.getName().isEmpty();
    }

    public static boolean hasHistory(Stock stock, int yearsOfHistory) throws IOException {
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -yearsOfHistory);
        List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);
        return (history != null);
    }

    public static String stockToString(Context context, com.udacity.stockhawk.data.Stock stock) {
        Float absoluteChange = stock.getAbsoluteChange();

        String change = getDollarFormatWithPlus().format(absoluteChange);
        String percentage = getPercentageFormat().format(stock.getPercentageChange() / 100);
        String textDisplayMode = "";
        if (PrefUtils.getDisplayMode(context)
                .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            textDisplayMode = change;
        } else {
            textDisplayMode = percentage;
        }

        if (absoluteChange > 0) {
            return context.getString(R.string.cd_stock_resume_up, stock.getSymbol(), getInDollarFormat(stock.getPrice()), textDisplayMode);
        } else {
            return context.getString(R.string.cd_stock_resume_down, stock.getSymbol(), getInDollarFormat(stock.getPrice()), textDisplayMode);
        }
    }

    public static String stockToStringComplete(Context context, com.udacity.stockhawk.data.Stock stock) {
        Float absoluteChange = stock.getAbsoluteChange();

        String change = getDollarFormatWithPlus().format(absoluteChange);
        String percentage = getPercentageFormat().format(stock.getPercentageChange() / 100);

        if (absoluteChange > 0) {
            return context.getString(R.string.cd_stock_resume_up_complete, stock.getSymbol(), getInDollarFormat(stock.getPrice()), change, percentage);
        } else {
            return context.getString(R.string.cd_stock_resume_down_complete, stock.getSymbol(), getInDollarFormat(stock.getPrice()), change, percentage);
        }
    }

    public static boolean isNetworkUp(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isEmty(String symbol) {
        return (symbol == null || symbol.isEmpty());
    }


    public static DecimalFormat getPercentageFormat() {
        DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(GeneralUtils.PERCENTAGE_FORMAT_MAXIMUM_DIGITS);
        percentageFormat.setMinimumFractionDigits(GeneralUtils.PERCENTAGE_FORMAT_MINIMUM_DIGITS);
        percentageFormat.setPositivePrefix(GeneralUtils.PERCENTAGE_FORMAT_POSITIVE_PREFIX);
        return percentageFormat;
    }

    public static DecimalFormat getDollarFormatWithPlus() {
        DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix(GeneralUtils.DOLLAR_FORMAT_POSITIVE_PREFIX);
        return dollarFormatWithPlus;
    }

    public static DecimalFormat getDollarFormat() {
        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        return dollarFormat;
    }

    public static String addParenthesis(String stringWithoutParenthesis) {
        return "(" + stringWithoutParenthesis + ")";
    }

    public static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}
