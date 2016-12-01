/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author anpwang
 */
public class IndexStatics {

    private double max;
    private double min;

    private final Map<IndexRange, Integer> histogram = new HashMap<>();
    private final Map<IndexRange, List<Value>> classified = new HashMap<>();

    private final double average;
    private double variance;

    public IndexStatics(List<Value> values, IndexRange[] ranges) {
        max = -1000000000000.0;
        min = 1000000000000.0;

        double sum = 0;
        int count = 0;

        for (Value valueValue : values) {
            Float value = valueValue.getValue();
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }

            for (IndexRange range : ranges) {
                if (range.inRange(value)) {
                    Integer num = histogram.get(range);
                    if (num == null) {
                        num = 0;
                    }
                    num++;
                    histogram.put(range, num);
                    List<Value> classifiedValue = classified.get(range);
                    if (classifiedValue == null) {
                        classifiedValue = new ArrayList<>();
                        classified.put(range, classifiedValue);
                    }
                    classifiedValue.add(valueValue);

                }
            }

            sum = sum + value;
            count++;
        }
        average = sum / count;
    }

    /**
     * @return the max
     */
    public double getMax() {
        return max;
    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @return the histogram
     */
    public Map<IndexRange, Integer> getHistogram() {
        return histogram;
    }

    /**
     * @return the average
     */
    public double getAverage() {
        return average;
    }

    /**
     * @return the variance
     */
    public double getVariance() {
        return variance;
    }

    /**
     * @return the classified
     */
    public Map<IndexRange, List<Value>> getClassified() {
        return classified;
    }

    public static class IndexRange {

        private final String name;
        private final double high;
        private final double low;

        IndexRange(String name, double low, double high) {
            this.name = name;
            this.low = low;
            this.high = high;
        }

        public boolean inRange(double value) {
            return value < getHigh() && value >= getLow();
        }

        public static List<IndexRange> getRanges(double[] limits) {
            List<IndexRange> ranges = new ArrayList<>();

            double last = limits[0];

            for (int i = 1; i < limits.length; i++) {
                ranges.add(new IndexRange("" + i, last, limits[i]));
                last = limits[i];
            }
            return ranges;
        }

        @Override
        public String toString() {
            return getName();
        }

        /**
         * @return the high
         */
        public double getHigh() {
            return high;
        }

        /**
         * @return the low
         */
        public double getLow() {
            return low;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
    }
}
