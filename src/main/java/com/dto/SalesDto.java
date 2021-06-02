package com.dto;

import com.pojo.SalesPojo;
import com.service.*;
import com.util.DatatypeConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class SalesDto {
    @Autowired
    private SalesService salesService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private StyleService styleService;

    public void add(@RequestPart MultipartFile file) throws IOException, ApiException {
        BufferedReader TSVFile = new BufferedReader(new
                InputStreamReader(file.getInputStream(), "UTF-8"));
        String dataRow = TSVFile.readLine(); // Read first line.
        int rowNumber = 0;

        while (dataRow != null) {
            if (rowNumber == 0) {
                if (checkFileHeading(dataRow) == false) {
                    throw new ApiException("File orientation is not proper");
                }
            } else {
                try {
                    salesService.add(convertRowsToPojo(dataRow));
                }
                catch (ApiException e){

                }
            }
            dataRow = TSVFile.readLine(); // Read next line of data.
            rowNumber++;
        }
    }

    private SalesPojo convertRowsToPojo(String dataRow) throws ApiException {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        SalesPojo salesPojo = new SalesPojo();
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        salesPojo.setDate(convertStringTodate(dataArray.get(0)));
        salesPojo.setSkuId(skuService.select(dataArray.get(1)));
        salesPojo.setStoreId(storeService.select(dataArray.get(2)));
        salesPojo.setDiscount(DatatypeConversion.convertStringToDouble(dataArray.get(4)));
        salesPojo.setRevenue(DatatypeConversion.convertStringToDouble(dataArray.get(5)));
        salesPojo.setQuantity(DatatypeConversion.convertStringToInteger(dataArray.get(3)));
        return salesPojo;
    }

    private boolean checkFileHeading(String dataRow) {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        if (!dataArray.get(2).equals("Branch") || !dataArray.get(1).equals("SKU") || !dataArray.get(3).equals("Quantity") || !dataArray.get(0).equals("Date") || !dataArray.get(4).equals("Discount") || !dataArray.get(5).equals("Revenue")) {
            return false;
        }
        return true;
    }

    private LocalDate convertStringTodate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }
}
