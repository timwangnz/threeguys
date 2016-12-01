/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.ai.stockanalysis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author anpwang
 */
public class SP500Oracle {

    //take three years of data
    private int daysInThePast = 600;
    //recommend buy, sell, hold(0, 1, 2) based on 20 trading days
    private int daysInTheFuture = 30;
    private float buyBand = 1;
    private float sellBand = -1;
    private int numberOfTraingingPoints = 100000, numberOfTestPoints = 100;
    String trainingFileName = "/Users/anpwang/sp500/weka_train_" + daysInThePast + "_" + daysInTheFuture + ".arff";
    String testingFileName = "/Users/anpwang/sp500/weka_test_" + daysInThePast + "_" + daysInTheFuture + ".arff";
    String header = "% 1. Title: S&P 500 Test\n"
            + "% \n"
            + "% 2. Sources:\n"
            + "%      (a) Creator: Anping Wang\n"
            + "%      (b) Date: " + (new Date()) + "\n"
            + "%      (c) Days: " + daysInThePast + " days to predicate " + daysInTheFuture + " days\n"
            + "% \n"
            + "@RELATION SP500\n"
            + " \n";

    String classAttr = "@ATTRIBUTE class {buy,sell,hold}\n";

    String dataAttr = "@DATA\n";

    private static final String[] FILE_HEADER_MAPPING = {"Date", "Open", "High", "Low", "Close", "Volume", "Adj Close"};

    public static void main(String[] args) throws Exception {
        SP500Oracle oracle = new SP500Oracle(10, 600, .01f, -0.01f);
        IndexSet indexSet = getDateSet("/Users/anpwang/sp500/20150824.csv");
        Date first = Index.dataFormat.parse("2012-01-01");
        Date last = Index.dataFormat.parse("2015-01-01");
        oracle.testModel(first, indexSet);
        //oracle.forcast(indexSet, 12000, 10, first, last);
    }

    public SP500Oracle() {
        numberOfTraingingPoints = 1000000;
    }

    public SP500Oracle(int daysInFuture, int daysInPast, float buyBand, float sellBand) {
        this.daysInTheFuture = daysInFuture;
        this.daysInThePast = daysInPast;
        this.buyBand = buyBand;
        this.sellBand = sellBand;
    }

    public void forcast(IndexSet indexSet, float dollars, int steps, Date first, Date last) throws Exception {
        List<Index> indices = indexSet.getHistoricalValue();
        Index startIndex = null;
        for (Index index : indices) {
            if (index.getDate().getTime() > first.getTime()) {
                startIndex = index;
            }
        }
        Index inIndex = null;

        float spshares = 0;
        float shares = 0;

        //  if (startIndex != null) {
        while (startIndex != null) {

            double rec = recommend(startIndex.getDate(), indexSet);

            if (spshares == 0) {
                spshares = dollars / startIndex.getPriceAtClose();
            }

            System.err.print(startIndex.getDate() + "," + rec + "," + startIndex.getPriceAtClose() + "," + spshares * startIndex.getPriceAtClose() + ",");

            if (shares != 0) {
                System.err.print(shares * startIndex.getPriceAtClose());
            } else {
                System.err.print(dollars);
            }

            if (rec == 0 && inIndex == null) {
                inIndex = startIndex;
                shares = dollars / startIndex.getPriceAtClose();
                System.err.print(",bought " + shares + " shares at " + inIndex.getPriceAtClose() + " ");

                startIndex = startIndex.getIndex(steps);

            } else if (rec == 1) {//sell
                if (inIndex != null) {
                    dollars = shares * startIndex.getPriceAtClose();
                    System.err.print(",sold " + shares + " shares at " + startIndex.getPriceAtClose() + " ");
                    shares = 0; //i owe shares
                }
                startIndex = startIndex.getIndex(steps);
                inIndex = null;
            } else {
                startIndex = startIndex.getIndex(steps);
            }
            System.err.print("\n");
            if (startIndex == null || startIndex.getDate().getTime() > last.getTime()) {
                break;
            }
        }
    }

    public static IndexSet getDateSet(String filename) throws Exception {
        File file = new File(filename);

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING).withRecordSeparator("\n");
        CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), csvFileFormat);
        //ValueSet set = new ValueSet();
        IndexSet indexSet = new IndexSet();
        for (CSVRecord csvRecord : parser) {
            if (csvRecord.getRecordNumber() > 1) {
                Index index = Index.parse(csvRecord);
                indexSet.addIndex(index);
                //set.addValue(index.getDate(), index.getPriceAtClose());
            }
        }
        return indexSet;
    }

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public void readData(Date date, IndexSet indexSet) throws Exception {
        prepareData(date, indexSet, true);
        prepareData(addDays(date, 1), indexSet, false);

        ArffLoader loader = new ArffLoader();

        byte[] barray = trainingWriter.toString().getBytes();
        InputStream is = new ByteArrayInputStream(barray);
        loader.setSource(is);
        Instances data = loader.getDataSet();
        is.close();
        loader.reset();

        loader = new ArffLoader();
        barray = testWriter.toString().getBytes();
        is = new ByteArrayInputStream(barray);
        loader.setSource(is);
        Instances testingData = loader.getDataSet();
        loader.reset();
    }

    public void testModel(Date date, IndexSet indexSet) throws Exception {
        prepareData(date, indexSet, true);
        ArffLoader loader = new ArffLoader();
        byte[] barray = trainingWriter.toString().getBytes();
        FileWriter fileWriter = new FileWriter(new File("/Users/anpwang/sp500/testModel.arff"));

        fileWriter.write(trainingWriter.toString());
        fileWriter.close();
        /*
         InputStream is = new ByteArrayInputStream(barray);
         loader.setSource(is);
         Instances data = loader.getDataSet();
         is.close();
         loader.reset();

         J48 buyModel = buildModel(data);
         crossChecking(buyModel, data);
         */
        prepareData(date, indexSet, false);
        fileWriter = new FileWriter(new File("/Users/anpwang/sp500/testModelTest.arff"));

        fileWriter.write(testWriter.toString());
        fileWriter.close();
    }

    public double recommend(Date date, IndexSet indexSet) throws Exception {
        prepareData(date, indexSet, true);
        prepareData(addDays(date, 1), indexSet, false);

        ArffLoader loader = new ArffLoader();

        byte[] barray = trainingWriter.toString().getBytes();

        InputStream is = new ByteArrayInputStream(barray);
        loader.setSource(is);
        Instances data = loader.getDataSet();
        is.close();
        loader.reset();

        J48 buyModel = buildModel(data);
        //crossChecking(buyModel, data);

        loader = new ArffLoader();
        barray = testWriter.toString().getBytes();
        is = new ByteArrayInputStream(barray);
        loader.setSource(is);
        Instances testingData = loader.getDataSet();
        loader.reset();

        testingData.setClassIndex(testingData.numAttributes() - 1);
        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(buyModel, testingData);
        FastVector predictions = eval.predictions();
        double recommendation = -1;
        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            recommendation = np.predicted();
        }
        return recommendation;
    }

    int numberOfAttributes = 0;
    private Writer trainingWriter = null;
    private Writer testWriter = null;

    private void getWriter(boolean training) {
        if (training) {
            trainingWriter = new StringWriter();
        } else {
            testWriter = new StringWriter();
        }

    }

    private void prepareData(Date date, IndexSet indexSet, boolean training) throws Exception {
        getWriter(training);
        numberOfAttributes = 0;
        Writer writer = training ? trainingWriter : testWriter;

        IOUtils.write(header, writer);

        int k = 1;
        for (int j = 0; j < daysInThePast; j += k) {
            IOUtils.write("@ATTRIBUTE d" + (j + 1) + "  NUMERIC\n", writer);
            int t = getIncrement(j);
            if (j > 5) {
                k += t;
            }
            numberOfAttributes++;
        }

        IOUtils.write("@ATTRIBUTE dprice  NUMERIC\n", writer);

        IOUtils.write(classAttr, writer);
        IOUtils.write(dataAttr, writer);

        if (training) {
            trainingSet(date, indexSet, daysInThePast, daysInTheFuture, writer);
        } else {
            testSet(date, indexSet, daysInThePast, daysInTheFuture, writer);
        }
        IOUtils.closeQuietly(writer);
    }

    public int getIncrement(int j) {
        int t = 0;
        if (j > 5) {
            t = 1;
        }
        if (j > 30) {
            t = 2;
        }
        return t;
    }

    private void crossChecking(J48 model, Instances data) throws Exception {

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(model, data, 10, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));

    }

    private J48 buildModel(Instances data) {
        try {
            data.setClassIndex(data.numAttributes() - 1);
            String[] options = new String[1];
            options[0] = "-U";
            J48 tree = new J48();
            tree.setOptions(options);
            tree.buildClassifier(data);
            return tree;
        } catch (Exception ex) {
            Logger.getLogger(SP500Oracle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void trainingSet(Date date, IndexSet indexSet, int daysInThePast, int daysInTheFuture, Writer writer) throws Exception {
        int counts = 0;
        List<Index> indices = indexSet.getHistoricalValue();
        while (counts < indices.size()) {
            Index today = indices.get(counts);
            Index future = today.getIndex(daysInTheFuture);
            Index old = today.getPriorIndex(daysInThePast);
            if (future != null && old != null) { //future must exists to be used for training
                if (future.getDate().getTime() < date.getTime()) { //only take future that is less than given date, otherwise you are using future to train 

                    String comma = "";
                    StringBuffer line = new StringBuffer();
                    int k = 1;
                    int elements = 0;
                    //System.err.println(future.getDate() + " " + today.getDate());
                    for (int j = 0; j < daysInThePast; j += k) {//get daysInThePast as training dates
                        old = today.getPriorIndex(j);
                        line.append(comma).append(old.getDerivative(j));
                        //line.append(comma).append(old.getPriceAtClose());
                        comma = ",";
                        //System.err.println("\t" + old + "," + today + "," + old.getDerivative(j));
                        elements++;
                        //
                        int t = getIncrement(j);
                        if (j > 5) {
                            k += t;
                        }
                    }

                    line.append(comma).append(today.getPriceAtClose());

                    Float change = today.getDerivative(daysInTheFuture);
                    if (change > buyBand) {
                        line.append(comma).append("buy");
                    } else if (change < sellBand) {
                        line.append(comma).append("sell");
                    } else {
                        line.append(comma).append("hold");
                    }
                    line.append("\n");
                    if (elements != numberOfAttributes) {
                        System.err.println("Failed " + elements);
                    } else {

                        IOUtils.write(line, writer);
                    }
                    //System.err.print(today + " "+ future.getDate() + line);
                }
            }
            counts++;
            if (counts > numberOfTraingingPoints) {
                break;
            }
        }
    }

    private void testSet(Date date, IndexSet indexSet, int daysInThePast, int daysInTheFuture, Writer writer) throws Exception {
        int counts = 0;
        List<Index> indices = indexSet.getHistoricalValue();
        while (counts < indices.size()) {
            Index today = indices.get(counts);
        
            Index old = today.getPriorIndex(daysInThePast);
            if (old != null) {
                if (today.getDate().getTime() < date.getTime()) { 

                    String comma = "";
                    StringBuffer line = new StringBuffer();
                    int k = 1;
                    int elements = 0;
                    //System.err.println(future.getDate() + " " + today.getDate());
                    for (int j = 0; j < daysInThePast; j += k) {//get daysInThePast as training dates
                        old = today.getPriorIndex(j);
                        line.append(comma).append(old.getDerivative(j));
                        //line.append(comma).append(old.getPriceAtClose());
                        comma = ",";
                        //System.err.println("\t" + old + "," + today + "," + old.getDerivative(j));
                        elements++;
                        //
                        int t = getIncrement(j);
                        if (j > 5) {
                            k += t;
                        }
                    }

                    line.append(comma).append(today.getPriceAtClose());

                    Float change = today.getDerivative(daysInTheFuture);
                    if (change == null) {
                        line.append(comma).append("hold");
                    } else
                    if (change > buyBand) {
                        line.append(comma).append("buy");
                    } else if (change < sellBand) {
                        line.append(comma).append("sell");
                    } else {
                        line.append(comma).append("hold");
                    }
                    line.append("\n");
                    if (elements != numberOfAttributes) {
                        System.err.println("Failed " + elements);
                    } else {

                        IOUtils.write(line, writer);
                    }
                    //System.err.print(today + " "+ future.getDate() + line);
                }
            }
            counts++;
            if (counts > numberOfTestPoints) {
                break;
            }
        }
    }

    private void _trainingSet(Date date, IndexSet indexSet, int daysInThePast, int daysInTheFuture, Writer writer) throws Exception {
        int counts = 0;
        while (counts < indexSet.getHistoricalValue().size()) {
            Index first = indexSet.getHistoricalValue().get(counts);
            Index today = first.getIndex(daysInThePast);
            if (today != null && today.getIndex(daysInThePast) != null) {
                if (today.getDate().getTime() <= date.getTime()) {
                    String comma = "";
                    StringBuffer line = new StringBuffer();
                    int k = 1;
                    int elements = 0;
                    //System.err.println(date + " " + today.getDate());
                    for (int j = 0; j < daysInThePast; j += k) {
                        Index old = first.getIndex(daysInThePast - j);
                        line.append(comma).append(old.getDerivative(j));

                        comma = ",";
                        elements++;
                        //
                        int t = getIncrement(j);
                        if (j > 5) {
                            k += t;
                        }
                    }

                    Float change = today.getDerivative(daysInTheFuture);
                    if (change > buyBand) {
                        line.append(comma).append("buy");
                    } else if (change < sellBand) {
                        line.append(comma).append("sell");
                    } else {
                        line.append(comma).append("hold");
                    }
                    line.append("\n");
                    if (elements != numberOfAttributes) {
                        System.err.println("Failed " + elements);
                    } else {

                        IOUtils.write(line, writer);
                    }
                    System.err.print(today + " " + line);
                }
            }
            counts++;
        }
    }

    private void _testSet(Date date, IndexSet indexSet, int daysInThePast, int daysInTheFuture, Writer writer) throws Exception {
        int counts = 0;
        while (counts < indexSet.getHistoricalValue().size()) {
            Index first = indexSet.getHistoricalValue().get(counts);
            Index today = first.getIndex(daysInThePast);
            if (today != null) {
                if (today.getDate().getTime() <= date.getTime()) {
                    String comma = "";
                    int k = 1;
                    //System.err.println(today.getDate());
                    //   int count = 0;
                    for (int j = 0; j < daysInThePast; j += k) {
                        Index old = first.getIndex(daysInThePast - j);
                        //System.err.println("\t" + old);
                        IOUtils.write(comma + (old.getDerivative(j)), writer);
                        comma = ",";
                        //
                        int t = getIncrement(j);
                        if (j > 5) {
                            k += t;
                        }
                        //  count++;
                    }
                    IOUtils.write(comma + today.getPriceAtClose(), writer);
                    Float change = today.getDerivative(daysInTheFuture);
                    if (change == null) {
                        IOUtils.write(comma + "hold", writer);
                    } else if (change > buyBand) {
                        IOUtils.write(comma + "buy", writer);
                    } else if (change < sellBand) {
                        IOUtils.write(comma + "sell", writer);
                    } else {
                        IOUtils.write(comma + "hold", writer);
                    }
                    // IOUtils.write(comma + "hold", writer);
                    IOUtils.write("\n", writer);
                    break;
                }
            }
            counts++;
        }
    }
}
