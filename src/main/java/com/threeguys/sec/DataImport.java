/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.threeguys.sec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.codehaus.jackson.map.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author anpwang
 */
public class DataImport {

    private final static Logger sLogger = LoggerFactory.getLogger(DataImport.class);
    private static final String DATA_DIR = "/Users/anpwang/NetBeansProjects/stockanalysis/data";

    private static final Map<Integer, Company> companyMap = new HashMap<>();
    static DateFormat dataformat = new SimpleDateFormat("M-dd-yy");
    static Map<Integer, List<TimeSeries>> series = new HashMap<>();

    private static final String[] FILE_HEADER_MAPPING
            = {"SEC ID", "Name", "oldNames",
                "sectorName", "sectorCode",
                "oldSectorNames", "oldSectorCodes",
                "sicCode", "oldSicCodes"};

    private static final String[] COMPANY_TICKER_HEADER_MAPPING
            = {"CIK", "Ticker", "Name",
                "Exchange", "SIC",
                "State", "Incorporated",
                "IRS"};

    private static final String[] COMPANY_DETAILS_HEADER_MAPPING
            = {"", "1", "2",
                "3", "4",
                "5", "6",
                "7", "8", "9"};

    private static final String quoteUrl = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=";
    private static final String HTTP_GET = "GET";

    public static void main(String[] args) {
        try {
            getDataSet(DATA_DIR + "/companies-names-industries.csv");
            int total = 0;
            for (Company company : getTickers(DATA_DIR + "/cik_ticker.csv")) {
                //System.err.println(mapperObj.writeValueAsString(company));
                if (getStock(company)) {
                    getTimeSeriese(company, TimeSeries.Type.quartly);
                    getTimeSeriese(company, TimeSeries.Type.instant);
                    getTimeSeriese(company, TimeSeries.Type.yearly);
                }
                if (total++ > 10) {
                    break;
                }
            }

            for (Company company : companyMap.values()) {
                System.err.println(company);
                List<TimeSeries> list = series.get(company.getCentralIndexKey());
                if (list != null) {
                    for (TimeSeries ts : list) {
                        System.err.println(ts);
                    }
                }
            }
        } catch (IOException ex) {
            sLogger.error("Failed to harvest data", ex);
        }
    }

    public static void getTimeSeriese(Company company, TimeSeries.Type type) throws IOException {
        String filename = company.getCentralIndexKey() + "-" + type + ".csv";
        File file = new File(DATA_DIR + "/companies/" + filename);
        Map<Integer, Date> dates = new HashMap<>();
        if (file.exists()) {
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n").withDelimiter(',');
            CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), csvFileFormat);
            for (CSVRecord csvRecord : parser) {
                if (csvRecord.getRecordNumber() > 1) {
                    List<TimeSeries> list = series.get(company.getCentralIndexKey());
                    if (list == null) {
                        list = new ArrayList<>();
                        series.put(company.getCentralIndexKey(), list);
                    }
                    TimeSeries ts = new TimeSeries(company.getCentralIndexKey(), csvRecord.get(0), type);
                    createTimeSeriesValues(ts, dates, csvRecord);
                    list.add(ts);
                } else {
                    for (int i = 1; i < csvRecord.size(); i++) {
                        try {
                            dates.put(i, dataformat.parse(csvRecord.get(i)));
                        } catch (ParseException ex) {
                            sLogger.info("Failed to parse record " + csvRecord);

                        }
                    }
                }
            }
        }
    }

    public static boolean getStock(Company company) {
        try {
            if (company.getStockTicker() == null) {
                return false;
            }
            URL url = new URL(quoteUrl + company.getStockTicker());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuilder result = new StringBuilder();
            conn.setRequestMethod(HTTP_GET);
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            }

            Stock stock = toStock(result.toString());
            if (stock != null) {
                stock.setCentralIndexKey(company.getCentralIndexKey());
            }
            return true;
        } catch (Exception ex) {
            sLogger.error("Failed to get stock for " + company.getName(), ex);
            return false;
        }
    }

    public static List<Company> getDataSet(String filename) throws IOException {
        File file = new File(filename);
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING).withRecordSeparator("\n").withDelimiter(',');
        CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), csvFileFormat);
        List<Company> companies = new ArrayList<>();
        for (CSVRecord csvRecord : parser) {
            if (csvRecord.getRecordNumber() > 1) {
                //System.err.println(csvRecord);
                Company company = new Company();
                company.setCentralIndexKey(Integer.parseInt(csvRecord.get(0)));
                company.setName(csvRecord.get(1));
                //2 for old names
                company.setNaiscName(csvRecord.get(3));
                if (csvRecord.get(4).length() > 0) {
                    company.setNaiscCode(Integer.valueOf(csvRecord.get(4)));
                }
                if (csvRecord.get(7).length() > 0) {
                    company.setSicCode(Integer.valueOf(csvRecord.get(7)));
                }
                companies.add(company);
                companyMap.put(company.getCentralIndexKey(), company);
            }
        }
        return companies;
    }

    public static Collection<Company> getTickers(String filename) throws IOException {
        File file = new File(filename);
        //CIK|Ticker|Name|Exchange|SIC|Business|Incorporated|IRS
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(COMPANY_TICKER_HEADER_MAPPING).withRecordSeparator("\n").withDelimiter('|');
        CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), csvFileFormat);

        for (CSVRecord csvRecord : parser) {
            if (csvRecord.getRecordNumber() > 1) {
                Integer key = Integer.parseInt(csvRecord.get(0));
                Company company = companyMap.get(key);
                if (company != null) {
                    company.setStockTicker(csvRecord.get(1));
                    company.setState(csvRecord.get(5));
                    company.setExchange(csvRecord.get(3));
                    company.setIncorporated(csvRecord.get(6));
                }
            }
        }
        return companyMap.values();
    }

    public static Stock toStock(String json) {
        try {
            DateFormat df = new SimpleDateFormat("E MMM d HH:mm:ss zXXX yyyy");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.setDateFormat(df);
            return objMapper.readValue(json, Stock.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void createTimeSeriesValues(TimeSeries ts, Map<Integer, Date> dates, CSVRecord csvRecord) {

        for (int i : dates.keySet()) {
            String strValue = csvRecord.get(i);
            float value = Float.MIN_VALUE;
            if (!strValue.isEmpty()) {
                value = Float.parseFloat(strValue);
            }
            ts.addValue(dates.get(i), value);
        }
    }
}
