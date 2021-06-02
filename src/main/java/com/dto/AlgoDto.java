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
        List<SalesData> cleanedSales = liquidationCleanup(salesData,input.getLiquidationMultiplier());
        noosReport(cleanedSales);
        goodSizes(cleanedSales,input.getGoodSize(),input.getBadSize());
    }

    private void goodSizes(List<SalesData> cleanedSales,double goodSize,double badSize) {
        HashMap<Pair<String,String>, SizeIdentificationHelper> clubbing = new HashMap<Pair<String,String>, SizeIdentificationHelper>();
        for(SalesData salesData:cleanedSales) {
            Pair<String, String> catSubCat = new Pair<String, String>(salesData.getCategory(), salesData.getSubCategory());
            if (clubbing.containsKey(catSubCat)) {
                SizeIdentificationHelper modify = clubbing.get(catSubCat);
                modify.setRevenue(modify.getRevenue()+ salesData.getRevenue());
                clubbing.put(catSubCat,modify);
            } else {
                SizeIdentificationHelper data = new SizeIdentificationHelper();
                data.setSubCategory(salesData.getSubCategory());
                data.setCategory(salesData.getCategory());
                data.setCategory(salesData.getCategory());
                clubbing.put(catSubCat,data);
            }
        }

        HashMap<Pair<String,Pair<String,String>>,GoodSizesPojo> finalIdentification = new HashMap<Pair<String, Pair<String, String>>, GoodSizesPojo>();
        for(SalesData salesData:cleanedSales) {
            Pair<String, String> catSubCat = new Pair<String, String>(salesData.getCategory(), salesData.getSubCategory());
            Pair<String,Pair<String,String>> sizeCat= new Pair<String,Pair<String,String>>(salesData.getSize(), catSubCat);
            GoodSizesPojo data = new GoodSizesPojo();
            if(finalIdentification.containsKey(sizeCat)){
                data = finalIdentification.get(sizeCat);
                double addition = salesData.getRevenue()*100/clubbing.get(catSubCat).getRevenue();
                data.setRevContri(data.getRevContri()+addition);
            }
            else{
                data.setSize(salesData.getSize());
                data.setSubCategory(salesData.getSubCategory());
                data.setCategory(salesData.getCategory());
                data.setRevContri(salesData.getRevenue()*100/clubbing.get(catSubCat).getRevenue());
            }

            if(data.getRevContri()>=goodSize){
                data.setTypeOfSizes("Good Size");
            }
            else if(data.getRevContri()<=badSize){
                data.setTypeOfSizes("Bad Size");
            }
            else{
                data.setTypeOfSizes("Average Size");
            }
            finalIdentification.put(sizeCat,data);
        }

    }
//
    private void noosReport(List<SalesData> cleanedSales) {
        HashMap<String, NoosHelper> noosRevenue = new HashMap<String, NoosHelper>();
        for(SalesData salesData:cleanedSales){
            if(noosRevenue.containsKey(salesData.getCategory())){
                NoosHelper helper = noosRevenue.get(salesData.getCategory());
                helper.setRevenue(helper.getRevenue()+ salesData.getRevenue());
                if(helper.getStart().isAfter(salesData.getDate())){
                    helper.setStart(salesData.getDate());
                }
                if(helper.getEnd().isBefore(salesData.getDate())){
                    helper.setEnd(salesData.getDate());
                }
                noosRevenue.put(salesData.getCategory(),helper);
            }
            else{
                NoosHelper helper = new NoosHelper();
                helper.setRevenue(salesData.getRevenue());
                helper.setStart(salesData.getDate());
                helper.setEnd(salesData.getDate());
                noosRevenue.put(salesData.getCategory(),helper);
            }
        }

        HashMap<Pair<String,String>, NoosPojo> finalNoos = new HashMap<Pair<String, String>, NoosPojo>();
        for(SalesData salesData:cleanedSales){
            Pair<String,String> catStyleCode = new Pair<String,String>(salesData.getCategory(), salesData.getStyleCode());
            if(finalNoos.containsKey(catStyleCode)){
                NoosPojo data = finalNoos.get(catStyleCode);
                double addition = salesData.getRevenue()*100/noosRevenue.get(salesData.getCategory()).getRevenue();
                data.setStyleRevContri(data.getStyleRevContri()+addition);
                double additionInRos = salesData.getQuantity()/DateUtil.differenceInDays(noosRevenue.get(salesData.getCategory()).getStart(),noosRevenue.get(salesData.getCategory()).getEnd());
                data.setStyleRevContri(data.getStyleRos()+additionInRos);
            }
            else{
                NoosPojo data = new NoosPojo();
                data.setCategory(salesData.getCategory());
                data.setStyleRos(salesData.getQuantity()/(DateUtil.differenceInDays(noosRevenue.get(salesData.getCategory()).getStart(),noosRevenue.get(salesData.getCategory()).getEnd())+1));
                data.setStyleCode(salesData.getStyleCode());
                data.setStyleRevContri(salesData.getRevenue()*100/noosRevenue.get(salesData.getCategory()).getRevenue());
                finalNoos.put(catStyleCode,data);
            }
        }
    }

    private List<SalesData> liquidationCleanup(List<SalesData> salesData,double multiplier) throws IOException {
        List<SalesData> cleanedSales = new ArrayList<SalesData>();
        HashMap<Pair<String,String>, LiquidationHelper> liquidation = new HashMap<Pair<String,String>, LiquidationHelper>();
        for(SalesData it:salesData) {
            Pair<String, String> catSubCat = new Pair<String, String>(it.getCategory(), it.getSubCategory());
            if (liquidation.containsKey(catSubCat)) {
                LiquidationHelper modify = liquidation.get(catSubCat);
                modify.setAvgDiscount(((it.getQuantity() * it.getDiscount()) + (modify.getAvgDiscount() * modify.getQuantity())) / (it.getQuantity() + modify.getQuantity()));
                modify.setRevenue(modify.getRevenue() + it.getRevenue());
                modify.setQuantity(modify.getQuantity() + it.getQuantity());
                liquidation.put(catSubCat,modify);
            } else {
                LiquidationHelper data = new LiquidationHelper();
                data.setAvgDiscount(it.getDiscount());
                data.setCategory(it.getCategory());
                data.setSubCategory(it.getSubCategory());
                data.setRevenue(it.getRevenue());
                data.setQuantity(it.getQuantity());
                data.setAvgCleanedDiscount(0);
                data.setCleanedRevenue(0);
                data.setCleanedQuantity(0);
                liquidation.put(catSubCat,data);
            }
        }

        for(SalesData it:salesData){
            Pair<String, String> catSubCat = new Pair<String, String>(it.getCategory(), it.getSubCategory());
            LiquidationHelper data = new LiquidationHelper();
            data = liquidation.get(catSubCat);
            if(it.getDiscount()<= data.getAvgDiscount()*multiplier/100){
                cleanedSales.add(it);
                data.setQuantity(data.getQuantity()+it.getQuantity());
            }
            else{
                data.setAvgCleanedDiscount(((it.getDiscount()*it.getQuantity())+(data.getCleanedQuantity()*data.getAvgCleanedDiscount()))/(it.getQuantity()+data.getCleanedQuantity()));
                data.setCleanedQuantity(data.getCleanedQuantity()+it.getQuantity());
                data.setCleanedRevenue(it.getRevenue()+data.getCleanedRevenue());
            }
            liquidation.put(catSubCat,data);
        }

        FileWriter fos = new FileWriter("files/algo-files/Liquidation_Cleanup_Report.txt",false);
        PrintWriter dos = new PrintWriter(fos);
        for (Map.Entry mapElement : liquidation.entrySet()) {
            LiquidationHelper helper = (LiquidationHelper) mapElement.getValue();

            dos.println(helper.getCategory()+'\t'+helper.getSubCategory()+'\t'+helper.getCleanedRevenue()+"\t"+helper.getAvgCleanedDiscount());
        }
        fos.close();
        for(SalesData it:cleanedSales){
            System.out.println(it.getRevenue());
        }
        return cleanedSales;
    }

    private List<SalesData> convertIntoSalesData(List<SalesPojo> list) {
        List<SalesData> converted = new ArrayList<SalesData>();
        HashMap<Integer, SkuPojo> skuMap = skuService.selectAllMap();
        HashMap<Integer, StylePojo> styleMap = styleService.selectAllMap();
        for(SalesPojo p:list){
            SkuPojo skuPojo = skuMap.get(p.getSkuId());
            SalesData data = new SalesData();
            data.setQuantity(p.getQuantity());
            data.setDate(p.getDate());
            data.setDiscount(p.getDiscount()*100/(p.getRevenue()+p.getDiscount()));
            data.setSize(skuPojo.getSize());
            data.setRevenue(p.getRevenue());
            data.setCategory(styleMap.get(skuPojo.getStyleId()).getCategory());
            data.setSubCategory(styleMap.get(skuPojo.getStyleId()).getSubCategory());
            data.setStyleCode(styleMap.get(skuPojo.getStyleId()).getStyleCode());
            converted.add(data);
            System.out.println(data.getCategory()+'\t'+data.getSubCategory()+'\t'+data.getSize()+'\t'+data.getDiscount());
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
        if(input.getDate().isAfter(LocalDate.now())){
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

