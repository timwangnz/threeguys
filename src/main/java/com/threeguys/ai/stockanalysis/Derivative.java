/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author anpwang
 */
public class Derivative {
    private final Date date;
    private final float value;
    private final float change;
    private final float rate;
    public static List<Derivative> calculate(ValueSet set, int steps) {
        List<Derivative> derivatives = new ArrayList<>();
        List<Value> indices = set.getValues();
        for (int i = indices.size() - 1; i >= 0; i--) {
            Value old = indices.get(i);
            if (i - steps >= 0) {
                Value next = indices.get(i - steps);
                derivatives.add(new Derivative(next.getDate(), next.getValue() - old.getValue(), old.getValue()));
            }
        }
        return derivatives;
    }

    private Derivative(Date date, float change, float value) {
        this.date = date;
        this.change = change;
        this.value = value;
        this.rate = change*100/value;
    }

    @Override
    public String toString() {
        return this.getDate() + ":" + this.value + " " + this.getRate();
    }
    
    public float getRate()
    {
        return this.rate;
    }
    
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }

    /**
     * @return the change
     */
    public float getChange() {
        return change;
    }
}
