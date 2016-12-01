/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.util.List;
import java.util.Map;

/**
 *
 * @author anpwang
 */
public class Histogram {
    
    public static void histograms(ValueSet set) {
        IndexStatics.IndexRange[] ranges = new IndexStatics.IndexRange[]{
            new IndexStatics.IndexRange("low", set.getMin(), (set.getMin() + set.getMax()) / 2),
            new IndexStatics.IndexRange("high", (set.getMin() + set.getMax()) / 2, set.getMax())
        };

        //IndexStatics stats = new IndexStatics(set.getValues(), ranges);
        System.err.println(set.getValues().size());

        List<Derivative> changes = Derivative.calculate(set, 1);
        System.err.println(set.getValues().size() + ":" + changes.size());
        ValueSet changeSet = new ValueSet();

        for (Derivative derivative : changes) {
            changeSet.addValue(derivative.getDate(), derivative.getRate());
            //System.err.println(derivative);
        }

        IndexStatics.IndexRange[] changeRanges = new IndexStatics.IndexRange[]{
            new IndexStatics.IndexRange("<-10", -100, -10),
            new IndexStatics.IndexRange("-10:-5", -10, -5),
            new IndexStatics.IndexRange("-100:-3", -100, -0),
            new IndexStatics.IndexRange("-3:-1", -3, -1),
            new IndexStatics.IndexRange("-1:-0.5", -1, -0.5),
            new IndexStatics.IndexRange("-0.5:0", -0.5, 0),
            new IndexStatics.IndexRange("0:0.5", 0, 0.5),
            new IndexStatics.IndexRange("0.5:1", 0.5, 1),
            new IndexStatics.IndexRange("1:3", 1, 3),
            new IndexStatics.IndexRange(">3", 3, 100)
        };

        IndexStatics stats = new IndexStatics(changeSet.getValues(), changeRanges);

        Map<IndexStatics.IndexRange, List<Value>> classified = stats.getClassified();

        int positive = 0, negative = 0, neg2 = 0, neg3 = 0, neg4 = 0;
        for (Value value : classified.get(changeRanges[2])) {
            Value first = value.getNext();
            if (first == null) {
                continue;
            }
            if (first.getValue() > 0) {
                positive++;
            } else {
                negative++;
                Value second = first.getNext();
                if (second != null && second.getValue() < -1) {
                    Value third = second.getNext();
                    if (third != null && third.getValue() < -2) {

                        neg3++;
                        Value fourth = third.getNext();
                        //Float growth = third.getDerivative(1);
                        if (fourth != null && fourth.getValue() < 0) {
                            System.err.println("-------------------\n" + value + "\n" + first + "\n" + second + "\n" + third + "\n" + fourth + "\n-------------------");
                            neg4 = neg4 + (fourth.getValue() < -1 ? 1 : 0);
                        } else {
                            System.err.println("-------------------\n" + value + "\n" + first + "\n" + second + "\n" + third + "\n-------------------");
                        }
                    }
                    neg2++;
                }
            }
        }

        System.err.println(positive + ":" + negative + ":" + neg2 + ":" + neg3 + ":" + neg4);
        /*
         for (IndexRange range : classified.keySet()) {
         System.err.println(range.getName() + " " + classified.get(range).size());
         }
         */
    }
}
