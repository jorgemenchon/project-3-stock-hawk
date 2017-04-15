package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    public static final String ACTION_FIND_QUOTE = "com.udacity.stockhawk.ACTION_FIND_QUOTE";
    public static final String PARAM_SYMBOL = "symbol";
    public static final String PARAM_EXISTS_SYMBOL = "existsSymbol";

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Timber.d("Intent handled. Action : " + action);
        if (action != null && action.equals(ACTION_FIND_QUOTE)) {
            QuoteSyncJob.existsQuote(intent, getApplicationContext());
        } else {
            QuoteSyncJob.getQuotes(getApplicationContext());
        }
    }
}
