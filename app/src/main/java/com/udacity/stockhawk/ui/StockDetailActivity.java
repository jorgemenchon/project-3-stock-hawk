package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Stock;
import com.udacity.stockhawk.util.GeneralUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_stock_symbol)
    TextView stockSymbol;

    @BindView(R.id.tv_stock_name)
    TextView stockName;

    @BindView(R.id.tv_stock_price)
    TextView stockPrice;

    @BindView(R.id.gv_history)
    GraphView graphHistory;

    @BindView(R.id.tv_stock_change)
    TextView stockChange;

    @BindView(R.id.tv_stock_change_percentage)
    TextView stockChangePercentage;


    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat percentageFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(GeneralUtils.INTENT_STOCK)) {
            Stock stock = intent.getParcelableExtra(GeneralUtils.INTENT_STOCK);

            this.stockSymbol.setText(GeneralUtils.addParenthesis(stock.getSymbol()));
            this.stockName.setText(stock.getName());
            this.stockPrice.setText(GeneralUtils.getInDollarFormat(stock.getPrice()));

            dollarFormatWithPlus = GeneralUtils.getDollarFormatWithPlus();
            percentageFormat = GeneralUtils.getPercentageFormat();

            String change = dollarFormatWithPlus.format(stock.getAbsoluteChange());
            String percentage = percentageFormat.format(stock.getPercentageChange() / 100);
            this.stockChange.setText(change);
            this.stockChangePercentage.setText(GeneralUtils.addParenthesis(percentage));
            putBackgroundResourceToStockChange(stock);
            configureGraph(stock);
        }
    }

    private void configureGraph(Stock stock) {
        LineGraphSeries<DataPoint> series = obtainStockHistorySeries(stock);
        graphHistory.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        graphHistory.setTextDirection(View.TEXT_DIRECTION_RTL);
        graphHistory.addSeries(series);
        DateFormat dateFormat = new SimpleDateFormat(GeneralUtils.DATA_FORMATTER_AXIS_GRAPHIC_HISTORY);
        graphHistory.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, dateFormat));
        graphHistory.setContentDescription(getString(R.string.cd_stock_history));
    }

    private void putBackgroundResourceToStockChange(Stock stock) {
        float rawAbsoluteChange = stock.getAbsoluteChange();

        if (rawAbsoluteChange > 0) {
            stockChange.setTextColor(ContextCompat.getColor(this, R.color.material_green_700));
            stockChange.setContentDescription(getString(R.string.cd_stock_up, stockChange.getText().toString()));
            stockChangePercentage.setTextColor(ContextCompat.getColor(this, R.color.material_green_700));
            stockChangePercentage.setContentDescription(getString(R.string.cd_stock_up, stockChangePercentage.getText().toString()));
        } else {
            stockChange.setTextColor(ContextCompat.getColor(this, R.color.material_red_700));
            stockChange.setContentDescription(getString(R.string.cd_stock_down, stockChange.getText().toString()));
            stockChangePercentage.setTextColor(ContextCompat.getColor(this, R.color.material_red_700));
            stockChangePercentage.setContentDescription(getString(R.string.cd_stock_down, stockChangePercentage.getText().toString()));
        }
    }

    private LineGraphSeries<DataPoint> obtainStockHistorySeries(Stock stock) {
        String stockHistory = stock.getHistory();
        String[] stocksHistorySplit = stockHistory.split("\n");
        int stocksHistorySize = stocksHistorySplit.length;
        Calendar calendar = Calendar.getInstance();
        DataPoint[] dataPointData = new DataPoint[stocksHistorySize];
        int positionArray = 0;
        int positionDate = 0;
        int positionPrice = 1;
        for (int stockIndex = stocksHistorySize - 1; stockIndex >= 0; stockIndex--) {
            String stockWithDate = stocksHistorySplit[stockIndex];
            String[] dateAndStock = stockWithDate.split(GeneralUtils.SEPARATOR_HISTORY);
            String dateMilliSecond = dateAndStock[positionDate];
            String price = dateAndStock[positionPrice];
            calendar.setTimeInMillis(Long.valueOf(dateMilliSecond));
            java.util.Date date = calendar.getTime();
            dataPointData[positionArray] = new DataPoint(date, Double.parseDouble(price));
            positionArray++;
        }
        return new LineGraphSeries<>(dataPointData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
