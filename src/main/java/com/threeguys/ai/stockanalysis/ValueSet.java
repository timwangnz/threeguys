/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author anpwang
 */
public class ValueSet {

    private double max = -1000000000000d;
    private double min = 1000000000000d;
    private double sum = 0;
    private double average = 0;

    private final LinkedList<Value> values = new LinkedList<>();

    /**
     * @return the historicalValue
     */
    public List<Value> getValues() {
        return values;
    }

    public void addValue(Date date, Float value) {
        Value valueValue = new Value(date, value);
        
        if (!values.isEmpty())
        {
            Value lastValue = values.getLast();
            lastValue.setNext(valueValue);
        }
        
        values.addLast(valueValue);
        int count = values.size();

        if (value > getMax()) {
            max = value;
        }
        
        if (value < getMin()) {
            min = value;
        }
        
        sum = getSum() + value;
        average = getSum() / count;
    }

    public List<Float> getValueValues() {
        List<Float> returnValues = new ArrayList<>();
        for (Value value : this.values) {
            returnValues.add(value.getValue());
        }
        return returnValues;
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

    @Override
    public String toString() {
        return "Number of Values :" + this.values.size() + "\n"
                + "Average Price :" + this.average + "\n"
                + "Min Price :" + this.min + "\n"
                + "Max Price :" + this.max + "\n";

    }
}
