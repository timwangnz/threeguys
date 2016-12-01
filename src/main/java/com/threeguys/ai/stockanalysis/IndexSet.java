/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author anpwang
 */
public class IndexSet {

    private double max = -1000000000000d;
    private double min = 1000000000000d;
    private double sum = 0;
    private double average = 0;

    private final LinkedList<Index> values = new LinkedList<>();

    /**
     * @return the historicalValue
     */
    public List<Index> getHistoricalValue() {
        return values;
    }

    public void addIndex(Index index) {

        if (!values.isEmpty()) {
            Index lastValue = values.getLast();
            index.setNext(lastValue);
        }
        
        values.addLast(index);
        int count = values.size();
        float price = index.getPriceAtClose();
        if (price > getMax()) {
            setMax(price);
        }
        if (price < getMin()) {
            min = price;
        }
        sum = getSum() + price;
        average = getSum() / count;
    }

    /**
     * @return the max
     */
    public double getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @return the sum
     */
    public double getSum() {
        return sum;
    }

    /**
     * @return the average
     */
    public double getAverage() {
        return average;
    }

    public List<Float> getValues() {
        List<Float> returnValues = new ArrayList<>();
        for (Index index : this.values) {
            returnValues.add(index.getPriceAtClose());
        }
        return returnValues;
    }

    @Override
    public String toString() {
        return "Number of Values :" + this.values.size() + "\n"
                + "Average Price :" + this.average + "\n"
                + "Min Price :" + this.min + "\n"
                + "Max Price :" + this.max + "\n";

    }
}
