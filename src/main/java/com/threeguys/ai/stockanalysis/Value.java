/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.util.Date;

/**
 *
 * @author anpwang
 */
public class Value {

    private final Date date;
    private final Float value;
    private Value next;

    @Override
    public String toString() {
        return date + " " + value;
    }

    Value(Date date, Float value) {
        this.date = date;
        this.value = value;
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
    public Float getValue() {
        return value;
    }

    /**
     * @return the next
     */
    public Value getNext() {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(Value next) {
        this.next = next;
    }
}
