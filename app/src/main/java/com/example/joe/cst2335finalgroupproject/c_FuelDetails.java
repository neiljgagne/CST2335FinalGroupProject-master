package com.example.joe.cst2335finalgroupproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Nathan on 2017-12-01.
 */

public class c_FuelDetails implements Parcelable {

    private double price;
    private double litres;
    private double kilometers;

    private Date date;

    public c_FuelDetails(double price, double litres, double kilometers, Date date) {
        setPrice(price);
        setLitres(litres);
        setKilometers(kilometers);
        setDate(date);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLitres() {
        return litres;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public double getKilometers() {
        return kilometers;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.price);
        dest.writeDouble(this.litres);
        dest.writeDouble(this.kilometers);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected c_FuelDetails(Parcel in) {
        this.price = in.readDouble();
        this.litres = in.readDouble();
        this.kilometers = in.readDouble();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Parcelable.Creator<c_FuelDetails> CREATOR = new Parcelable.Creator<c_FuelDetails>() {
        @Override
        public c_FuelDetails createFromParcel(Parcel source) {
            return new c_FuelDetails(source);
        }

        @Override
        public c_FuelDetails[] newArray(int size) {
            return new c_FuelDetails[size];
        }
    };
}