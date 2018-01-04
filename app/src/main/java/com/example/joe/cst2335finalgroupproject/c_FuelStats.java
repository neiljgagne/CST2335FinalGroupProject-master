package com.example.joe.cst2335finalgroupproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nathan on 2017-12-12.
 */

// Passing an ArrayList of custom objects to another activity
// https://stackoverflow.com/questions/6681217/help-passing-an-arraylist-of-objects-to-a-new-activity
    // http://www.parcelabler.com/
public class c_FuelStats implements Parcelable {

    private String monthYear;
    private double totalPurchases;

    public c_FuelStats(String monthYear, double totalPurchases) {
        this.monthYear = monthYear;
        this.totalPurchases = totalPurchases;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public Double getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(Double totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.monthYear);
        dest.writeDouble(this.totalPurchases);
    }

    protected c_FuelStats(Parcel in) {
        this.monthYear = in.readString();
        this.totalPurchases = in.readDouble();
    }

    public static final Parcelable.Creator<c_FuelStats> CREATOR = new Parcelable.Creator<c_FuelStats>() {
        @Override
        public c_FuelStats createFromParcel(Parcel source) {
            return new c_FuelStats(source);
        }

        @Override
        public c_FuelStats[] newArray(int size) {
            return new c_FuelStats[size];
        }
    };
}