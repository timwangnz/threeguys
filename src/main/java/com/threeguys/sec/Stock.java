/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.sec;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author anpwang
 */
@JsonIgnoreProperties({"Status", "Name", "Message"})
public class Stock {
    private Integer centralIndexKey;
    @JsonProperty("Symbol")
    private String symbol;
    @JsonProperty("LastPrice")
    private float lastPrice;
    @JsonProperty("Change")
    private float change;
    @JsonProperty("ChangePercent")
    private float chagnePct;
    @JsonProperty("Timestamp")
    private Date timestamp;
    @JsonProperty("MarketCap")
    private float marketCap;
    @JsonProperty("MSDate")
    private float lastTradingDate;
    @JsonProperty("Volume")
    private float volume;
    @JsonProperty("ChangeYTD")
    private float changeYTD;
    @JsonProperty("ChangePercentYTD")
    private float changePctYTD;
    @JsonProperty("High")
    private float high;
    @JsonProperty("Low")
    private float low;
    @JsonProperty("Open")
    private float open;

    public static void main(String[] args) {
        try {
            DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss zXXX yyyy");
             df.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.err.println(df.format(new Date()));
            Date testDate = df.parse("Fri Nov 18 00:00:00 UTC-05:00 2016");
            System.err.println(testDate);
            String json = "{\"Status\":\"SUCCESS\",\"Name\":\"Kopin Corp\",\"Symbol\":\"KOPN\",\"LastPrice\":2.68,\"Change\":0.02,\"ChangePercent\":0.751879699248121,\"Timestamp\":\"Fri Nov 18 00:00:00 UTC-05:00 2016\",\n"
                    + "\"MSDate\":42692,\"MarketCap\":178943600,\"Volume\":355588,\"ChangeYTD\":2.72,\"ChangePercentYTD\":-1.47058823529412,\"High\":2.73,\"Low\":2.58,\"Open\":2.68}";
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.setDateFormat(df);
            Stock stoke = objMapper.readValue(json, Stock.class);
            System.err.println(stoke.timestamp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   
    /**
     * @return the lastPrice
     */
    public float getLastPrice() {
        return lastPrice;
    }

    /**
     * @param lastPrice the lastPrice to set
     */
    public void setLastPrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    /**
     * @return the change
     */
    public float getChange() {
        return change;
    }

    /**
     * @param change the change to set
     */
    public void setChange(float change) {
        this.change = change;
    }

    /**
     * @return the chagnePct
     */
    public float getChagnePct() {
        return chagnePct;
    }

    /**
     * @param chagnePct the chagnePct to set
     */
    public void setChagnePct(float chagnePct) {
        this.chagnePct = chagnePct;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the volume
     */
    public float getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * @return the changeYTD
     */
    public float getChangeYTD() {
        return changeYTD;
    }

    /**
     * @param changeYTD the changeYTD to set
     */
    public void setChangeYTD(float changeYTD) {
        this.changeYTD = changeYTD;
    }

    /**
     * @return the changePctYTD
     */
    public float getChangePctYTD() {
        return changePctYTD;
    }

    /**
     * @param changePctYTD the changePctYTD to set
     */
    public void setChangePctYTD(float changePctYTD) {
        this.changePctYTD = changePctYTD;
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

    /**
     * @return the open
     */
    public float getOpen() {
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(float open) {
        this.open = open;
    }

    /**
     * @return the centralIndexKey
     */
    public Integer getCentralIndexKey() {
        return centralIndexKey;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @return the marketCap
     */
    public float getMarketCap() {
        return marketCap;
    }

    /**
     * @param marketCap the marketCap to set
     */
    public void setMarketCap(float marketCap) {
        this.marketCap = marketCap;
    }

    /**
     * @return the lastTradingDate
     */
    public float getLastTradingDate() {
        return lastTradingDate;
    }

    /**
     * @param lastTradingDate the lastTradingDate to set
     */
    public void setLastTradingDate(float lastTradingDate) {
        this.lastTradingDate = lastTradingDate;
    }

    /**
     * @param centralIndexKey the centralIndexKey to set
     */
    public void setCentralIndexKey(Integer centralIndexKey) {
        this.centralIndexKey = centralIndexKey;
    }

}
