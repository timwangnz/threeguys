/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.sec;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author anpwang
 */
public class TimeSeries {
    public enum Type {
        yearly, quartly, daily, instant;
    }
    private String name;
    private int key;
    private Type type; //yearly, quartly, daily
    private final List<TimeValue> values = new ArrayList<>();

    public void addValue(Date date, float value) {
        values.add(new TimeValue(date, value));
    }

    TimeSeries(int key, String name, Type type) {
        this.key = key;
        this.name = name;
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the key
     */
    public int getKey() {
        return key;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param key the key to set
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    public List<TimeValue> getValues() {
        return this.values;
    }
    @Override
    public String toString()
    {
        return name + ":" +type + "(" +  this.values.size() + ");";
    }
}
