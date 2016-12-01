/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author anpwang
 */
public class Index {

    private float priceAtOpen;
    private float priceAtClose;
    private long volume;
    private Date date;
    private float high;
    private float low;
    
    private Index next;
    private Index last;
    
    static final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static Index parse(CSVRecord record) throws ParseException {
        Index index = new Index();
        index.volume = Long.valueOf(record.get("Volume"));
        index.date = dataFormat.parse(record.get("Date"));
        index.high = Float.valueOf(record.get("High"));
        index.low = Float.valueOf(record.get("Low"));
        index.priceAtClose = Float.valueOf(record.get("Close"));
        index.priceAtOpen = Float.valueOf(record.get("Open"));
        return index;
    }

    public Float getDerivative(int days) {
        int i = 0;
        Index v = this;
        while (i < days && v != null) {
            v = v.next;
            i++;
        }
        if (v != null) {
            return (v.priceAtClose - priceAtClose) * 100 / priceAtClose;
        }
        return null;
    }

    public Index getPriorIndex(int days) {
        int i = 0;
        Index v = this;
        while (i < days && v != null) {
            v = v.last;
            i++;
        }
        if (v != null) {
            return v;
        }
        return null;
    }

    public Index getIndex(int days) {
        int i = 0;
        Index v = this;
        while (i < days && v != null) {
            v = v.next;
            i++;
        }
        if (v != null) {
            return v;
        }
        return null;
    }

    /**
     * @return the priceAtOpen
     */
    public float getPriceAtOpen() {
        return priceAtOpen;
    }

    /**
     * @param priceAtOpen the priceAtOpen to set
     */
    public void setPriceAtOpen(float priceAtOpen) {
        this.priceAtOpen = priceAtOpen;
    }

    /**
     * @return the priceAtClose
     */
    public float getPriceAtClose() {
        return priceAtClose;
    }

    /**
     * @param priceAtClose the priceAtClose to set
     */
    public void setPriceAtClose(float priceAtClose) {
        this.priceAtClose = priceAtClose;
    }

    /**
     * @return the volume
     */
    public long getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(long volume) {
        this.volume = volume;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the high
     */
    public float getHigh() {
        return high;
    }

    /**
     * @param high the high to set
     */
    public void setHigh(float high) {
        this.high = high;
    }

    /**
     * @return the low
     */
    public float getLow() {
        return low;
    }

    /**
     * @param low the low to set
     */
    public void setLow(float low) {
        this.low = low;
    }

    @Override
    public String toString() {
        return this.date + "," + this.priceAtOpen + "," + this.high + "," + this.low + "," + this.priceAtClose + "," + this.volume;
    }

    public void setNext(Index index) {
        next = index;
        next.last = this;
    }

    public Index getNext() {
        return next;
    }
}
