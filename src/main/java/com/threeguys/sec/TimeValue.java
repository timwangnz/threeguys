/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.sec;

import java.util.Date;

/**
 *
 * @author anpwang
 */
public class TimeValue {

    private long time;
    private float value;

    public TimeValue() {

    }

    public TimeValue(Date date, float value) {
        time = date.getTime() / 1000;
        this.value = value;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(float value) {
        this.value = value;
    }
}
