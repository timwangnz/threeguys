/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.sec;

/**
 *
 * @author anpwang
 */
public class Company {

    private Integer centralIndexKey; //sec
    private String name;
    private int naiscCode;
    private int sicCode;
    private String stockTicker;
    private String naiscName;
    private String exchange;
    private String state;
    private String incorporated;

    /**
     * @return the centralIndexKey
     */
    public Integer getCentralIndexKey() {
        return centralIndexKey;
    }

    /**
     * @param centralIndexKey the centralIndexKey to set
     */
    public void setCentralIndexKey(Integer centralIndexKey) {
        this.centralIndexKey = centralIndexKey;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the naiscCode
     */
    public int getNaiscCode() {
        return naiscCode;
    }

    /**
     * @param naiscCode the naiscCode to set
     */
    public void setNaiscCode(int naiscCode) {
        this.naiscCode = naiscCode;
    }

    /**
     * @return the sicCode
     */
    public int getSicCode() {
        return sicCode;
    }

    /**
     * @param sicCode the sicCode to set
     */
    public void setSicCode(int sicCode) {
        this.sicCode = sicCode;
    }

    /**
     * @return the stockTicker
     */
    public String getStockTicker() {
        return stockTicker;
    }

    /**
     * @param stockTicker the stockTicker to set
     */
    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    /**
     * @return the naiscName
     */
    public String getNaiscName() {
        return naiscName;
    }

    /**
     * @param naiscName the naiscName to set
     */
    public void setNaiscName(String naiscName) {
        this.naiscName = naiscName;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the incorporated
     */
    public String getIncorporated() {
        return incorporated;
    }

    /**
     * @param incorporated the incorporated to set
     */
    public void setIncorporated(String incorporated) {
        this.incorporated = incorporated;
    }

    /**
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * @param exchange the exchange to set
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return name + ":" + centralIndexKey + "(" + stockTicker + ");";
    }
}
