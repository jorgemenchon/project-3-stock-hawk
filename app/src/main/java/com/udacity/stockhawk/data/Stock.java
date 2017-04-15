package com.udacity.stockhawk.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.util.GeneralUtils;

/**
 * Created by jorgemenchon on 01/04/2017.
 */

public class Stock implements Parcelable {

    private String symbol;
    private Integer id;
    private Float price;
    private Float absoluteChange;
    private Float percentageChange;
    private String history;
    private String name;

    public Stock(Integer id, String symbol, Float price, Float absoluteChange, Float percentageChange, String history, String name) {
        this.symbol = symbol;
        this.id = id;
        this.price = price;
        this.absoluteChange = absoluteChange;
        this.percentageChange = percentageChange;
        this.history = history;
        this.name = name;
    }


    protected Stock(Parcel in) {
        symbol = in.readString();
        history = in.readString();
        id = in.readInt();
        price = in.readFloat();
        absoluteChange = in.readFloat();
        percentageChange = in.readFloat();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(symbol);
        dest.writeString(history);
        dest.writeInt(id);
        dest.writeFloat(price);
        dest.writeFloat(absoluteChange);
        dest.writeFloat(percentageChange);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getAbsoluteChange() {
        return absoluteChange;
    }

    public void setAbsoluteChange(Float absoluteChange) {
        this.absoluteChange = absoluteChange;
    }

    public Float getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(Float percentageChange) {
        this.percentageChange = percentageChange;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
