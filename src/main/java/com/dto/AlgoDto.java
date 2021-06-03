package com.dto;

import com.model.*;
import com.pojo.*;
import com.service.*;
import com.util.DateUtil;
import com.util.NumberUtil;
import javafx.util.Pair;
import org.hibernate.engine.jdbc.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AlgoDto {
    @Autowired
    private AlgoService algoService;
    @Autowired
    private SalesService salesService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private StyleService styleService;
    @Autowired
    private ReportService reportService;

    public void addParameters(InputForm input) throws ApiException {
        checkParameters(input);
        AlgoInputPojo inputPojo = convertFormToPojo(input);
        algoService.addParameters(inputPojo);
    }

    public void algoRun() throws IOException {
        AlgoInputPojo input = algoService.selectRecent();
        System.out.println(input.getLiquidationMultiplier());
        List<SalesPojo> list = salesService.selectAll();
        List<SalesData> salesData = convertIntoSalesData(list);
        List<SalesData> cleanedSales = liquidationCleanup(salesData, input.getLiquidationMultiplier());
        noosReport(cleanedSales);
        goodSizes(cleanedSales, input.getGoodSize(), input.getBadSize());
    }

    private void goodSizes(List<SalesData> cleanedSales, double goodSize, double badSize) {

        // key -> cat, subCat
        HashMap<Pair<String, String>, SizeIdentificationData> clubbing = new HashMap<Pair<String, String>, SizeIdentificationData>();

        // 1. Aggregating revenue at cat - subCat
        for (SalesData salesData : cleanedSales) {
            Pair<String, String> catSubCat = new Pair<String, String>(salesData.getCategory(), salesData.getSubCategory());
            SizeIdentificationData helper = clubbing.computeIfAbsent(catSubCat,o-> new SizeIdentificationData());
            helper.setCategory(salesData.getCategory());
            helper.setSubCategory(salesData.getSubCategory());
            helper.setRevenue(helper.getRevenue()+ salesData.getRevenue());
        }

        // {Cat, Subcat, Size} -> GoodSizePojo
        HashMap<Pair<String, Pair<String, String>>, GoodSizesPojo> finalIdentification = new HashMap<Pair<String, Pair<String, String>>, GoodSizesPojo>();

        // 1. Calculate perc of each size
        for (SalesData salesData : cleanedSales) {
            Pair<String, String> catSubCat = new Pair<String, String>(salesData.getCategory(), salesData.getSubCategory());
            Pair<String, Pair<String, String>> sizeCat = new Pair<String, Pair<String, String>>(salesData.getSize(), catSubCat);
            GoodSizesPojo helper = finalIdentification.computeIfAbsent(sizeCat,o-> new GoodSizesPojo());
            helper.setCategory(salesData.getCategory());
            helper.setSize(salesData.getSize());
            helper.setSubCategory(salesData.getSubCategory());
            double addition = salesData.getRevenue() * 100 / clubbing.get(catSubCat).getRevenue();
            helper.setRevContri(helper.getRevContri()+addition);
            if (helper.getRevContri() >= goodSize) {
                helper.setTypeOfSizes("Good Size");
            } else if (helper.getRevContri() <= badSize) {
                helper.setTypeOfSizes("Bad Size");
            } else {
                helper.setTypeOfSizes("Average Size");
            }
            finalIdentification.put(sizeCat, helper);
        }

        reportService.deleteIdentification();
        for (Map.Entry mapElement : finalIdentification.entrySet()) {
            GoodSizesPojo helper = (GoodSizesPojo) mapElement.getValue();
            reportService.addIdentification(helper);
        }

    }

    //
    private void noosReport(List<SalesData> cleanedSales) throws IOException {

        // key -> category
        HashMap<String, NoosData> noosCategoryDataMap = new HashMap<String, NoosData>();
        HashMap<String, NoosData> noosStyleDataMap = new HashMap<String, NoosData>();
//        cleanedSales = cleanedSales.stream().filter(sale -> sale.getDate().isAfter(LocalDate.MIN)).collect(Collectors.toList());

        // 1. aggregate revenue to cat level
        // 2. first sale day & last sale day for each category
        for (SalesData salesData : cleanedSales) {
            NoosData noosData = noosCategoryDataMap.computeIfAbsent(salesData.getCategory(), o -> new NoosData());
            noosData.setRevenue(noosData.getRevenue() + salesData.getRevenue());
            noosData.setFirstSaleDay(getFirstSaleDay(noosData, salesData));
            noosData.setLastSaleDay(getLastSaleDay(noosData, salesData));

            NoosData noosStyle = noosStyleDataMap.computeIfAbsent(salesData.getStyleCode(), o-> new NoosData());
            noosStyle.setRevenue(noosStyle.getRevenue() + salesData.getRevenue());
            noosStyle.setFirstSaleDay(getFirstSaleDay(noosStyle, salesData));
            noosStyle.setLastSaleDay(getLastSaleDay(noosStyle, salesData));
        }

        HashMap< String, NoosPojo> categoryNoos = new HashMap< String, NoosPojo>();
        // For category ROS
        // 1. calculate style rev contri
        // 2. calculate style ros
        for (SalesData salesData : cleanedSales) {
            NoosPojo noosPojo = categoryNoos.computeIfAbsent(salesData.getCategory(),o-> new NoosPojo());
            double addition = salesData.getRevenue() * 100 / noosCategoryDataMap.get(salesData.getCategory()).getRevenue();
            noosPojo.setStyleRevContri(noosPojo.getStyleRevContri() + addition);
            double additionInRos = salesData.getQuantity() / (DateUtil.differenceInDays(noosCategoryDataMap.get(salesData.getCategory()).getFirstSaleDay(), noosCategoryDataMap.get(salesData.getCategory()).getLastSaleDay())+1);
            noosPojo.setStyleRos(noosPojo.getStyleRos() + additionInRos);
            noosPojo.setCategory(salesData.getCategory());
            noosPojo.setStyleCode(salesData.getStyleCode());
        }

        HashMap< String, NoosPojo> finalNoos = new HashMap< String, NoosPojo>();
        // 1. calculate style rev contri
        // 2. calculate style ros
        for (SalesData salesData : cleanedSales) {
            NoosPojo noosPojo = finalNoos.computeIfAbsent(salesData.getStyleCode(),o-> new NoosPojo());
            double addition = salesData.getRevenue() * 100 / noosStyleDataMap.get(salesData.getStyleCode()).getRevenue();
            noosPojo.setStyleRevContri(noosPojo.getStyleRevContri() + addition);
            double additionInRos = salesData.getQuantity() / (DateUtil.differenceInDays(noosStyleDataMap.get(salesData.getStyleCode()).getFirstSaleDay(), noosStyleDataMap.get(salesData.getStyleCode()).getLastSaleDay())+1);
            noosPojo.setStyleRos(noosPojo.getStyleRos() + additionInRos);
            noosPojo.setCategory(salesData.getCategory());
            noosPojo.setStyleCode(salesData.getStyleCode());
        }

        FileWriter fos = new FileWriter("files/algo-files/NOOS_Report.txt", false);
        PrintWriter dos = new PrintWriter(fos);
        reportService.deleteNoos();
        for (Map.Entry mapElement : finalNoos.entrySet()) {
            NoosPojo helper = (NoosPojo) mapElement.getValue();
            if(helper.getStyleRos() >= categoryNoos.get(helper.getCategory()).getStyleRos()){
                dos.println(helper.getCategory()+"\t"+helper.getStyleCode()+"\t"+helper.getStyleRevContri()+"\t"+helper.getStyleRos());
                reportService.addNoos(helper);
            }
        }
        fos.close();
    }

    private LocalDate getFirstSaleDay(NoosData noosData, SalesData salesData) {
        if (noosData.getFirstSaleDay() == null)
            return salesData.getDate();

        return salesData.getDate().isBefore(noosData.getFirstSaleDay()) ? salesData.getDate() : noosData.getFirstSaleDay();
    }

    private LocalDate getLastSaleDay(NoosData noosData, SalesData salesData) {
        if (noosData.getLastSaleDay() == null)
            return salesData.getDate();

        return salesData.getDate().isAfter(noosData.getLastSaleDay()) ? salesData.getDate() : noosData.getLastSaleDay();
    }

    private List<SalesData> liquidationCleanup(List<SalesData> salesData, double multiplier) throws IOException {
        List<SalesData> cleanedSales = new ArrayList<SalesData>();
        HashMap<Pair<String, String>, LiquidationData> liquidationMap = new HashMap<Pair<String, String>, LiquidationData>();

        for (SalesData sale : salesData) {
            Pair<String, String> catSubCatKey = new Pair<String, String>(sale.getCategory(), sale.getSubCategory());
            LiquidationData data = liquidationMap.computeIfAbsent(catSubCatKey, o -> new LiquidationData(sale.getCategory(), sale.getSubCategory()));

            data.setAvgDiscount(((sale.getQuantity() * sale.getDiscount()) + (data.getAvgDiscount() * data.getQuantity())) / (sale.getQuantity() + data.getQuantity()));
            data.setRevenue(data.getRevenue() + sale.getRevenue());
            data.setQuantity(data.getQuantity() + sale.getQuantity());
        }

        for (SalesData sale : salesData) {
            Pair<String, String> catSubCat = new Pair<String, String>(sale.getCategory(), sale.getSubCategory());
            LiquidationData catSubCatData = liquidationMap.get(catSubCat);

            if (sale.getDiscount() <= catSubCatData.getAvgDiscount() * multiplier / 100) {
                cleanedSales.add(sale);
                // TODO IMP maybe a new variable cleanedQty
                catSubCatData.setQuantity(catSubCatData.getQuantity() + sale.getQuantity());
            } else {
                catSubCatData.setAvgCleanedDiscount(((sale.getDiscount() * sale.getQuantity()) + (catSubCatData.getCleanedQuantity() * catSubCatData.getAvgCleanedDiscount())) / (sale.getQuantity() + catSubCatData.getCleanedQuantity()));
                catSubCatData.setCleanedQuantity(catSubCatData.getCleanedQuantity() + sale.getQuantity());
                catSubCatData.setCleanedRevenue(sale.getRevenue() + catSubCatData.getCleanedRevenue());
            }
            liquidationMap.put(catSubCat, catSubCatData);
        }

        FileWriter fos = new FileWriter("files/algo-files/Liquidation_Cleanup_Report.txt", false);
        PrintWriter dos = new PrintWriter(fos);
        reportService.deleteLiquidation();;
        for (Map.Entry mapElement : liquidationMap.entrySet()) {
            LiquidationData helper = (LiquidationData) mapElement.getValue();
            dos.println(helper.getCategory() + '\t' + helper.getSubCategory() + '\t' + helper.getCleanedRevenue() + "\t" + helper.getAvgCleanedDiscount());
            reportService.addLiquidation(convertDataToPojo(helper));
        }
        fos.close();
        for (SalesData it : cleanedSales) {
            System.out.println(it.getRevenue());
        }
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSource.getDataSource());
//        String dataFilepath = "C:\\projects\\Iris\\files\\algo-files\\Liquidation-Cleanup-Report.txt";
//        String tableName = "liquidationpojo";
//        String sql = "LOAD DATA LOCAL INFILE '" + dataFilepath + "' into table " + tableName;
//
//        jdbcTemplate.execute(sql);

        return cleanedSales;
    }

    private List<SalesData> convertIntoSalesData(List<SalesPojo> list) {
        List<SalesData> converted = new ArrayList<SalesData>();
        HashMap<Integer, SkuPojo> skuMap = skuService.selectAllMap();
        HashMap<Integer, StylePojo> styleMap = styleService.selectAllMap();
        for (SalesPojo p : list) {
            SkuPojo skuPojo = skuMap.get(p.getSkuId());
            SalesData data = new SalesData();
            data.setQuantity(p.getQuantity());
            data.setDate(p.getDate());
            data.setDiscount(p.getDiscount() * 100 / (p.getRevenue() + p.getDiscount()));
            data.setSize(skuPojo.getSize());
            data.setRevenue(p.getRevenue());
            data.setCategory(styleMap.get(skuPojo.getStyleId()).getCategory());
            data.setSubCategory(styleMap.get(skuPojo.getStyleId()).getSubCategory());
            data.setStyleCode(styleMap.get(skuPojo.getStyleId()).getStyleCode());
            converted.add(data);
            System.out.println(data.getCategory() + '\t' + data.getSubCategory() + '\t' + data.getSize() + '\t' + data.getDiscount());
        }
        return converted;
    }

    private void checkParameters(InputForm input) throws ApiException {
        if (NumberUtil.greaterThan100(input.getLiquidationMultiplier()) || NumberUtil.greaterThan100(input.getGoodSize()) || NumberUtil.greaterThan100(input.getBadSize())) {
            throw new ApiException("Percentages cannot be greater than 100.");
        }
        if (NumberUtil.negative(input.getLiquidationMultiplier()) || NumberUtil.negative(input.getGoodSize()) || NumberUtil.negative(input.getBadSize())) {
            throw new ApiException("Percentages cannot be negative.");
        }
        if (input.getDate().isAfter(LocalDate.now())) {
            throw new ApiException("Cannot run algo for date after today's date");
        }
    }

    private LiquidationPojo convertDataToPojo(LiquidationData data){
        LiquidationPojo converted = new LiquidationPojo();
        converted.setCategory(data.getCategory());
        converted.setSubCategory(data.getSubCategory());
        converted.setAvgDiscount(data.getAvgDiscount());
        converted.setAvgDiscountAfterCleanup(data.getAvgCleanedDiscount());
        converted.setRevenueCleanup(data.getCleanedRevenue());
        return converted;
    }

    private AlgoInputPojo convertFormToPojo(InputForm form) {
        AlgoInputPojo inputs = new AlgoInputPojo();
        inputs.setLiquidationMultiplier(form.getLiquidationMultiplier());
        inputs.setDate(form.getDate());
        inputs.setBadSize(form.getBadSize());
        inputs.setGoodSize(form.getGoodSize());
        return inputs;
    }

}

