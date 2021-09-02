package com.dto;

import com.pojo.SalesPojo;
import com.pojo.SkuPojo;
import com.service.*;
import com.util.DatatypeConversion;
import com.util.FileUtil;
import com.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
        if (checkFileHeading(dataRow) == false) {
            throw new ApiException("File orientation is not proper");
        }
        boolean errors = scanFileForErrors(file);
        if (errors) {
            throw new ApiException("File contains some errors.");
        }
        dataRow = TSVFile.readLine();

        while (dataRow != null) {
            salesService.add(convertRowsToPojo(dataRow));
            dataRow = TSVFile.readLine(); // Read next line of data.
        }
    }

    public void selectAll(HttpServletResponse response) throws IOException {
        List<SalesPojo> salesPojo = salesService.selectAll();
        FileWriter fos = new FileWriter("files/downloads/sku.txt",false);
        PrintWriter dos = new PrintWriter(fos);
        dos.println("Date\tSku Code\tStore\tQuantity\tDiscount\tRevenue");
        for(SalesPojo s:salesPojo){
            dos.println(s.getDate().getDayOfMonth()+"/"+s.getDate().getMonthValue()+"/"+s.getDate().getYear()+'\t'+skuService.selectById(s.getSkuId())+'\t'+storeService.selectById(s.getStoreId())+'\t'+s.getQuantity()+'\t'+s.getDiscount()+'\t'+s.getRevenue());
        }
        fos.close();
        FileUtil.downloadFile("downloads/sku",response);
    }

    public void downloadErrors(HttpServletResponse response) throws ApiException, IOException {
        File file = new File("C:\\projects\\Iris\\files\\error-files\\sales-error.txt");
        if (file.exists() == false) {
            throw new ApiException("Upload a file first.");
        }
        FileUtil.downloadFile("error-files/sales-error", response);
    }

    public boolean scanFileForErrors(MultipartFile file) throws IOException {
        BufferedReader TSVFile = new BufferedReader(new
                InputStreamReader(file.getInputStream(), "UTF-8"));
        boolean ans = false;
        String dataRow = TSVFile.readLine();
        int rowNumber = 0;
        refreshFile();
        dataRow = TSVFile.readLine();
        FileWriter fos = new FileWriter("files/error-files/sales-error.txt", true);
        PrintWriter dos = new PrintWriter(fos);
        while (dataRow != null) {
            try {
                SalesPojo dataConverted = convertRowsToPojo(dataRow);
                check(dataConverted);
            } catch (ApiException e) {
                dos.println(rowNumber + "\t" + dataRow + "\t" + e.getMessage());
                ans = true;
            }
            rowNumber++;
            dataRow = TSVFile.readLine(); // Read next line of data.
        }
        fos.close();
        return ans;
    }

    private void check(SalesPojo dataConverted) throws ApiException {
        if (dataConverted.getDate().isAfter(LocalDate.now())) {
            throw new ApiException("Sales date cannot be ahead of today's date");
        }
        if (dataConverted.getRevenue() < 0 || dataConverted.getDiscount() < 0 || dataConverted.getQuantity() < 0) {
            throw new ApiException("Revenue,Discount or Quantity is negative.");
        }
    }

    private void refreshFile() throws IOException {
        FileWriter fos = new FileWriter("files/error-files/sales-error.txt", false);
        PrintWriter dos = new PrintWriter(fos);
        dos.println("Row Number\tDate\tSKU\tBranch\tQuantity\tDiscount\tRevenue\tError Message");
        fos.close();
    }

    private SalesPojo convertRowsToPojo(String dataRow) throws ApiException {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        SalesPojo salesPojo = new SalesPojo();
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        salesPojo.setDate(convertStringTodate(dataArray.get(0)));
        salesPojo.setSkuId(skuService.select(StringUtil.toLowerCaseTrim(dataArray.get(1))));
        salesPojo.setStoreId(storeService.select(StringUtil.toLowerCaseTrim(dataArray.get(2))));
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

    private LocalDate convertStringTodate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }
}
