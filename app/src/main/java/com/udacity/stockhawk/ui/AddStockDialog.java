package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.util.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddStockDialog extends DialogFragment {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText stock;
    FindQuoteBroadcastReceiver broadcastReceiver;
    IntentFilter filter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        ButterKnife.bind(this, custom);
        custom.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addStock();
                return true;
            }
        });

        Dialog dialog = createDialog(custom);

        Window window = dialog.getWindow();

        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        filter = new IntentFilter(FindQuoteBroadcastReceiver.ACTION_DATA_FIND);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        return dialog;
    }

    private Dialog createDialog(View custom) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(custom);
        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addStock();
                    }

                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);
        return builder.create();
    }

    private void addStock() {
        Activity parent = getActivity();
        if (parent instanceof MainActivity) {
            MainActivity parentMainActivity = ((MainActivity) parent);
            String symbol = this.stock.getText().toString();
            if (GeneralUtils.isNetworkUp(parent)) {
                existStock(symbol, parentMainActivity);
            } else {
                parentMainActivity.addStock(symbol);
            }
        }
    }

    private void existStock(String symbol, MainActivity parent) {
        broadcastReceiver = new FindQuoteBroadcastReceiver(parent, symbol);
        parent.registerReceiver(broadcastReceiver, filter);
        Intent msgIntent = new Intent(parent, QuoteIntentService.class);
        msgIntent.putExtra(QuoteIntentService.PARAM_SYMBOL, symbol);
        msgIntent.setAction(QuoteIntentService.ACTION_FIND_QUOTE);
        parent.startService(msgIntent);
    }

    public class FindQuoteBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_DATA_FIND = "com.udacity.stockhawk.ACTION_DATA_FIND";
        private MainActivity parentActivity;
        private String symbol;

        public FindQuoteBroadcastReceiver(MainActivity parentActivity, String symbol) {
            this.parentActivity = parentActivity;
            this.symbol = symbol;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean existsQuote = intent.getBooleanExtra(QuoteIntentService.PARAM_EXISTS_SYMBOL, false);
            if (existsQuote) {
                parentActivity.addStock(symbol);
            } else {
                String errorMessage = parentActivity.getString(R.string.error_stock_no_exist);
                Toast.makeText(parentActivity, errorMessage, Toast.LENGTH_SHORT).show();
            }
            parentActivity.unregisterReceiver(this);
        }
    }
}
