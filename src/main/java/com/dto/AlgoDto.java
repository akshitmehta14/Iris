package com.dto;

import com.model.*;
import com.pojo.*;
import com.service.*;
import com.util.DateUtil;
import com.util.NumberUtil;
import javafx.util.Pair;
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
            if (clubbing.containsKey(catSubCat)) {
                SizeIdentificationData modify = clubbing.get(catSubCat);
                modify.setRevenue(modify.getRevenue() + salesData.getRevenue());
                clubbing.put(catSubCat, modify);
            } else {
                SizeIdentificationData data = new SizeIdentificationData();
                data.setSubCategory(salesData.getSubCategory());
                data.setCategory(salesData.getCategory());
                clubbing.put(catSubCat, data);
            }
        }

        // {Cat, Subcat, Size} -> GoodSizePojo
        HashMap<Pair<String, Pair<String, String>>, GoodSizesPojo> finalIdentification = new HashMap<Pair<String, Pair<String, String>>, GoodSizesPojo>();

        // 1. Calculate perc of each size
        for (SalesData salesData : cleanedSales) {
            Pair<String, String> catSubCat = new Pair<String, String>(salesData.getCategory(), salesData.getSubCategory());
            Pair<String, Pair<String, String>> sizeCat = new Pair<String, Pair<String, String>>(salesData.getSize(), catSubCat);
            GoodSizesPojo data = new GoodSizesPojo();
            if (finalIdentification.containsKey(sizeCat)) {
                data = finalIdentification.get(sizeCat);
                double addition = salesData.getRevenue() * 100 / clubbing.get(catSubCat).getRevenue();
                data.setRevContri(data.getRevContri() + addition);
            } else {
                data.setSize(salesData.getSize());
                data.setSubCategory(salesData.getSubCategory());
                data.setCategory(salesData.getCategory());
                data.setRevContri(salesData.getRevenue() * 100 / clubbing.get(catSubCat).getRevenue());
            }

            if (data.getRevContri() >= goodSize) {
                data.setTypeOfSizes("Good Size");
            } else if (data.getRevContri() <= badSize) {
                data.setTypeOfSizes("Bad Size");
            } else {
                data.setTypeOfSizes("Average Size");
            }
            finalIdentification.put(sizeCat, data);
        }

    }

    //
    private void noosReport(List<SalesData> cleanedSales) {

        // key -> category
        HashMap<String, NoosData> noosDataMap = new HashMap<String, NoosData>();
//        cleanedSales = cleanedSales.stream().filter(sale -> sale.getDate().isAfter(LocalDate.MIN)).collect(Collectors.toList());

        // 1. aggregate revenue to cat level
        // 2. first sale day & last sale day for each category
        for (SalesData salesData : cleanedSales) {
            NoosData noosData = noosDataMap.computeIfAbsent(salesData.getCategory(), o -> new NoosData());
            noosData.setRevenue(noosData.getRevenue() + salesData.getRevenue());
            noosData.setFirstSaleDay(getFirstSaleDay(noosData, salesData));
            noosData.setLastSaleDay(getLastSaleDay(noosData, salesData));

//            if (noosDataMap.containsKey(salesData.getCategory())) {
//                NoosData helper = noosDataMap.get(salesData.getCategory());
//                helper.setRevenue(helper.getRevenue() + salesData.getRevenue());
//                if (helper.getFirstSaleDay().isAfter(salesData.getDate())) {
//                    helper.setFirstSaleDay(salesData.getDate());
//                }
//                if (helper.getLastSaleDay().isBefore(salesData.getDate())) {
//                    helper.setLastSaleDay(salesData.getDate());
//                }
//                noosDataMap.put(salesData.getCategory(), helper);
//            } else {
//                NoosData helper = new NoosData();
//                helper.setRevenue(salesData.getRevenue());
//                helper.setFirstSaleDay(salesData.getDate());
//                helper.setLastSaleDay(salesData.getDate());
//                noosDataMap.put(salesData.getCategory(), helper);
//            }
        }

        HashMap<Pair<String, String>, NoosPojo> finalNoos = new HashMap<Pair<String, String>, NoosPojo>();

        // 1. calculate style rev contri
        // 2. calculate style ros
        for (SalesData salesData : cleanedSales) {
            Pair<String, String> catStyleCode = new Pair<String, String>(salesData.getCategory(), salesData.getStyleCode());
            if (finalNoos.containsKey(catStyleCode)) {
                NoosPojo data = finalNoos.get(catStyleCode);
                double addition = salesData.getRevenue() * 100 / noosDataMap.get(salesData.getCategory()).getRevenue();
                data.setStyleRevContri(data.getStyleRevContri() + addition);
                double additionInRos = salesData.getQuantity() / DateUtil.differenceInDays(noosDataMap.get(salesData.getCategory()).getFirstSaleDay(), noosDataMap.get(salesData.getCategory()).getLastSaleDay());
                data.setStyleRevContri(data.getStyleRos() + additionInRos);
            } else {
                NoosPojo data = new NoosPojo();
                data.setCategory(salesData.getCategory());
                data.setStyleRos(salesData.getQuantity() / (DateUtil.differenceInDays(noosDataMap.get(salesData.getCategory()).getFirstSaleDay(), noosDataMap.get(salesData.getCategory()).getLastSaleDay()) + 1));
                data.setStyleCode(salesData.getStyleCode());
                data.setStyleRevContri(salesData.getRevenue() * 100 / noosDataMap.get(salesData.getCategory()).getRevenue());
                finalNoos.put(catStyleCode, data);
            }
        }
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

//            if (liquidationMap.containsKey(catSubCatKey)) {
//                LiquidationData modify = liquidationMap.get(catSubCatKey);
//                modify.setAvgDiscount(((sale.getQuantity() * sale.getDiscount()) + (modify.getAvgDiscount() * modify.getQuantity())) / (sale.getQuantity() + modify.getQuantity()));
//                modify.setRevenue(modify.getRevenue() + sale.getRevenue());
//                modify.setQuantity(modify.getQuantity() + sale.getQuantity());
//                liquidationMap.put(catSubCatKey,modify);
//            } else {
//                LiquidationData data = new LiquidationData();
//                data.setAvgDiscount(sale.getDiscount());
////                data.setCategory(sale.getCategory());
////                data.setSubCategory(sale.getSubCategory());
//                data.setRevenue(sale.getRevenue());
//                data.setQuantity(sale.getQuantity());
////                data.setAvgCleanedDiscount(0);
////                data.setCleanedRevenue(0);
////                data.setCleanedQuantity(0);
//                liquidationMap.put(catSubCatKey,data);
//            }
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
        for (Map.Entry mapElement : liquidationMap.entrySet()) {
            LiquidationData helper = (LiquidationData) mapElement.getValue();
            dos.println(helper.getCategory() + '\t' + helper.getSubCategory() + '\t' + helper.getCleanedRevenue() + "\t" + helper.getAvgCleanedDiscount());
        }
        fos.close();
        for (SalesData it : cleanedSales) {
            System.out.println(it.getRevenue());
        }
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

    private AlgoInputPojo convertFormToPojo(InputForm form) {
        AlgoInputPojo inputs = new AlgoInputPojo();
        inputs.setLiquidationMultiplier(form.getLiquidationMultiplier());
        inputs.setDate(form.getDate());
        inputs.setBadSize(form.getBadSize());
        inputs.setGoodSize(form.getGoodSize());
        return inputs;
    }

}

